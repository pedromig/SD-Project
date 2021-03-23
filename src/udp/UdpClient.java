package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient extends UdpCore {

    public UdpClient(int port) {
        super(port);
    }

    @Override
    public void flavour() throws IOException {
        DatagramPacket dp;
        int counter = 0;
        // Establish Socket Connection
        this.socket = new DatagramSocket(this.port);
        while(true){
            String s = "Ola " + counter++;
            this.sendPacket(InetAddress.getByName("localhost"), 1235, s);
            dp = this.receivePacket();
            try {sleep(3000);} catch (InterruptedException ignored) {};
            System.out.println("[Client] Got: " + this.readPacket(dp));
        }
    }
}
