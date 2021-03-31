package multicast.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class MulticastPacket {
	private static final int PACKET_SIZE = 1000;

	private final HashMap<String, String> items;
	private boolean isComplete;

	public MulticastPacket() {
		this.items = new HashMap<>();
		this.isComplete = false;
	}

	public static MulticastPacket from(MulticastSocket socket, String self) throws IOException {
		MulticastPacket packet;
		do {
			byte[] buffer = new byte[PACKET_SIZE];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
			socket.receive(datagram);

			packet = MulticastPacket.parse(datagram);
			while (!packet.isComplete) {
				System.out.println("here");
				MulticastPacket concat = MulticastPacket.from(socket, self);
				packet.items.putAll(concat.items);
			}
			packet.isComplete = true;
		} while (packet.getItem("source").equals(self));
		return packet;
	}

	public static MulticastPacket parse(DatagramPacket datagramPacket) {
		MulticastPacket packet = new MulticastPacket();
		String data = new String(datagramPacket.getData());

		if (data.lastIndexOf('\n') != -1)
			packet.isComplete = true;

		String[] tokens = data.trim().split("(\\W?;\\W?)");
		for (String token : tokens) {
			String[] pair = token.split("(\\W?\\|\\W?)");
			packet.addItem(pair[0], pair[1]);
		}
		return packet;
	}

	public void sendTo(MulticastSocket socket, InetAddress address, int port) throws IOException {
		byte[] data = this.getPacketBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		socket.send(packet);
	}

	public void addItem(String key, String value) {
		this.items.put(key, value);
	}

	public String getItem(String key) {
		return items.get(key);
	}

	private byte[] getPacketBytes() {
		StringBuilder builder = new StringBuilder();
		for (String key : this.items.keySet()) {
			builder.append(key)
					.append("|")
					.append(this.items.get(key))
					.append(";");
		}
		builder.append("\n");
		return builder.toString().getBytes();
	}

	@Override
	public String toString() {
		return this.items.toString();
	}
}
