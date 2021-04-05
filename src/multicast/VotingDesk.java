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

/**
 * The {@code VotingDesk} class represents a multicast server that provides the functionalities needed for a given
 * eVoting election to occur. This server serves as a interface where both the users and the voting terminals (where
 * the users vote) can connect to, in order to perform their activities. All voting desks are implemented as
 * instances of this class.
 * <p>
 * The {@code VotingDesk} being itself a multicast server implementation uses 2 multicast groups that can configured
 * using a properties file. These groups are used for the exchange of status / discovery messages between itself and
 * the voting terminals and another group for the exchange of critical / relevant information between the server and
 * the terminals (multicast clients). In the exchange of server-client information this server uses a protocol that
 * provides the primitives and basic structure of the messages that are transmitted. The implementation of this
 * protocol is provided in the {@link MulticastProtocol} interface.
 * <p>
 * The {@code VotingDesk} class is extends the {@link java.rmi.server.UnicastRemoteObject} class because it interacts
 * with a centralized Remote Method Invocation server that provides all the methods needed for the exchange of
 * relevant data e.g votes and user credentials. Also this server shares status messages with the RMI server in of
 * for this to know about the status of the current machine. The methods necessary for this are implemented in the
 * {@link RmiMulticastServerInterface}.
 * <p>
 * In order for this {@code VotingDesk} to be instanced correctly it may need a configuration.properties file that
 * follows the format specified by the {@link java.util.Properties} file format.
 * In the case of this server the arguments required to be present in the properties file are showed in the following
 * example:
 * <blockquote><pre>
 * server.department.name=DEI
 * multicast.discovery.group=224.3.2.1
 * multicast.discovery.port=6789
 * multicast.voting.group=224.3.2.2
 * multicast.voting.port=4321
 * </pre></blockquote>
 * <p>
 * The server uses a logging system that automatically prints debug messages to the console and to a log file if
 * those messages are emitted at level equal or greater than {@code java.lang.System.Logger.Level.WARNING}
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 * @see java.rmi.server.UnicastRemoteObject
 * @see java.lang.System.Logger
 * @see java.rmi.Naming
 * @see multicast.protocol.MulticastProtocol
 * @see multicast.protocol.MulticastPacket
 * @see rmi.interfaces.RmiServerInterface
 **/
public class VotingDesk extends UnicastRemoteObject implements MulticastProtocol, RmiMulticastServerInterface {

	/**
	 * @implNote Global / Default attributes that are given for a multicast server on startup
	 */
	private static final String DEFAULT_MULTICAST_SERVER_NAME = "department";

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.1";
	private static final String DEFAULT_DISCOVERY_MULTICAST_PORT = "6789";

	private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.2";
	private static final String DEFAULT_VOTING_MULTICAST_PORT = "4321";

	private static final int RMI_RECONNECT_ATTEMPTS = 5;
	private static final int RMI_RECONNECT_TIMEOUT_MS = 30000 / RMI_RECONNECT_ATTEMPTS;

	/**
	 * @implNote Default RMI Server connection configurations
	 */
	private static final String DEFAULT_RMI_SERVER_IP = "localhost";
	private static final String DEFAULT_RMI_SERVER_PORT = "7000";

	/**
	 * @implNote Logger instance use to log debug messages for the this server class.
	 */
	private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());
	private final String config;

	/**
	 * @implNote This VotingDesk server Name / ID
	 */
	private String name;

	private RmiServerInterface rmiServer;
	private InetAddress rmiAddress;
	private int rmiPort;

	/**
	 * @implNote Handles for the socket connection to the multicast discovery/status group needed by this server
	 */
	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	/**
	 * @implNote Handles for the socket connection to the multicast voting/information group needed by this server
	 */
	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	/**
	 * @implNote Data Structures holding flow control information in the server
	 * (available terminals, queued users, etc...)
	 */
	private final Hashtable<String, String> terminals;
	private final BlockingQueue<String> voters;
	private final BlockingQueue<String> availableTerminals;

	/**
	 * Initializes a newly created {@code VotingDesk} object so that it represents
	 * multicast server instance. The configurations of this server and initialization
	 * of the data structures required in order to save relevant information.
	 *
	 * @param configFilePath A {@code String} containing the server.properties file with the configuration for this
	 *                       server instance.
	 * @throws RemoteException A {@code RemoteException} exception that occurred during the execution of a remote
	 *                         method call to the RMI server
	 */
	public VotingDesk(String configFilePath) throws RemoteException {
		super();
		this.config = configFilePath;
		this.terminals = new Hashtable<>();
		this.voters = new LinkedBlockingQueue<>();
		this.availableTerminals = new LinkedBlockingQueue<>();
	}

	/**
	 * First method that is ran on server startup parsing the command line
	 * arguments and setting up te server and handling a potential error that
	 * can occur if {@link java.rmi.registry.LocateRegistry} could not find entry to the RMI
	 * server remote object exiting with exit code -1.
	 *
	 * @param args The command line arguments supplied to this function. In the case of this program the only one
	 *             required is the path for the configuration file
	 */
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

	/**
	 * Implementation of a {@link rmi.interfaces.RmiMulticastServerInterface} method required by the RMI
	 * server in order to query information about this {@code VotingDesk} instance.
	 * <p>
	 * A remote method invocation used by the RMI server to do a personalized print where this VotingDesk
	 * ID is displayed as a message preamble.
	 *
	 * @param msg The message to be displayed
	 * @throws RemoteException A RemoteException exception that occurred during the execution of a remote method call
	 *                         to the RMI server
	 */
	@Override
	public void print(String msg) throws RemoteException {
		System.out.println("[VotingDesk@" + name + "]: " + msg);
	}

	/**
	 * Implementation of a {@link rmi.interfaces.RmiMulticastServerInterface} method required by the RMI
	 * server in order to query information about this {@code VotingDesk} instance.
	 * <p>
	 * A remote method invocation used by the RMI server print information about the current status of this VotingDesk.
	 * The print message includes information about the terminals that this server has connected to it in any given
	 * moment and which users are connected to it.
	 *
	 * @return A {@code String} containing a status message about this server instance.
	 * @throws RemoteException A RemoteException exception that occurred during the execution of a remote method call
	 *                         to the RMI server
	 *                         <p>
	 *                         Used by this method implementation
	 * @see java.lang.StringBuilder
	 */
	@Override
	public String ping() throws RemoteException {
		StringBuilder sb = new StringBuilder();
		sb.append("VotingDesk@").append(name);
		for (String terminal : terminals.keySet()) {
			sb.append("Terminal: ")
			  .append(terminal)
			  .append("\t User: ")
			  .append(terminals.get(terminal))
			  .append("\n");
		}
		return sb.toString();
	}

	/**
	 * A wrapper method used to encapsulate {@link java.rmi.RemoteException} thrown by a method that requires some
	 * information to be queried from the RMI server.
	 * <p>
	 * This method provides a implementation that attempts to call method passed by parameter using the
	 * {@link java.util.concurrent.Callable} interface and in the event that this method fails throwing a
	 * {@link java.rmi.RemoteException} looping afterwards until the connection is able to be re-established
	 * with the RMI main or backup servers.
	 *
	 * @param function A {@code Callable} interface object wrapping a remote method call to the RMI server
	 * @param <V>      A template argument specifying the return type of the method called.
	 * @return The result of the computation made by the function {@param function} parameter
	 */
	private <V> V rmiServerRemoteExceptionHandler(Callable<V> function) {
		V result;
		while (true) {
			try {
				result = function.call();
				break;
			} catch (Exception e) {
				rmiServer = this.connectRMIServer();
			}
		}
		return result;
	}

	/**
	 * This method is responsible for reading the properties file and returning a {@link java.util.Properties} object
	 * with the key-value pairs containing the server configs.
	 *
	 * @param path The path to the server.properties file
	 * @return A properties object loaded with the server configs.
	 */
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

	/**
	 * @return
	 */
	private RmiServerInterface connectRMIServer() {
		int attempt = 0;
		RmiServerInterface server = null;

		while (attempt++ != RMI_RECONNECT_ATTEMPTS) {
			try {
				server = (RmiServerInterface)
						Naming.lookup("rmi://" + this.rmiAddress.getHostAddress() + ":" + this.rmiPort + "/RmiServer");
				server.subscribe(this, this.getName());
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
				// FIXME: Look at this!
				LOGGER.severe("Exception caught during RMI server connection: " + e.getMessage());
				return null;
			}
		}
		LOGGER.severe("RMI server connection timed out!");
		return server;
	}

	public void start() {
		setupLogger();

		Properties configs = readPropertiesFile(this.config);
		try {
			this.rmiAddress = InetAddress.getByName((String)
					configs.getOrDefault(
							"rmi.server.ip",
							DEFAULT_RMI_SERVER_IP
					)
			);

		} catch (UnknownHostException e) {
			LOGGER.severe("Unknown Host: " + e.getMessage());
		}

		this.rmiPort = Integer.parseInt((String) configs.getOrDefault(
				"rmi.server.port",
				DEFAULT_RMI_SERVER_PORT
		));


		if ((this.rmiServer = connectRMIServer()) != null) {

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

	@Override
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

					if (terminals.containsValue(String.valueOf(user.getIdentityCardNumber()))) {
						LOGGER.warning("Voter with Citizen Card ID: " + user.getIdentityCardNumber() +
									   " is already voting (there is an impostor among us)");
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

