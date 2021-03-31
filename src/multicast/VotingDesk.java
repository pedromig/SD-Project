package multicast;

import multicast.utils.LoggingFormatter;
import multicast.protocol.MulticastProtocol;
import multicast.protocol.MulticastPacket;
import multicast.ui.VotingDeskUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

public class VotingDesk {

	// Globals / Defaults
	private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.1";
	private static final int DEFAULT_VOTING_MULTICAST_PORT = 6789;

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
	private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

	private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());

	// Attributes
	private final String name;

	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	private final Hashtable<String, String> terminals;
	private final BlockingQueue<String> voters;
	private final BlockingQueue<String> availableTerminals;

	public VotingDesk(String name) {
		this.name = name;
		this.terminals = new Hashtable<>();
		this.voters = new LinkedBlockingQueue<>();
		this.availableTerminals = new LinkedBlockingQueue<>();
	}

	public void enqueueVoter(String voter) {
		this.voters.add(voter);
		LOGGER.info("Enqueued voter with Citizen Card ID: " + voter);
	}

	private void setupLogger() {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LoggingFormatter.ConsoleFormatter());
		LOGGER.setUseParentHandlers(false);
		LOGGER.addHandler(handler);
		try {
			FileHandler logFile = new FileHandler(this.getName().toLowerCase() + LocalDate.now() + ".log");
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

			String terminalManagerThread = this.getName() + "_TerminalManager";
			VotingDeskTerminalManager terminalManager = new VotingDeskTerminalManager(terminalManagerThread);
			LOGGER.info("Creating VotingDeskTerminalManager thread!");

			String voteManagerThread = this.getName() + "_VotingManager";
			VotingDeskVotingManager voteManager = new VotingDeskVotingManager(voteManagerThread);
			LOGGER.info("Creating VotingDeskVoteManager thread!");

			String clientHandlerThread = this.getName() + "_ClientHandler";
			VotingDeskClientHandler clientHandler = new VotingDeskClientHandler(clientHandlerThread, voteManagerThread);
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

	public void start() {
		setupLogger();

		// TODO: Read config file && setup server (including attributes)
		try {
			this.statusGroup = InetAddress.getByName(DEFAULT_MULTICAST_DISCOVERY_ADDRESS);
			this.statusPort = DEFAULT_DISCOVERY_MULTICAST_PORT;

			this.votingGroup = InetAddress.getByName(DEFAULT_MULTICAST_VOTING_ADDRESS);
			this.votingPort = DEFAULT_VOTING_MULTICAST_PORT;

		} catch (UnknownHostException e) {
			LOGGER.severe("Unknown Host Exception caught");
		}

		// TODO: Connect VotingDesk to RMI
		votingDeskServerStartup();
	}

	public String getName() {
		return name;
	}

	// Listen for terminals connecting && status, add them to the hashmap of existing terminals
	private class VotingDeskTerminalManager extends Thread {

		public VotingDeskTerminalManager(String name) {
			super(name);
		}

		@Override
		public void run() {
			LOGGER.info(this.getName() + " thread started!");
			while (!this.isInterrupted()) {
				try {
					MulticastPacket request = MulticastPacket.from(statusSocket, this.getName());
					String requestType = request.getItem("type");

					switch (requestType) {

						case MulticastProtocol.GREETING:
							String target = request.getItem("source");

							MulticastPacket acknowledge = MulticastProtocol.acknowledge(this.getName(), target);
							acknowledge.sendTo(statusSocket, statusGroup, statusPort);

							HashMap<String, String> info = new HashMap<>();
							info.put("MULTICAST_VOTING_ADDRESS", String.valueOf(votingGroup.getHostName()));
							info.put("MULTICAST_VOTING_PORT", String.valueOf(votingPort));

							MulticastPacket reply = MulticastProtocol.itemList(this.getName(), target, info);
							reply.sendTo(statusSocket, statusGroup, statusPort);

							terminals.put(target, "EMPTY");
							LOGGER.info("Voting terminal " + target + " connected!");
							availableTerminals.offer(target);
							LOGGER.info("Voting terminal " + target + " available!");
							break;

						case MulticastProtocol.GOODBYE:
							// TODO: Handle terminal shutdown properly

							LOGGER.info("Voting Terminal Disconnected");
							break;

						case MulticastProtocol.OFFER:
							// TODO terminal available

							LOGGER.info("Voting Terminal Offer");
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

			try {
				MulticastPacket request = MulticastPacket.from(statusSocket, this.getName());
				String requestType = request.getItem("type");


			} catch (IOException e) {
				LOGGER.info(this.getName() + " thread stopped, socket closed!");
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
					LOGGER.info("Voter with Citized Card ID identified");

					boolean timeoutFlag = true;
					while (!this.isInterrupted() && userExists && timeoutFlag) {

						LOGGER.info("Waiting for terminal to be available");
						String terminal = availableTerminals.take();
						LOGGER.info("Terminal " + terminal + " requested by voter with Citizen Card ID: " + voter);

						HashMap<String, String> userItems = new HashMap<>();
						userItems.put("id", voter);
						userItems.put("vote-manager", handler);

						MulticastPacket packet = MulticastProtocol.itemList(this.getName(), terminal, userItems);
						packet.sendTo(votingSocket, votingGroup, votingPort);

						try {
							MulticastPacket ack = MulticastPacket.from(ackListenerSocket, this.getName());
							if (ack.getItem("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
								ack.getItem("target").equals(this.getName())) {
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

	public static void main(String[] args) {
		VotingDesk server = new VotingDesk("VotingDesk@DEI");
		server.start();
	}
}
