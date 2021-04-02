package multicast.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class MulticastPacket {
	private static final int PACKET_SIZE = 5000;

	private final HashMap<String, String> items;

	public MulticastPacket() {
		this.items = new HashMap<>();
	}

	public static MulticastPacket from(MulticastSocket socket, String self) throws IOException {
		MulticastPacket packet;
		do {
			byte[] buffer = new byte[PACKET_SIZE];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
			socket.receive(datagram);

			packet = MulticastPacket.parse(datagram);
		} while (packet.get("source").equals(self));
		return packet;
	}

	public static MulticastPacket parse(DatagramPacket datagramPacket) {
		MulticastPacket packet = new MulticastPacket();
		String data = new String(datagramPacket.getData());

		String[] tokens = data.trim().split("(\\W?;\\W?)");
		for (String token : tokens) {
			String[] pair = token.split("(\\W?\\|\\W?)");
			packet.put(pair[0], pair[1]);
		}
		return packet;
	}

	public void sendTo(MulticastSocket socket, InetAddress address, int port) throws IOException {
		byte[] data = this.getPacketBytes();
		assert(data.length <= PACKET_SIZE) : "Packet size may not exceed " + PACKET_SIZE + " bytes!!";
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		socket.send(packet);
	}

	public void put(String key, String value) {
		this.items.put(key, value);
	}

	public String get(String key) {
		return items.get(key);
	}

	public HashMap<String, String> getItems() {
		return items;
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
