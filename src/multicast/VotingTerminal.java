package multicast;

import multicast.protocol.MulticastProtocol;
import multicast.protocol.MulticastPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class VotingTerminal implements MulticastProtocol {

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
	private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

	private final String name;

	private MulticastSocket statusSocket;
	private InetAddress statusGroup;
	private int statusPort;

	private MulticastSocket votingSocket;
	private InetAddress votingGroup;
	private int votingPort;

	public VotingTerminal(String name) {
		this.name = name;
	}

	public void start() {

		// TODO: parsing and client setup;
		try {
			this.statusGroup = InetAddress.getByName(DEFAULT_MULTICAST_DISCOVERY_ADDRESS);
			this.statusPort = DEFAULT_DISCOVERY_MULTICAST_PORT;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		connectToVotingDesk();
		votingService();
	}

	public String getName() {
		return name;
	}

	private void connectToVotingDesk() {
		try {
			this.statusSocket = new MulticastSocket(this.statusPort);
			this.statusSocket.joinGroup(this.statusGroup);

			MulticastPacket greeting = MulticastProtocol.greeting(this.getName());
			greeting.sendTo(statusSocket, statusGroup, statusPort);

			MulticastPacket reply = MulticastPacket.from(this.statusSocket, this.getName());
			if (reply.getItem("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
				reply.getItem("target").equals(this.getName())) {

				MulticastPacket infoList = MulticastPacket.from(this.statusSocket, this.getName());
				this.votingGroup = InetAddress.getByName(infoList.getItem("MULTICAST_VOTING_ADDRESS"));
				this.votingPort = Integer.parseInt(infoList.getItem("MULTICAST_VOTING_PORT"));

				this.votingSocket = new MulticastSocket(votingPort);
				this.votingSocket.joinGroup(votingGroup);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void votingService() {
		try {
			MulticastPacket voter = MulticastPacket.from(votingSocket, this.getName());
			String requestSource = voter.getItem("source");
			String voterID = voter.getItem("id");
			String voteRecipient = voter.getItem("vote-manager");

			MulticastPacket acknowledge = MulticastProtocol.acknowledge(this.getName(), requestSource);
			acknowledge.sendTo(votingSocket, votingGroup, votingPort);

			System.out.println("Hello " + voterID + " ready to vote?");
			String username = "hello", password = "world";

			HashMap<String, String> userAuthInfo = new HashMap<>();
			userAuthInfo.put("username", username);
			userAuthInfo.put("password", password);

			MulticastPacket auth = MulticastProtocol.itemList(this.getName(), voteRecipient, userAuthInfo);
			auth.sendTo(votingSocket, votingGroup, votingPort);

			MulticastPacket packet = MulticastProtocol.offer(this.getName());
			packet.sendTo(statusSocket, statusGroup, statusPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		VotingTerminal terminal = new VotingTerminal(args[0]);
		terminal.start();
	}
}

