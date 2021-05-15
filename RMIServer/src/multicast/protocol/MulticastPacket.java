package multicast.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

/**
 * The {@code MulticastPacket} class is a class that works like a wrapper arrowing the DatagramPacket implementation.
 * Since the DatagramPacket cannot be extended (because it has been marked as a final class) this class aims to
 * provide the same features but adapted to work with the {@link MulticastProtocol} developed for our application.
 * <p>
 * The data contained in a {@code MulticastPacket} follows a hashmap like structure and is parsed and stored using
 * that data structure. The string representation of a packet before it is parsed is the following
 * <blockquote><pre> {source = VotingDesk@DEI ; target = VT-1 ; status="logged-in"} </pre></blockquote>
 * <p>
 * This API provides methods that make easy the sending and receiving of messages between 2 multicast entities
 * Usage examples are shown bellow:
 * <blockquote<pre>
 *  MulticastPacket empty = new MulticastPacket();
 *  empty.put("Hello", "World");
 *  String query = empty.get("Hello")
 *
 *  MulticastPacket packet = new MulticastPacket.from(socket)
 *  System.out.println(packet);
 *
 *  Multicast info = new MulticastPacket()
 *  info.add("username", "pedro");
 *  info.sendTo(socket, group, port);
 *
 *  // Example using {@link MulticastProtocol} protocol standard.
 *  MulticastPacket ack = MulticastProtocol.acknowledge();
 *  ack.sendTo(socket, group, port);
 *  </pre></blockquote>
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 * @see DatagramPacket
 * @see InetAddress
 */
public class MulticastPacket {

	/**
	 * @implNote The maximum number of bytes that can be placed inside a packet.
	 */
	private static final int PACKET_SIZE = 5000;

	/**
	 * @implNote The information key-value pairs that this packet contains
	 */
	private final HashMap<String, String> items;


	/**
	 * The default constructor of an instance of this class
	 */
	public MulticastPacket() {
		this.items = new HashMap<>();
	}

	/**
	 * This static method provides a simple way to read a datagram from a socket and parse it returning a multicast
	 * packet with all the information that was contained in that packet. The method assumes in its implementation
	 * that any message that has origin in the sender of the current message is to be ignored. We take that in
	 * consideration because being the message sent to a multicast group there is a risk of the sender reading a
	 * message that was sent by itself. For that not to happen we use a loop that only breaks when a message that does
	 * not have a source in {@code self} is received
	 *
	 * @param socket The socket where the message is to be read.
	 * @param self   The ID of the sender of this message.
	 * @return The {@code MulticastPacket} with all the information contained in the datagram received from the socket
	 * @throws IOException A exception thrown if the socket where we were trying to read is closed during the read
	 *                     operation
	 */
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

	/**
	 * This static method implements the parsing needed to transform the  textual information contained in a
	 * {@link DatagramPacket} into a {@link MulticastPacket}.
	 *
	 * @param datagramPacket The {@code DatagramPacket} that is going to be parsed
	 * @return The {@code MulticastPacket} that was parsed from the original message conveyed in the {@code
	 * DatagramPacket}
	 */
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

	/**
	 * A method that is used to send a the information contained in this instance of the {@code MulticastPacket} to the
	 * given socket linked to a given multicast group address and port.
	 *
	 * @param socket  The socket where the information will be sent
	 * @param address The address of the group where that information will go to
	 * @param port    The port to be used to send the messages.
	 * @throws IOException A exception thrown if the socket where we were trying to read is closed during the read
	 *                     operation
	 */
	public void sendTo(MulticastSocket socket, InetAddress address, int port) throws IOException {
		byte[] data = this.getPacketBytes();
		assert (data.length <= PACKET_SIZE) : "Packet size may not exceed " + PACKET_SIZE + " bytes!!";
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		socket.send(packet);
	}

	/**
	 * A setter method for the underlying hashmap used to store this packet information.
	 *
	 * @param key   The key of the hashmap
	 * @param value The value to be associated with a given key
	 */
	public void put(String key, String value) {
		this.items.put(key, value);
	}

	/**
	 * A getter method for retrieving the value associated with a key of the hashmap used has a storage data structure
	 * of the packet implementation.
	 *
	 * @param key The key of the item to be fetched
	 * @return The value associated with the given key
	 */
	public String get(String key) {
		return items.get(key);
	}

	/**
	 * This method retrieves the reference to the hashmap used in the underlying implementation of this hashmap
	 *
	 * @return The hashmap data structure containing all the items of this packet
	 */
	public HashMap<String, String> getItems() {
		return items;
	}

	/**
	 * This method transforms the information of the hashmap that contains the information of this {@code
	 * MulticastPacket} to bytes in order for this packet to be sent as a datagram.
	 *
	 * @return The byte representation of the items of this {@code MulticastPacket}
	 */
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

	/**
	 * Returns a string representation of the object.
	 * @return A string representation of this object
	 */
	@Override
	public String toString() {
		return this.items.toString();
	}
}
