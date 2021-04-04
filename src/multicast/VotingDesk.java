package multicast;

import multicast.utils.LoggingFormatter;
import multicast.protocol.MulticastProtocol;
import multicast.protocol.MulticastPacket;
import multicast.ui.VotingDeskUI;

import rmi.interfaces.RmiMulticastServerInterface;
import rmi.interfaces.RmiServerInterface;

import utils.Vote;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Person;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;


public class VotingDesk extends UnicastRemoteObject implements MulticastProtocol, RmiMulticastServerInterface {

	// Globals / Defaults
	private static final String DEFAULT_MULTICAST_SERVER_NAME = "department";

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.1";
	private static final String DEFAULT_DISCOVERY_MULTICAST_PORT = "6789";

	private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.2";
	private static final String DEFAULT_VOTING_MULTICAST_PORT = "4321";

	private static final int RMI_RECONNECT_ATTEMPTS = 5;
	private static final int RMI_RECONNECT_TIMEOUT_MS = 30000 / RMI_RECONNECT_ATTEMPTS;

	private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());
	private final String config;

	// Attributes
	private String name;

	private static final String rmiServerUrl = "RmiServer";
	private RmiServerInterface rmiServer;

	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	private final Hashtable<String, String> terminals;
	private final BlockingQueue<String> voters;
	private final BlockingQueue<String> availableTerminals;

	public VotingDesk(String configFilePath) throws RemoteException {
		super();
		this.config = configFilePath;
		this.terminals = new Hashtable<>();
		this.voters = new LinkedBlockingQueue<>();
		this.availableTerminals = new LinkedBlockingQueue<>();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java " + VotingDesk.class.getName() + " {PROPERTIES_FILE}");
			System.exit(0);
		}

		VotingDesk server;
		try {
			server = new VotingDesk(args[0]);
			server.start();
		} catch (RemoteException e) {
			LOGGER.severe("Could not found entry on rmi registry (probably its down...) : " + e.getMessage());
			System.exit(-1);
		}
	}

	private <V> V rmiServerRemoteExceptionHandler(Callable<V> function) {
		V result = null;
		boolean tryConnect = true;
		while (tryConnect) {
			try {
				result = function.call();
				break;
			} catch (Exception e) {
				tryConnect = (rmiServer = this.connectRMIServer()) == null;
				e.printStackTrace();
			}
		}
		return result;
	}

	private Properties readPropertiesFile(String path) {
		Properties config = new Properties();
		try (InputStream is = new FileInputStream(path)) {
			config.load(is);
		} catch (IOException e) {
			LOGGER.severe("Could not read configurations file: " + e.getMessage());
			LOGGER.warning("Running server with default configs");
		}
		return config;
	}

	private RmiServerInterface connectRMIServer() {
		int attempt = 0;
		RmiServerInterface server = null;

		while (attempt++ != RMI_RECONNECT_ATTEMPTS) {
			try {
				server = (RmiServerInterface) Naming.lookup(rmiServerUrl);
				server.subscribe(this);
				return server;
			} catch (RemoteException e) {
				LOGGER.warning("RMI server connection failed, retrying in "
							   + RMI_RECONNECT_TIMEOUT_MS / 1000 + " seconds... " +
							   "(" + attempt + "/" + RMI_RECONNECT_ATTEMPTS + ")"
				);
				try {
					Thread.sleep(RMI_RECONNECT_TIMEOUT_MS);
				} catch (InterruptedException e1) {
					LOGGER.severe("RMI timeout error: " + e1.getMessage());
				}
			} catch (NotBoundException | MalformedURLException e) {
				LOGGER.severe("Exception caught during RMI server connection: " + e.getMessage());
				return null;
			}
		}
		LOGGER.severe("RMI server connection timed out!");
		return server;
	}

	public void start() {
		setupLogger();
		if ((this.rmiServer = connectRMIServer()) != null) {

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
			votingDeskServerStartup();
		}
		System.exit(0);
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

			voteManager.join();
			clientHandler.join();
			terminalManager.join();

		} catch (IOException e) {
			LOGGER.severe(this.getName() + " Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.severe(this.getName() + " server session interrupted!");
		}
		LOGGER.info(this.getName() + " server session terminated successfully!");
	}


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
								Person user = rmiServerRemoteExceptionHandler(
										() -> rmiServer.getPerson(Integer.parseInt(terminals.get(target)))
								);
								assert user != null :
										"No such user in the RMI database. (Please verify database integrity)";

								info.put("id", terminals.get(target));
								info.put("username", user.getName());
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
							// Perhaps Handle terminal shutdown properly
							LOGGER.info("Voting Terminal Disconnected");
							break;

					}
				} catch (IOException e) {
					LOGGER.info(this.getName() + " thread stopped, socket closed!");
				}
			}
		}
	}

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

						String election = request.get("election");
						String list = request.get("list");

						Person user = rmiServerRemoteExceptionHandler(
								() -> rmiServer.getPerson(Integer.parseInt(terminals.get(requestSource)))
						);
						assert user != null : "No such user in the RMI database. (Please verify database integrity)";

						Vote vote = new Vote(user.getIdentityCardNumber(), election, list, name);
						rmiServerRemoteExceptionHandler(() -> rmiServer.vote(vote));

						MulticastPacket ack = MulticastProtocol.acknowledge(this.getName(), requestSource);
						ack.sendTo(votingSocket, votingGroup, votingPort);

						terminals.put(requestSource, "EMPTY");

					} else if (request.get("target").equals(this.getName()) &&
							   requestType.equals(MulticastProtocol.LOGIN)) {

						MulticastPacket status;
						Person user = rmiServerRemoteExceptionHandler(
								() -> rmiServer.getPerson(Integer.parseInt(terminals.get(requestSource).trim()))
						);

						assert user != null;
						if (user.getPassword().equals(request.get("password"))) {
							status = MulticastProtocol.status(this.getName(), requestSource, "logged-in");
						} else {
							status = MulticastProtocol.status(this.getName(), requestSource, "invalid-password");
							status.sendTo(votingSocket, votingGroup, votingPort);
							continue;
						}
						status.sendTo(votingSocket, votingGroup, votingPort);

						CopyOnWriteArrayList<Election<?>> elections = rmiServerRemoteExceptionHandler(
								() -> rmiServer.getRunningElectionsByDepartment(name)
						);
						assert elections != null;
						elections.removeIf(election -> !election.getType().equals(user.getType()));

						for (Election<?> election : elections) {

							Boolean hasVoted = rmiServerRemoteExceptionHandler(
									() -> rmiServer.hasVoted(election.getName(), user.getIdentityCardNumber())
							);
							assert hasVoted != null : "Unexpected handling remote exception";

							if (!hasVoted) {
								HashMap<String, String> packetInfo = new HashMap<>();
								packetInfo.put("election-name", election.getName());

								CopyOnWriteArrayList<List<?>> lists = rmiServerRemoteExceptionHandler(() ->
										rmiServer.getListsAssignedOfType(user.getType(), election.getName())
								);

								int opt = 1;
								assert lists != null : "Unexpected handling remote exception";
								for (List<?> l : lists)
									packetInfo.put(String.valueOf(opt++), l.getName());

								packetInfo.put(String.valueOf(opt++), Vote.WHITE_VOTE);
								packetInfo.put(String.valueOf(opt), Vote.NULL_VOTE);

								MulticastPacket options =
										MulticastProtocol.itemList(this.getName(), requestSource, packetInfo);
								options.sendTo(votingSocket, votingGroup, votingPort);
							}
						}

						MulticastPacket confirmation =
								MulticastProtocol.status(this.getName(), requestSource, "info-sent");
						confirmation.sendTo(votingSocket, votingGroup, votingPort);
					}
				} catch (IOException e) {
					LOGGER.info(this.getName() + " thread stopped, socket closed!");
				}
			}
		}
	}

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

					Person user = rmiServerRemoteExceptionHandler(
							() -> rmiServer.getPerson(Integer.parseInt(voter))
					);

					if (user == null) {
						LOGGER.warning("Voter with Citizen Card ID not found!!");
						LOGGER.info("Removed voter with Citizen Card ID: " + voter);
						continue;
					}

					boolean canVote = false;
					CopyOnWriteArrayList<Election<?>> elections = rmiServerRemoteExceptionHandler(
							() -> rmiServer.getRunningElectionsByDepartment(name)
					);
					assert elections != null : "Unexpected error handling remote exception";
					elections.removeIf(election -> !election.getType().equals(user.getType()));

					for (Election<?> election : elections) {

						Boolean hasVoted = rmiServerRemoteExceptionHandler(
								() -> rmiServer.hasVoted(election.getName(), user.getIdentityCardNumber())
						);
						assert hasVoted != null : "Unexpected error handling remote exception";

						if (!hasVoted) {
							canVote = true;
							break;
						}
					}

					if (!canVote || elections.isEmpty()) {
						LOGGER.warning("Voter with Citizen Card ID: " + voter +
									   " is not eligible to vote in any election!");
						LOGGER.info("Removed voter with Citizen Card ID: " + voter);
						continue;
					}

					boolean timeoutFlag = true;
					LOGGER.info("Voter with Citizen Card ID identified");

					while (!this.isInterrupted() && timeoutFlag) {

						LOGGER.info("Waiting for terminal to be available");
						String terminal = availableTerminals.take();

						LOGGER.info("Terminal " + terminal + " requested by voter with Citizen Card ID: " + voter);
						terminals.put(terminal, voter);

						HashMap<String, String> userItems = new HashMap<>();
						userItems.put("id", voter);
						userItems.put("username", user.getName());
						userItems.put("vote-manager", handler);

						MulticastPacket packet = MulticastProtocol.itemList(this.getName(), terminal, userItems);
						packet.sendTo(votingSocket, votingGroup, votingPort);

						try {
							MulticastPacket ack = MulticastPacket.from(ackListenerSocket, this.getName());
							if (ack.get("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
								ack.get("target").equals(this.getName())) {
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

