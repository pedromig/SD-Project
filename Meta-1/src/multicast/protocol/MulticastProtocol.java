package multicast.protocol;

import java.util.HashMap;


/**
 * A interface that provides the {@code MulticastProtocol} standard to be used in the communication between
 * {@link multicast.VotingDesk} and the {@link multicast.VotingTerminal}. Any voting terminal that is to be used with
 * the voting desk following this implementation should implement this interface in order to provide compatibility and
 * stability to the implementations.
 * <p>
 * This API was made for easy of use and  to provide more readability to the code. If you dont want to use this API
 * feel free but be aware that a wrong implementation of the messages that does not follow this protocol might result
 * in the client-server communication not working.
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 * @see multicast.protocol.MulticastPacket
 */
public interface MulticastProtocol {

	/**
	 * @implNote DISCOVERY RELATED ADVERTISEMENTS
	 */
	String GREETING = "GREET";
	String READY = "READY";
	String OFFER = "OFFER";
	String PROBE = "PROBE";

	/**
	 * @implNote COMMUNICATION TERMINATION ADVERTISEMENT
	 */
	String GOODBYE = "BYE";

	/**
	 * @implSpec VOTING ADVERTISEMENT
	 */
	String VOTE = "VOTE";

	/**
	 * @implNote INFORMATIONAL MESSAGES
	 */
	String ACKNOWLEDGE = "ACK";
	String STATUS = "STATUS";
	String LOGIN = "LOGIN";
	String LIST = "ITEM_LIST";


	/**
	 * A simple header structure that all packets in this protocol use. Most of the packets only store information in
	 * the header being but others may add more information to it. This a standard template for all the messages that
	 * has a common message pattern.
	 *
	 * @param source The sender of a message
	 * @param type   The type of the message being sent
	 * @return The {@code MulticastPacket} containing the header info.
	 * @implSpec PROTOCOL HEADER
	 */
	static MulticastPacket header(String source, String type) {
		MulticastPacket packet = new MulticastPacket();
		packet.put("type", type);
		packet.put("source", source);
		return packet;
	}


	/**
	 * A {@code MulticastProtocol} message used to transmit a vote from the client to the {@code VotingDesk} server.
	 *
	 * @param source   The sender of the message
	 * @param election The election that is associated with this vote
	 * @param list     The list that a person voted.
	 * @return The {@code MulticastPacket} containing the vote info
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket vote(String source, String election, String list) {
		MulticastPacket packet = MulticastProtocol.header(source, VOTE);
		packet.put("election", election);
		packet.put("list", list);
		return packet;
	}

	/**
	 * A greeting message used by the {@code VotingTerminal} clients
	 *
	 * @param source The sender of the message
	 * @return The {@code MulticastPacket} containing the greeting
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket greeting(String source) {
		return MulticastProtocol.header(source, GREETING);
	}

	/**
	 * A message used by the {@code VotingTerminal} clients when they are ready to receive users
	 *
	 * @param source The sender of the message
	 * @return The {@code MulticastPacket} containing the ready information
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket ready(String source) {return MulticastProtocol.header(source, READY);}

	/**
	 * A message used by the {@code VotingTerminal} clients when they perform a successful shutdown. This is
	 * used to tell the server to remove the terminal from the list of active terminals.
	 *
	 * @param source The sender of the message
	 * @return The {@code MulticastPacket} containing the goodbye information
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket bye(String source) {
		return MulticastProtocol.header(source, GOODBYE);
	}

	/**
	 * A message used by the {@code VotingTerminal} clients when they are finished processing a user and are
	 * ready to receive one more job
	 *
	 * @param source The sender of the message
	 * @return The {@code MulticastPacket} containing the offer information
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket offer(String source) {
		return MulticastProtocol.header(source, OFFER);
	}

	/**
	 * A message used by the {@code VotingDesk} server on startup. This probe is sent scanning for any {@code
	 * VotingTerminal} client connected to the multicast discovery group and connecting with them.
	 *
	 * @param source The sender of the message
	 * @return The {@code MulticastPacket} containing the probe
	 * @implNote ADVERTISEMENT MESSAGES
	 */
	static MulticastPacket probe(String source) {return MulticastProtocol.header(source, PROBE);}


	/**
	 * A message used by the {@code VotingTerminal} clients or the {@code VotingDesk} servers when there is a
	 * need to send a list of items across the multicast group
	 *
	 * @param source The sender of the message
	 * @param target The target receiver of the message
	 * @param items  The item list contained in the message as a hashmap
	 * @return The {@code MulticastPacket} containing the item list
	 * @implNote INFORMATIONAL / COMMUNICATION MESSAGES
	 */
	static <K, V> MulticastPacket itemList(String source, String target, HashMap<K, V> items) {
		MulticastPacket packet = MulticastProtocol.header(source, LIST);
		packet.put("target", target);
		packet.put("ITEM_COUNT", String.valueOf(items.size()));
		for (K key : items.keySet()) {
			packet.put(key.toString(), items.get(key).toString());
		}
		return packet;
	}

	/**
	 * A message used by the {@code VotingTerminal} clients when there is a
	 * need to transmit a login message contain user critical information e.g credentials
	 *
	 * @param source The sender of the message
	 * @param target The target receive of this message
	 * @param userID The id of the user attempting to login
	 * @param pass   The password of the user attempting to login
	 * @return The {@code MulticastPacket} containing the item list
	 * @implNote INFORMATIONAL / COMMUNICATION MESSAGES
	 */
	static MulticastPacket login(String source, String target, String userID, String pass) {
		MulticastPacket packet = MulticastProtocol.header(source, LOGIN);
		packet.put("target", target);
		packet.put("id", userID);
		packet.put("password", pass);
		return packet;
	}

	/**
	 * A message used by the {@code VotingTerminal} clients when there is a
	 * need to transmit a status message containing the result code of a given operation
	 *
	 * @param source The sender of the message
	 * @param target The target receiver of this message
	 * @param status The status code to be sent
	 * @return The {@code MulticastPacket} containing the status message
	 * @implNote INFORMATIONAL / COMMUNICATION MESSAGES
	 */
	static MulticastPacket status(String source, String target, String status) {
		MulticastPacket packet = MulticastProtocol.header(source, STATUS);
		packet.put("target", target);
		packet.put("status", status);
		return packet;
	}

	/**
	 * A acknowledgement message used by the {@code VotingTerminal} and {@code VotingDesk} to confirm that a given
	 * operation took place or is completed.
	 *
	 * @param source The sender of the message
	 * @param target The target receiver of this message
	 * @return The {@code MulticastPacket} containing the acknowledgement message
	 * @implNote INFORMATIONAL / COMMUNICATION MESSAGES
	 */
	static MulticastPacket acknowledge(String source, String target) {
		MulticastPacket packet = MulticastProtocol.header(source, ACKNOWLEDGE);
		packet.put("target", target);
		return packet;
	}

}
