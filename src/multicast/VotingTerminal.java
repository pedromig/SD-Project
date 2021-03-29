package multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class VotingTerminal extends Thread implements MulticastProtocol {

    private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
    private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

    private MulticastSocket discovery;
    private MulticastSocket voting;

    public VotingTerminal(String name) {
        super(name);
    }

    private String[] connect(String groupAddress, int port) {
        String[] votingInterface = null;
        try {
            this.discovery = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(groupAddress);
            discovery.joinGroup(group);

            MulticastPacket greeting = MulticastProtocol.greeting(this.getName());
            greeting.sendTo(discovery, group, port);

            MulticastPacket reply = MulticastPacket.from(this.discovery, this.getName());

            if (reply.getItem("type").equals(MulticastProtocol.ACKNOWLEDGE) &&
                    reply.getItem("target").equals(this.getName())) {
                MulticastPacket infoList = MulticastPacket.from(this.discovery, this.getName());
                votingInterface = new String[]{infoList.getItem("MULTICAST_VOTING_ADDRESS"),
                        infoList.getItem("MULTICAST_VOTING_PORT")};

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return votingInterface;
    }

    @Override
    public void run() {
        String[] votingInterface = connect(DEFAULT_MULTICAST_DISCOVERY_ADDRESS, DEFAULT_DISCOVERY_MULTICAST_PORT);
        String votingGroup = votingInterface[0];
        int votingPort = Integer.parseInt(votingInterface[1]);

        System.err.println(votingGroup);
        System.err.println(votingPort);


    }

    public static void main(String[] args) {
        VotingTerminal terminal = new VotingTerminal("VotingTerminal-1");
        terminal.start();
    }

}
