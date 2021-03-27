package multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class VotingTerminal extends Thread implements MulticastProtocol {

    private static final String DEFAULT_MULTICAST_DISCOVERY_ADDRESS = "224.3.2.2";
    private static final int DEFAULT_DISCOVERY_MULTICAST_PORT = 4321;

    public VotingTerminal() {

    }

    @Override
    public void run() {

        int discoveryPort = DEFAULT_DISCOVERY_MULTICAST_PORT;
        try (MulticastSocket discovery = new MulticastSocket()) {

            InetAddress discoveryGroup = InetAddress.getByName(DEFAULT_MULTICAST_DISCOVERY_ADDRESS);
            discovery.joinGroup(discoveryGroup);

            MulticastPacket greeting = MulticastProtocol.greeting(this.getName());
            greeting.sendTo(discovery, discoveryGroup, discoveryPort);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VotingTerminal terminal = new VotingTerminal();
        terminal.start();
    }

}
