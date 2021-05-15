package multicast;

import multicast.protocol.MulticastPacket;
import multicast.protocol.MulticastProtocol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * The {@code multicast.VotingTerminal} class represents a multicast client that provides the functionalities needed for a given
 * eVoting election to occur. This client serves as a interface with the users (voters) where these can interact with
 * in order to register their voting option. All voting terminals are implemented assistance of this class.
 * <p>
 * The {@code multicast.VotingDesk} being itself a multicast client implementation uses 2 multicast groups that can configured
 * using a properties file. These groups are used for the exchange of status / discovery messages between the server and
 * the voting terminals and another group for the exchange of critical / relevant information between the server and
 * the terminals (multicast clients). In the exchange of client-server information this client makes use of a
 * protocol that provides the primitives and basic structure of the messages that are transmitted. The implementation
 * of protocol is provided in the {@link MulticastProtocol} interface. Since the protocol is standard a client
 * for many platforms may be created if the rules of communication are followed.
 * <p>
 * In order for this {@code multicast.VotingTerminal} to be instanced correctly it may need a configuration.properties file that
 * follows the format specified by the {@link Properties} file format.
 * In the case of this client the arguments required to be present in the properties file are showed in the following
 * example:
 * <blockquote><pre>
 * terminal.id=VT-1
 * multicast.discovery.group=224.3.2.1
 * multicast.discovery.port=6789
 * </pre></blockquote>
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 * @see MulticastProtocol
 * @see MulticastPacket
 **/
public class VotingTerminal implements MulticastProtocol {

	/**
	 * @implNote Global / Default attributes that are given for a multicast client on startup
	 */
	private static final String DEFAULT_MULTICAST_CLIENT_NAME = "VT-" + VotingTerminal.class.hashCode();

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.1";
	private static final String DEFAULT_DISCOVERY_MULTICAST_PORT = "6789";

	private static final int MAX_PASSWORD_ATTEMPTS = 3;
	private static final int VOTING_TIMEOUT_MS = 120000;
	private static final int AUTH_TIMEOUT_MS = 60000;

	/**
	 * @implNote This multicast.VotingTerminal client Name / ID
	 */
	private String name;

	/**
	 * @implNote The path to the configuration properties file is stored here
	 */
	private final String config;

	/**
	 * @implNote Handles for the socket connection to the multicast discovery/status group needed by this client
	 */
	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	/**
	 * @implNote Handles for the socket connection to the multicast voting/information group needed by this client
	 */
	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	/**
	 * Initializes a newly created {@code multicast.VotingTerminal} object so that it represents
	 * multicast client instance. The configurations of this client.
	 *
	 * @param configPath A {@code String} containing the terminal.properties file with the configuration for this
	 *                   client instance.
	 */
	public VotingTerminal(String configPath) {
		this.config = configPath;
	}

	/**
	 * First method that is ran on client startup parsing the command line
	 * arguments.
	 *
	 * @param args The command line arguments supplied to this function. In the case of this program the only one
	 *             required is the path for the configuration file
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java " + VotingTerminal.class.getName() + " {PROPERTIES_FILE}");
			return;
		}
		VotingTerminal terminal = new VotingTerminal(args[0]);
		terminal.start();
	}

	/**
	 * This method is responsible for reading the properties file and returning a {@link Properties} object
	 * with the key-value pairs containing the client configs.
	 *
	 * @param path The path to the terminal.properties file
	 * @return A properties object loaded with the terminal configs.
	 */
	public Properties readPropertiesFile(String path) {
		Properties config = new Properties();
		try (InputStream is = new FileInputStream(path)) {
			config.load(is);
		} catch (IOException e) {
			System.err.println("Could not read configurations file: " + e.getMessage());
			System.err.println("Running terminal with default configs");
		}
		return config;
	}


	/**
	 * The method that is called to prepare the terminal client startup, reading the terminal configuration properties
	 * file. In case an exception is thrown inside the server will catch that exception
	 * emitting an error message and automatically stopping its execution.
	 */
	public void start() {

		Properties configs = readPropertiesFile(this.config);
		this.name = (String) configs.getOrDefault(
				"terminal.id",
				DEFAULT_MULTICAST_CLIENT_NAME
		);

		try {
			this.statusGroup = InetAddress.getByName(
					(String) configs.getOrDefault(
							"multicast.discovery.group",
							DEFAULT_MULTICAST_DISCOVERY_ADDRESS
					)
			);
		} catch (UnknownHostException e) {
			System.err.println("Unknown Host: " + e.getMessage());
		}

		this.statusPort = Integer.parseInt((String) configs.getOrDefault(
				"multicast.discovery.port",
				DEFAULT_DISCOVERY_MULTICAST_PORT
				)
		);

		MulticastPacket connection;
		if ((connection = connectToVotingDesk()) != null) {
			try (Scanner sc = new Scanner(System.in)) {

				if (Integer.parseInt(connection.get("ITEM_COUNT")) > 2) {
					System.err.println("Hello " + connection.get("username") + " ready to vote?");
					try {
						registerVote(sc, connection.get("id"), connection.get("vote-manager"));
					} catch (TimeoutException e) {
						System.out.println("\nVoting Timeout");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException interruptedException) {
							interruptedException.printStackTrace();
						}
					}
				}

				System.out.print("\033[H\033[2J");
				System.out.flush();
				votingService(sc);

			} catch (IOException e) {
				System.out.println("Exception: " + e.getMessage());
			}
		}
	}

	/**
	 * A getter method for the name of this {@code multicast.VotingTerminal} multicast client. Through this name the Multicast
	 * server is able to identify this {@code multicast.VotingTerminal} being able to monitor it, and restrict use it to attend
	 * to the clients (voters) requests.
	 *
	 * @return The name identifying the current {@code multicast.VotingTerminal} client instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Helper method to check if a given {@link MulticastPacket} is addressed to this instance of a voting terminal.
	 *
	 * @param packet   The multicast packet that is being received
	 * @param senderID The id of the server thread that was responsible for sending the message
	 * @return true - if the message is not self addressed; false - otherwise
	 */
	private boolean isNotSelfAddressed(MulticastPacket packet, String senderID) {
		return (packet.get("target") != null && !packet.get("target").equals(this.getName())) ||
			   (packet.get("target") != null && packet.get("target").equals(this.getName()) &&
				!packet.get("source").equals(senderID));
	}

	/**
	 * A method that implements the functionality that allows the {@code multicast.VotingTerminal} multicast client to connect
	 * to a{@code multicast.VotingDesk} server running in a given multicast group. In case the server does not respond the method
	 * will wait until a server connects to a multicast group and sends {@link MulticastProtocol#PROBE} that tells the
	 * terminal clients that they can already connect to it.
	 *
	 * @return The handle to the {@link rmiserver.interfaces.RmiServerInterface} object holding the remote methods that
	 * can be invoked by this {@code multicast.VotingDesk} instance.
	 * @implNote The method might not be able to establish a connection with the multicast server because this one is
	 * offline. In that case it will wait until the program is killed having no timout mechanism.
	 */
	private MulticastPacket connectToVotingDesk() {
		MulticastPacket infoList = null;
		try {
			this.statusSocket = new MulticastSocket(this.statusPort);
			this.statusSocket.joinGroup(this.statusGroup);

			MulticastPacket greeting = MulticastProtocol.greeting(this.getName());
			greeting.sendTo(statusSocket, statusGroup, statusPort);

			System.out.println("Waiting for server to be ready...");
			boolean connected = false;
			while (!connected) {
				MulticastPacket reply = MulticastPacket.from(this.statusSocket, this.getName());
				System.out.println(reply);

				if (reply.get("type").equals(MulticastProtocol.PROBE)) {
					greeting.sendTo(statusSocket, statusGroup, statusPort);
					reply = MulticastPacket.from(this.statusSocket, this.getName());
					System.out.println(reply);
				}

				if (reply.get("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
					reply.get("target").equals(this.getName())) {

					infoList = MulticastPacket.from(this.statusSocket, this.getName());
					this.votingGroup = InetAddress.getByName(infoList.get("MULTICAST_VOTING_ADDRESS"));
					this.votingPort = Integer.parseInt(infoList.get("MULTICAST_VOTING_PORT"));

					this.votingSocket = new MulticastSocket(votingPort);
					this.votingSocket.joinGroup(votingGroup);
					connected = true;
				}
			}

		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return infoList;
	}


	/**
	 * This method is responsible for starting this {@code multicast.VotingTerminal} voting service that starts by signaling the
	 * server telling that it is ready through the use of a {@link MulticastProtocol#READY} message. After this
	 * terminal receives a job it will complete it and on completion will send a {@link MulticastProtocol#OFFER}
	 * message signaling the server so it can know that this terminal is available to receive a new job again.
	 *
	 * @param sc An instance o a scanner class containing the scanner to be used in this terminal in order to receive
	 *           user input
	 * @throws IOException A exception that may be thrown if the socket through which this service is sending the
	 *                     messages is closed
	 */
	private void votingService(Scanner sc) throws IOException {

		MulticastPacket ready = MulticastProtocol.ready(this.getName());
		ready.sendTo(statusSocket, statusGroup, statusPort);

		System.out.println(this.getName() + " Blocked and Waiting!");
		while (true) {
			MulticastPacket voter = MulticastPacket.from(votingSocket, this.getName());
			String requestType = voter.get("type");
			String requestSource = voter.get("source");
			String requestTarget = voter.get("target");

			if (requestType.equals(MulticastProtocol.LIST) && requestTarget != null &&
				requestTarget.equals(this.getName())) {

				String voterID = voter.get("id");
				String username = voter.get("username");
				String voteRecipient = voter.get("vote-manager");
				if (voteRecipient == null || username == null || voterID == null) {
					continue;
				}

				MulticastPacket acknowledge = MulticastProtocol.acknowledge(this.getName(), requestSource);
				acknowledge.sendTo(votingSocket, votingGroup, votingPort);

				try {
					System.err.println("Hello " + username + " ready to vote?");
					registerVote(sc, voterID, voteRecipient);

					System.out.print("\033[H\033[2J");
					System.out.flush();

				} catch (TimeoutException e) {
					System.out.println("Voting Timeout");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}

				MulticastPacket packet = MulticastProtocol.offer(this.getName());
				packet.sendTo(statusSocket, statusGroup, statusPort);
				System.out.println(this.getName() + " Blocked and Waiting!");
			}
		}
	}

	/**
	 * This method is responsible for registering a vote intention of a given client using this {@code multicast.VotingTerminal}
	 * instance. To make the vote choice it is given to the user 120 seconds by default after which a timeout will
	 * take place causing the terminal to be blocked again and the user removed (needing to go back to the {@code
	 * multicast.VotingDesk} in order to reenter the queue.
	 *
	 * @param sc            An instance o a scanner class containing the scanner to be used in this terminal in order
	 *                      to receive user input
	 * @param voterID       The Citizen Card ID of the voter in order to signal the server when the client has already
	 *                      finished voting (freeing the terminal)
	 * @param voteRecipient The thread/server that handles the reception of votes send by the terminals.
	 * @throws TimeoutException The exception thrown when a timeout occurs in a terminal because the user failed to
	 *                          fill in the required information in the time given to do it.
	 */
	private void registerVote(Scanner sc, String voterID, String voteRecipient) throws TimeoutException {
		try {
			String option = null;
			if (authenticateVoter(sc, voterID, voteRecipient)) {
				MulticastPacket election;
				System.out.println("You have " + VOTING_TIMEOUT_MS / 1000 + " seconds to vote!");

				if ((election = selectElection(sc, voteRecipient)) != null) {

					for (String key : election.getItems().keySet()) {
						if (!key.matches("source|ITEM_COUNT|ITEM_LIST|target|election-name|type")) {
							System.out.println(key + " - " + election.get(key));
						}
					}

					while (election.get(option) == null) {
						FutureTask<String> task = new FutureTask<>(sc::nextLine);
						new Thread(task).start();
						System.out.print("Option: ");
						option = task.get(VOTING_TIMEOUT_MS, TimeUnit.MILLISECONDS).trim();
					}

					MulticastPacket vote =
							MulticastProtocol.vote(this.getName(),
									election.get("election-name"),
									election.get(option)
							);
					vote.sendTo(votingSocket, votingGroup, votingPort);

					MulticastPacket confirmation;
					do {
						confirmation = MulticastPacket.from(votingSocket, this.getName());
					} while (isNotSelfAddressed(confirmation, voteRecipient));

					System.out.println("Vote was registered successfully!");
					Thread.sleep(2000);
				}
			}
		} catch (InterruptedException | ExecutionException | IOException e) {
			System.err.println("Exception: " + e.getMessage());
		}
	}

	/**
	 * This method is responsible for authenticating a given user in the {@code multicast.VotingDesk} multicast server instance
	 * that it is connected to. It sends the user credentials and waits for a {@link MulticastProtocol#STATUS} message
	 * saying that the user logged in successfully. For this operation to be performed it is given to the client a 60
	 * seconds after which a timeout will occur causing the client (voter) to be removed from the terminal resulting
	 * in its blocking.
	 *
	 * @param sc            An instance o a scanner class containing the scanner to be used in this terminal in order
	 *                      to receive user input
	 * @param voterID       The Citizen Card ID of the voter in order to signal the server when the client has already
	 *                      finished voting (freeing the terminal)
	 * @param voteRecipient The thread/server that handles the reception of votes send by the terminals.
	 * @return A boolean with the value true if the user authenticated successfully and false otherwise
	 * @throws TimeoutException The exception thrown when a timeout occurs in a terminal because the user failed to
	 *                          fill in the required information in the time given to do it.
	 */
	private boolean authenticateVoter(Scanner sc, String voterID, String voteRecipient) throws TimeoutException {
		int attempts = MAX_PASSWORD_ATTEMPTS;

		try {
			System.out.println("You have " + AUTH_TIMEOUT_MS / 1000 + " seconds to authenticate!");
			MulticastPacket authStatus;
			do {
				System.out.println(attempts);
				String password = null;
				while (password == null || password.length() == 0) {
					System.out.print("Password: ");
					FutureTask<String> task = new FutureTask<>(sc::nextLine);
					new Thread(task).start();
					password = task.get(AUTH_TIMEOUT_MS, TimeUnit.MILLISECONDS).trim();
				}
				attempts--;

				MulticastPacket auth = MulticastProtocol.login(this.getName(), voteRecipient, voterID, password);
				auth.sendTo(votingSocket, votingGroup, votingPort);

				do {
					authStatus = MulticastPacket.from(votingSocket, this.getName());
				} while (isNotSelfAddressed(authStatus, voteRecipient));

			} while (attempts > 0 && !authStatus.get("status").equals("logged-in"));

			if (attempts == 0)
				System.err.println("Failed after 3 attempts, blocking terminal");
		} catch (IOException | InterruptedException | ExecutionException e) {
			System.err.println("Exception: " + e.getMessage());
		}
		return attempts != 0;
	}

	/**
	 * This method prompts the user to select and election in which to register its vote option.(Note that one user
	 * may only vote once for terminal session). In this method the it is displayed to the user the multiple elections
	 * where he is allowed to vote and after the user chooses one it will return the {@link MulticastPacket} that was
	 * received
	 * from the server with all the information regrading the election chosen by the user. It is given to the user 120
	 * seconds to choose the election. After that a timeout will ocurr being the terminal blocked and the user removed.
	 *
	 * @param sc            An instance o a scanner class containing the scanner to be used in this terminal in order
	 *                      to receive user input
	 * @param voteRecipient The thread/server that handles the reception of votes send by the terminals.
	 * @return The {@code multicast.protocol.MulticastPacket} with the information regarding the election choosen by the user.
	 * @throws TimeoutException A exception thrown if the user does not fill the required fields in the time that it
	 *                          was given to do so
	 */
	private MulticastPacket selectElection(Scanner sc, String voteRecipient) throws TimeoutException {
		MulticastPacket selected = null;
		try {
			ArrayList<MulticastPacket> elections = new ArrayList<>();
			while (true) {
				MulticastPacket e = MulticastPacket.from(votingSocket, this.getName());
				if (isNotSelfAddressed(e, voteRecipient))
					continue;
				if (e.get("type").equals(MulticastProtocol.STATUS))
					break;
				elections.add(e);
			}

			System.out.println("################ AVAILABLE ELECTIONS ################");
			int opt = 1;
			for (MulticastPacket packet : elections) {
				System.out.println(opt++ + " - " + packet.get("election-name"));
			}
			System.out.println("#####################################################");

			int election = -1;
			while (election <= 0 || election > elections.size()) {
				System.out.print("Choose Election: ");
				FutureTask<Integer> task = new FutureTask<>(sc::nextInt);
				new Thread(task).start();
				election = task.get(VOTING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			}
			selected = elections.get(election - 1);

		} catch (IOException | InterruptedException | ExecutionException e) {
			System.err.println("Exception: " + e.getMessage());
		}
		return selected;
	}
}

