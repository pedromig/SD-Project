package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * Core Java Class for the UDP transport layer Protocol
 *
 */
public abstract class UdpCore extends Thread {
    protected int port;
    protected DatagramSocket socket;

    public UdpCore(int port){
        this.port = port;
        this.start();
    }

    public void run() {
        try {
            this.flavour();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if(socket != null) socket.close();
        }
    }

    public abstract void flavour() throws IOException;

    public void replyTo(DatagramPacket request, String msg) throws IOException {
        sendPacket(request.getAddress(), request.getPort(), msg);
    }

    public void sendPacket(InetAddress addr, int port, String msg) throws IOException {
        byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
        this.socket.send(packet);
    }

    public DatagramPacket receivePacket() throws IOException {
        byte[] buffer = new byte[1000];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(request);
        return request;
    }

    public String readPacket(DatagramPacket packet) {
        return new String(packet.getData(), 0, packet.getLength());
    }
}
