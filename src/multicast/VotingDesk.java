package multicast;

import multicast.ui.VotingDeskConsole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDate;
import java.util.Date;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;

public class VotingDesk extends Thread {

    private static final String DEFAULT_MULTICAST_VOTING_ADDRESS = "224.3.2.1";
    private static final int DEFAULT_VOTING_MULTICAST_PORT = 6789;

    private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
    private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

    private static final Logger LOGGER = Logger.getLogger(VotingDesk.class.getName());
    private static boolean ENABLE_DEBUG_LOG = false;
    private static boolean ENABLE_LOG_FILE = false;

    public final Object lock = new Object();

    public VotingDesk(String name) {
        super(name);
    }

    public void enableDebugLog() {
        ENABLE_DEBUG_LOG = true;
    }

    public void enableLogging() {
        ENABLE_LOG_FILE = true;
    }

    private void setupLogger() {
        SimpleFormatter formatter = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT][%2$s][%3$s]: %4$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(format,
                        new Date(record.getMillis()),
                        Thread.currentThread().getName() + "Thread",
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        };

        LOGGER.setUseParentHandlers(false);
        if (ENABLE_DEBUG_LOG) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(formatter);
            LOGGER.addHandler(handler);
        }

        if (ENABLE_LOG_FILE) {
            try {
                FileHandler logFile = new FileHandler(this.getName().toLowerCase() + LocalDate.now() + ".log");
                logFile.setFormatter(formatter);
                LOGGER.addHandler(logFile);

            } catch (IOException e) {
                LOGGER.severe("Log file creation failed: " + e.getMessage());
            }
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
            LOGGER.info(this.getName() + " joined discovery multicast group " + discoveryGroup.getHostName() + ", port " + discoveryPort);
            LOGGER.info(this.getName() + " joined voting multicast group " + votingGroup.getHostName() + ", port " + votingPort);

            VotingDeskTerminalManager terminalManager = new VotingDeskTerminalManager(discovery, discoveryGroup, discoveryPort);
            LOGGER.info("Creating VotingDeskTerminalManager thread!");

            VotingDeskVoteManager voteManager = new VotingDeskVoteManager(voting, votingGroup, votingPort);
            LOGGER.info("Creating VotingDeskVoteManager thread!");

            terminalManager.start();
            voteManager.start();

            LOGGER.info("Starting VotingDesk console");
            VotingDeskConsole console = new VotingDeskConsole(this);

            console.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent arg) {
                    synchronized (lock) {
                        console.setVisible(false);
                        console.dispose();
                        lock.notify();
                    }
                }
            });

            console.open();
            synchronized (lock) {
                while (console.isVisible())
                    lock.wait();
            }

            discovery.close();
            voting.close();

            terminalManager.join();
            voteManager.join();

            LOGGER.info(this.getName() + " server session terminated successfully!");

        } catch (IOException e) {
            LOGGER.severe(this.getName() + " Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.info(this.getName() + " server session interrupted!");
        }
    }

    private static class VotingDeskTerminalManager extends Thread {
        private final MulticastSocket socket;
        private final InetAddress group;
        private final int port;

        public VotingDeskTerminalManager(MulticastSocket socket, InetAddress group, int port) {
            super("TerminalManger");
            this.socket = socket;
            this.group = group;
            this.port = port;
        }

        @Override
        public void run() {
            LOGGER.info(this.getName() + " thread started!");
            try {
                while (true) {
                    MulticastPacket packet = MulticastPacket.from(socket);
                    ;

                }

            } catch (IOException e) {
                LOGGER.info("VotingDeskTerminalManager thread stopped!");
            }
        }
    }

    private static class VotingDeskVoteManager extends Thread {
        private final MulticastSocket socket;
        private final InetAddress group;
        private final int port;

        public VotingDeskVoteManager(MulticastSocket socket, InetAddress group, int port) {
            super("VoteManager");
            this.socket = socket;
            this.group = group;
            this.port = port;
        }

        @Override
        public void run() {
            LOGGER.info(this.getName() + " thread started!");

            LOGGER.info("VotingDeskVoteManager thread stopped!");
        }

    }

    public static void main(String[] args) {
        VotingDesk server = new VotingDesk("VotingDesk@DEI");
        server.enableDebugLog();
        // server.enableLogging();
        server.start();
    }
}
