package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer extends UdpCore{

    public UdpServer(int port) {
        super(port);
    }

    @Override
    public void flavour() throws IOException {
        DatagramPacket dp;
        // Establish Socket Connection
        this.socket = new DatagramSocket(this.port);
        while(true){
            // Receive Packet
            dp = this.receivePacket();
            this.replyTo(dp,"Hello My friend its me how u been");
            System.out.println("[Server] Got : " + this.readPacket(dp));
        }
    }
}
