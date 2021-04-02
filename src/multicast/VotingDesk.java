package multicast;

import multicast.utils.LoggingFormatter;
import multicast.protocol.MulticastProtocol;
import multicast.protocol.MulticastPacket;
import multicast.ui.VotingDeskUI;

import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

// FIXME: If client sends greeting in the wrong group it will not work and the server will throw and exception

public class VotingDesk {

	// Globals / Defaults
	private static final String DEFAULT_MULTICAST_SERVER_NAME = "department";

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.1";
	private static final String DEFAULT_DISCOVERY_MULTICAST_PORT = "6789";

	private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.2";
	private static final String DEFAULT_VOTING_MULTICAST_PORT = "4321";

	private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());
	private final String config;

	// Attributes
	private String name;

	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	private final Hashtable<String, String> terminals;
	private final BlockingQueue<String> voters;
	private final BlockingQueue<String> availableTerminals;


	public VotingDesk(String configPath) {
		this.config = configPath;
		this.terminals = new Hashtable<>();
		this.voters = new LinkedBlockingQueue<>();
		this.availableTerminals = new LinkedBlockingQueue<>();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java " + VotingDesk.class.getName() + " {PROPERTIES_FILE}");
			return;
		}
		VotingDesk server = new VotingDesk(args[0]);
		server.start();
	}


	public Properties readPropertiesFile(String path) {
		Properties config = new Properties();
		try (InputStream is = new FileInputStream(path)) {
			config.load(is);
		} catch (IOException e) {
			LOGGER.severe("Could not read configurations file: " + e.getMessage());
			LOGGER.warning("Running server with default configs");
		}
		return config;
	}

	public void start() {
		setupLogger();

		Properties configs = readPropertiesFile(this.config);
		this.name = (String) configs.getOrDefault(
				"server.department.name",
				DEFAULT_MULTICAST_SERVER_NAME
		);

		try {
			this.statusGroup = InetAddress.getByName((String)
					configs.getOrDefault(
							"multicast.discovery.group",
							DEFAULT_MULTICAST_DISCOVERY_ADDRESS
					)
			);
		} catch (UnknownHostException e) {
			LOGGER.severe("Unknown Host: " + e.getMessage());
		}

		this.statusPort = Integer.parseInt((String)
				configs.getOrDefault(
						"multicast.discovery.port",
						DEFAULT_DISCOVERY_MULTICAST_PORT
				)
		);

		try {
			this.votingGroup = InetAddress.getByName((String)
					configs.getOrDefault(
							"multicast.voting.group",
							DEFAULT_MULTICAST_VOTING_ADDRESS
					)
			);
		} catch (UnknownHostException e) {
			LOGGER.severe("Unknown Host: " + e.getMessage());
		}

		this.votingPort = Integer.parseInt((String) configs.getOrDefault(
				"multicast.voting.port",
				DEFAULT_VOTING_MULTICAST_PORT
				)
		);

		// TODO: Connect VotingDesk to RMI
		votingDeskServerStartup();
	}


	public void enqueueVoter(String voter) {
		this.voters.add(voter);
		LOGGER.info("Enqueued voter with Citizen Card ID: " + voter);
	}

	public String getName() {
		return name;
	}

	private void setupLogger() {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LoggingFormatter.ConsoleFormatter());
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(handler);
		try {
			FileHandler logFile = new FileHandler("server" + LocalDate.now() + ".log");
			logFile.setFormatter(new LoggingFormatter.FileFormatter());
			LOGGER.addHandler(logFile);
			logFile.setLevel(Level.WARNING);
		} catch (IOException e) {
			LOGGER.severe("Log file creation failed: " + e.getMessage());
		}
	}

	private void votingDeskServerStartup() {
		try {
			this.statusSocket = new MulticastSocket(statusPort);
			this.votingSocket = new MulticastSocket(votingPort);

			statusSocket.joinGroup(statusGroup);
			votingSocket.joinGroup(votingGroup);

			LOGGER.info(this.getName() + " server running on " + statusSocket.getLocalAddress());
			LOGGER.info(this.getName() + " joined discovery multicast group " + statusGroup.getHostName()
						+ ", " + "port" + " " + statusPort);
			LOGGER.info(this.getName() + " joined voting multicast group " + votingGroup.getHostName()
						+ ", port " + votingPort);

			String voteManagerThread = this.getName() + "_VotingManager";
			VotingDeskVotingManager voteManager = new VotingDeskVotingManager(voteManagerThread);
			LOGGER.info("Creating VotingDeskVoteManager thread!");

			String terminalManagerThread = this.getName() + "_TerminalManager";
			VotingDeskTerminalManager terminalManager = new VotingDeskTerminalManager(terminalManagerThread,
					voteManagerThread);
			LOGGER.info("Creating VotingDeskTerminalManager thread!");

			String clientHandlerThread = this.getName() + "_ClientHandler";
			VotingDeskClientHandler clientHandler = new VotingDeskClientHandler(clientHandlerThread,
					voteManagerThread);
			LOGGER.info("Creating VotingDeskClientHandler thread!");

			terminalManager.start();
			voteManager.start();
			clientHandler.start();

			LOGGER.info("Starting VotingDesk console");
			VotingDeskUI console = new VotingDeskUI(this);
			console.addWindowCloseListener();
			console.open();

			synchronized (this) { this.wait(); }

			voteManager.interrupt();
			clientHandler.interrupt();
			terminalManager.interrupt();

			statusSocket.leaveGroup(statusGroup);
			votingSocket.leaveGroup(votingGroup);

			statusSocket.close();
			votingSocket.close();
		} catch (IOException e) {
			LOGGER.severe(this.getName() + " Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.severe(this.getName() + " server session interrupted!");
		}
		LOGGER.info(this.getName() + " server session terminated successfully!");
	}

	// Listen for terminals connecting && status, add them to the hashmap of existing terminals
	private class VotingDeskTerminalManager extends Thread {
		private final String handler;

		public VotingDeskTerminalManager(String name, String voterHandlerThread) {
			super(name);
			this.handler = voterHandlerThread;
		}

		@Override
		public void run() {
			LOGGER.info(this.getName() + " thread started!");

			try {
				MulticastPacket probe = MulticastProtocol.probe(this.getName());
				probe.sendTo(statusSocket, statusGroup, statusPort);
			} catch (IOException e) {
				LOGGER.info(this.getName() + " thread stopped, socket closed!");
			}

			while (!this.isInterrupted()) {
				try {
					MulticastPacket request = MulticastPacket.from(statusSocket, this.getName());
					String requestType = request.get("type");
					String target = request.get("source");

					switch (requestType) {

						case MulticastProtocol.GREETING:
							MulticastPacket acknowledge = MulticastProtocol.acknowledge(this.getName(), target);
							acknowledge.sendTo(statusSocket, statusGroup, statusPort);

							HashMap<String, String> info = new HashMap<>();
							info.put("MULTICAST_VOTING_ADDRESS", String.valueOf(votingGroup.getHostName()));
							info.put("MULTICAST_VOTING_PORT", String.valueOf(votingPort));

							boolean reconnected = false;
							if (terminals.get(target) != null && !terminals.get(target).equals("EMPTY")) {
								// TODO: fetch RMI info about this client
								String username = "Jarvardoc";
								info.put("id", terminals.get(target));
								info.put("username", username);
								info.put("vote-manager", handler);
								reconnected = true;
							}

							MulticastPacket reply = MulticastProtocol.itemList(this.getName(), target, info);
							reply.sendTo(statusSocket, statusGroup, statusPort);

							if (reconnected) {
								LOGGER.warning("Voting terminal " + target + " crashed while handling a client!");
								LOGGER.info("Voting terminal " + target + " reconnected!");
								LOGGER.info("Resuming client voting operation...");
							} else {
								terminals.put(target, "EMPTY");
								LOGGER.info("Voting terminal " + target + " connected!");
							}
							break;

						case MulticastProtocol.READY:
							availableTerminals.offer(target);
							LOGGER.info("Voting terminal " + target + " available!");
							break;

						case MulticastProtocol.OFFER:
							terminals.put(target, "EMPTY");
							availableTerminals.offer(target);
							LOGGER.info("Voting terminal " + target + " free!");
							break;

						case MulticastProtocol.GOODBYE:
							// TODO: Handle terminal shutdown properly
							LOGGER.info("Voting Terminal Disconnected");
							break;

					}
				} catch (IOException e) {
					LOGGER.info(this.getName() + " thread stopped, socket closed!");
				}
			}
		}
	}

	// ABOUT: receive voting related info and send back replies according to the message received
	private class VotingDeskVotingManager extends Thread {

		public VotingDeskVotingManager(String name) {
			super(name);
		}

		@Override
		public void run() {
			LOGGER.info(this.getName() + " thread started!");
			while (!this.isInterrupted()) {
				try {
					MulticastPacket request = MulticastPacket.from(votingSocket, this.getName());
					String requestSource = request.get("source");
					String requestType = request.get("type");

					if (requestType.equals(MulticastProtocol.VOTE)) {
						System.out.println(request);

						MulticastPacket ack = MulticastProtocol.acknowledge(this.getName(), requestSource);
						ack.sendTo(votingSocket, votingGroup, votingPort);
						System.out.println(ack);
						// TODO transmit vote to RMI server

					} else if (request.get("target").equals(this.getName()) &&
							   requestType.equals(MulticastProtocol.LOGIN)) {

						// TODO: authenticate in RMI server
						boolean loggedIn = true;

						MulticastPacket status =
								MulticastProtocol.status(this.getName(), requestSource, "logged-in");
						status.sendTo(votingSocket, votingGroup, votingPort);
						System.out.println(status);

						// TODO: fetch elections
						// TODO: get lists from RMI server
						HashMap<String, String> lists = new HashMap<>();
						lists.put("Are you blind?", "Choose one option");
						lists.put("1", "Yes");
						lists.put("2", "No");
						lists.put("3", "Perhaps");
						lists.put("4", "I am going to buy a brand new car");

						MulticastPacket options =
								MulticastProtocol.itemList(this.getName(), requestSource, lists);
						options.sendTo(votingSocket, votingGroup, votingPort);

					}

				} catch (IOException e) {
					LOGGER.info(this.getName() + " thread stopped, socket closed!");
				}
			}
		}
	}

	// ABOUT: Pop clients from the queue identify them and associate them with a terminal
// FIXME: Perhaps try to check if it is alive first and only then remove it. Going to just
//  remove it for now.
	private class VotingDeskClientHandler extends Thread {
		private static final int ACK_TIMEOUT_MS = 5000;

		private final String handler;

		public VotingDeskClientHandler(String name, String voterHandlerThread) {
			super(name);
			this.handler = voterHandlerThread;
		}

		@Override
		public void run() {
			LOGGER.info(this.getName() + " thread started!");
			try (MulticastSocket ackListenerSocket = new MulticastSocket(votingPort)) {
				ackListenerSocket.joinGroup(votingGroup);
				ackListenerSocket.setSoTimeout(ACK_TIMEOUT_MS);

				while (!this.isInterrupted()) {
					String voter = voters.take();
					LOGGER.info("Processing voter with Citizen Card ID: " + voter);

					// TODO: identify voter in RMI server
					boolean userExists = true;
					String username = "Jarvardoc";

					LOGGER.info("Voter with Citizen Card ID identified");
					boolean timeoutFlag = true;
					while (!this.isInterrupted() && userExists && timeoutFlag) {

						LOGGER.info("Waiting for terminal to be available");
						String terminal = availableTerminals.take();
						LOGGER.info("Terminal " + terminal + " requested by voter with Citizen Card ID: " + voter);

						HashMap<String, String> userItems = new HashMap<>();
						userItems.put("id", voter);
						userItems.put("username", username);
						userItems.put("vote-manager", handler);

						MulticastPacket packet = MulticastProtocol.itemList(this.getName(), terminal, userItems);
						packet.sendTo(votingSocket, votingGroup, votingPort);

						try {
							MulticastPacket ack = MulticastPacket.from(ackListenerSocket, this.getName());
							if (ack.get("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
								ack.get("target").equals(this.getName())) {
								terminals.put(terminal, voter);
								LOGGER.info("Voter with Citizen Card ID: " + voter + " assigned to terminal: " + terminal);
							}
							timeoutFlag = false;
						} catch (SocketTimeoutException e) {
							LOGGER.warning("Terminal " + terminal + " not responding and is probably down...");
							LOGGER.warning("Re-rooting voter with Citizen Card ID: " + voter + " to another terminal");
							terminals.remove(terminal);
							timeoutFlag = true;
						}
					}
				}
				ackListenerSocket.leaveGroup(votingGroup);
			} catch (IOException | InterruptedException e) {
				LOGGER.info(this.getName() + " thread stopped, socket closed!");
			}
		}
	}
}
