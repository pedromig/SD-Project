package multicast;

import ui.VotingDeskUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDate;
import java.util.Date;

import java.util.HashMap;

import java.util.logging.*;

public class VotingDesk extends Thread {

	private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.1";
	private static final int DEFAULT_VOTING_MULTICAST_PORT = 6789;

	private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
	private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

	private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());
	private static final Object runningUILock = new Object();

	public VotingDesk(String name) {
		super(name);
	}

	private void setupLogger() {
		SimpleFormatter formatter = new SimpleFormatter() {
			private static final String format = "[%1$tF %1$tT][%2$s]: %3$s %n";

			@Override
			public synchronized String format(LogRecord record) {
				return String.format(format,
						new Date(record.getMillis()),
						record.getLevel().getLocalizedName(),
						record.getMessage()
				);
			}
		};

		LOGGER.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		LOGGER.addHandler(handler);

		try {
			FileHandler logFile = new FileHandler(this.getName().toLowerCase() + LocalDate.now() + ".log");
			logFile.setFormatter(formatter);
			LOGGER.addHandler(logFile);

		} catch (IOException e) {
			LOGGER.severe("Log file creation failed: " + e.getMessage());
		}

	}

	@Override
	public void run() {
		setupLogger();

		int discoveryPort = DEFAULT_DISCOVERY_MULTICAST_PORT;
		int votingPort = DEFAULT_VOTING_MULTICAST_PORT;

		try (MulticastSocket discovery = new MulticastSocket(discoveryPort);
			 MulticastSocket voting = new MulticastSocket(votingPort)) {

			InetAddress discoveryGroup = InetAddress.getByName(DEFAULT_MULTICAST_DISCOVERY_ADDRESS);
			InetAddress votingGroup = InetAddress.getByName(DEFAULT_MULTICAST_VOTING_ADDRESS);

			discovery.joinGroup(discoveryGroup);
			voting.joinGroup(votingGroup);

			LOGGER.info(this.getName() + " server running on " + discovery.getLocalAddress());
			LOGGER.info(this.getName() + " joined discovery multicast group "
					+ discoveryGroup.getHostName() + ", " + "port" + " " + discoveryPort);
			LOGGER.info(this.getName() + " joined voting multicast group "
					+ votingGroup.getHostName() + ", port " + votingPort);

			String terminalManagerName = this.getName() + "_TerminalManager";
			VotingDeskTerminalManager terminalManager =
					new VotingDeskTerminalManager(terminalManagerName, discovery, discoveryGroup, discoveryPort,
							votingGroup, votingPort);
			LOGGER.info("Creating VotingDeskTerminalManager thread!");

			String voteManagerName = this.getName() + "_VotingManager";
			VotingDeskVoteManager voteManager
					= new VotingDeskVoteManager(voteManagerName, voting, votingGroup, votingPort);
			LOGGER.info("Creating VotingDeskVoteManager thread!");

			terminalManager.start();
			voteManager.start();

			LOGGER.info("Starting VotingDesk console");
			VotingDeskUI console = new VotingDeskUI(this);
			console.addWindowCloseListener();
			console.open();

			synchronized (this) {
				this.wait();
			}

			terminalManager.interrupt();
			voteManager.interrupt();

			voteManager.join();

			discovery.leaveGroup(discoveryGroup);
			voting.leaveGroup(votingGroup);
		} catch (IOException e) {
			LOGGER.severe(this.getName() + " Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.info(this.getName() + " server session interrupted!");
		}

		LOGGER.info(this.getName() + " server session terminated successfully!");
	}

	private static class VotingDeskTerminalManager extends Thread {
		private final MulticastSocket socket;

		private final InetAddress discoveryGroup;
		private final int discoveryPort;

		private final InetAddress votingGroup;
		private final int votingPort;

		public VotingDeskTerminalManager(String name, MulticastSocket socket, InetAddress group, int port,
										 InetAddress votingGroup, int votingPort) {
			super(name);
			this.socket = socket;
			this.discoveryGroup = group;
			this.discoveryPort = port;
			this.votingGroup = votingGroup;
			this.votingPort = votingPort;
		}

		@Override
		public void run() {
			LOGGER.info(this.getName() + " thread started!");
			try {
				while (!this.isInterrupted()) {
					MulticastPacket request = MulticastPacket.from(socket, this.getName());
					String requestType = request.getItem("type");

					if (requestType.equals(MulticastProtocol.GREETING)) {
						String target = request.getItem("source");

						MulticastPacket acknowledge = MulticastProtocol.acknowledge(this.getName(), target);
						acknowledge.sendTo(socket, discoveryGroup, discoveryPort);

						HashMap<String, String> info = new HashMap<>();
						info.put("MULTICAST_VOTING_ADDRESS", String.valueOf(votingGroup));
						info.put("MULTICAST_VOTING_PORT", String.valueOf(votingPort));

						MulticastPacket reply = MulticastProtocol.itemList(this.getName(), target, info);
						reply.sendTo(socket, discoveryGroup, discoveryPort);
					} else if (requestType.equals(MulticastProtocol.GOODBYE)) {
						throw new UnsupportedOperationException("Not implemented yet :)");
					}
				}
			} catch (IOException e) {
				LOGGER.info(this.getName() + " thread stopped!");
			}
		}
	}

	private static class VotingDeskVoteManager extends Thread {
		private final MulticastSocket socket;
		private final InetAddress group;
		private final int port;

		public VotingDeskVoteManager(String name, MulticastSocket socket, InetAddress group, int port) {
			super(name);
			this.socket = socket;
			this.group = group;
			this.port = port;
		}

		@Override
		public void run() {

			LOGGER.info(this.getName() + " thread started!");
			while (!this.isInterrupted()) ;
			LOGGER.info(this.getName() + " thread stopped!");
		}
	}

	public static void main(String[] args) {
		VotingDesk server = new VotingDesk("VotingDesk@DEI");
		server.start();
	}
}
