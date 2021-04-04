package multicast.protocol;

import java.util.HashMap;

public interface MulticastProtocol {

	// DISCOVERY RELATED ADVERTISEMENTS
	String GREETING = "GREET";
	String READY = "READY";
	String OFFER = "OFFER";
	String PROBE = "PROBE";

	// COMMUNICATION TERMINATION ADVERTISEMENT
	String GOODBYE = "BYE";

	// VOTING ADVERTISEMENT
	String VOTE = "VOTE";

	// INFORMATIONAL MESSAGES
	String ACKNOWLEDGE = "ACK";
	String STATUS = "STATUS";
	String LOGIN = "LOGIN";
	String LIST = "ITEM_LIST";


	// PROTOCOL HEADER

	static MulticastPacket header(String source, String type) {
		MulticastPacket packet = new MulticastPacket();
		packet.put("type", type);
		packet.put("source", source);
		return packet;
	}

	// ADVERTISEMENT MESSAGES

	static MulticastPacket vote(String source, String election, String list) {
		MulticastPacket packet = MulticastProtocol.header(source, VOTE);
		packet.put("election", election);
		packet.put("list", list);
		return packet;
	}

	static MulticastPacket greeting(String source) {
		return MulticastProtocol.header(source, GREETING);
	}

	static MulticastPacket ready(String source) {return MulticastProtocol.header(source, READY);}

	static MulticastPacket bye(String source) {
		return MulticastProtocol.header(source, GOODBYE);
	}

	static MulticastPacket offer(String source) {
		return MulticastProtocol.header(source, OFFER);
	}

	static MulticastPacket probe(String source) {return MulticastProtocol.header(source, PROBE);}

	// INFORMATIONAL / COMMUNICATION MESSAGES

	static <K, V> MulticastPacket itemList(String source, String target, HashMap<K, V> items) {
		MulticastPacket packet = MulticastProtocol.header(source, LIST);
		packet.put("target", target);
		packet.put("ITEM_COUNT", String.valueOf(items.size()));
		for (K key : items.keySet()) {
			packet.put(key.toString(), items.get(key).toString());
		}
		return packet;
	}

	static MulticastPacket login(String source, String target, String userID, String pass) {
		MulticastPacket packet = MulticastProtocol.header(source, LOGIN);
		packet.put("target", target);
		packet.put("id", userID);
		packet.put("password", pass);
		return packet;
	}

	static MulticastPacket status(String source, String target, String status) {
		MulticastPacket packet = MulticastProtocol.header(source, STATUS);
		packet.put("target", target);
		packet.put("status", status);
		return packet;
	}

	static MulticastPacket acknowledge(String source, String target) {
		MulticastPacket packet = MulticastProtocol.header(source, ACKNOWLEDGE);
		packet.put("target", target);
		return packet;
	}

}
