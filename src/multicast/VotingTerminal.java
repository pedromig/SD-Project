package multicast;

import multicast.protocol.MulticastProtocol;
import multicast.protocol.MulticastPacket;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.*;

public class VotingTerminal implements MulticastProtocol {

	// Globals / Defaults
	private static final String DEFAULT_MULTICAST_CLIENT_NAME =
			"VT-" + VotingTerminal.class.hashCode();

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.1";
	private static final String DEFAULT_DISCOVERY_MULTICAST_PORT = "6789";

	private static final int MAX_PASSWORD_ATTEMPTS = 3;
	private static final int VOTING_TIMEOUT_MS = 120000;
	private static final int AUTH_TIMEOUT_MS = 60000;

	private final String config;

	// Attributes
	private String name;

	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	public VotingTerminal(String configPath) {
		this.config = configPath;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java " + VotingTerminal.class.getName() + " {PROPERTIES_FILE}");
			return;
		}
		VotingTerminal terminal = new VotingTerminal(args[0]);
		terminal.start();
	}

	public Properties readPropertiesFile(String path) {
		Properties config = new Properties();
		try (InputStream is = new FileInputStream(path)) {
			config.load(is);
		} catch (IOException e) {
			System.err.println("Could not read configurations file: " + e.getMessage());
			System.err.println("Running server with default configs");
		}
		return config;
	}

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

	public String getName() {
		return name;
	}

	private boolean isNotSelfAddressed(MulticastPacket packet, String senderID) {
		return (packet.get("target") != null && !packet.get("target").equals(this.getName())) ||
			   (packet.get("target") != null && packet.get("target").equals(this.getName()) &&
				!packet.get("source").equals(senderID));
	}

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
					Thread.sleep(2000); // TODO: remove this
				}
			}
		} catch (InterruptedException | ExecutionException | IOException e) {
			System.err.println("Exception: " + e.getMessage());
		}
	}

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
