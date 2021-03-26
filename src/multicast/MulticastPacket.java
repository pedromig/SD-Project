package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;


public class MulticastPacket {
    private static final int PACKET_SIZE = 1000;

    private final HashMap<String, String> items;

    public MulticastPacket() {
        this.items = new HashMap<>();
    }

    public static MulticastPacket from(MulticastSocket socket) throws IOException {
        byte[] buffer = new byte[PACKET_SIZE];
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        socket.receive(datagram);
        return MulticastPacket.parse(datagram);
    }

    public static MulticastPacket parse(DatagramPacket datagramPacket) {
        String data = new String(datagramPacket.getData());
        String[] tokens = data.trim().split("(\\W?;\\W?)");

        for (String token : tokens){
            String[] pair = token.split("(\\W?\\|\\W?)");
            System.out.println(pair[0] + ": " + pair[1]);
        }
        return new MulticastPacket();
    }


    public void sendTo(MulticastSocket socket, InetAddress address, int port) throws IOException {
        byte[] data = this.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    public void addItem(String key, String value) {
        this.items.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : this.items.keySet()) {
            builder.append(key)
                    .append("|")
                    .append(this.items.get(key))
                    .append(";");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n");
        return builder.toString();
    }

}
