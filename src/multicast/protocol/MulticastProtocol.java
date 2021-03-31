package multicast.protocol;

import java.util.HashMap;

public interface MulticastProtocol {

	String GREETING = "GREET";
	String ACKNOWLEDGE = "ACK";
	String GOODBYE = "BYE";
	String LIST = "ITEM_LIST";
	String VOTE = "VOTE";
	String OFFER = "OFFER";
	String STATUS = "STATUS";

	// PROTOCOL HEADER

	static MulticastPacket header(String source, String type) {
		MulticastPacket packet = new MulticastPacket();
		packet.addItem("type", type);
		packet.addItem("source", source);
		return packet;
	}

	// ADVERTISEMENT MESSAGES

	static MulticastPacket vote(String source, String option) {
		MulticastPacket packet = MulticastProtocol.header(source, VOTE);
		packet.addItem("option", option);
		return packet;
	}

	static MulticastPacket greeting(String source) {
		return MulticastProtocol.header(source, GREETING);
	}

	static MulticastPacket bye(String source) {
		return MulticastProtocol.header(source, GOODBYE);
	}

	static MulticastPacket offer(String source) {
		return MulticastProtocol.header(source, OFFER);
	}

	// INFORMATIONAL / COMMUNICATION MESSAGES

	static <K, V> MulticastPacket itemList(String source, String target, HashMap<K, V> items) {
		MulticastPacket packet = MulticastProtocol.header(source, LIST);
		packet.addItem("target", target);
		packet.addItem("ITEM_COUNT", String.valueOf(items.size()));
		for (K key : items.keySet()) {
			packet.addItem(key.toString(), items.get(key).toString());
		}
		return packet;
	}

	static MulticastPacket status(String source, String target, String status) {
		MulticastPacket packet = MulticastProtocol.header(source, STATUS);
		packet.addItem("target", target);
		packet.addItem("status", status);
		return packet;
	}

	static MulticastPacket acknowledge(String source, String target) {
		MulticastPacket packet = MulticastProtocol.header(source, ACKNOWLEDGE);
		packet.addItem("target", target);
		return packet;
	}

}
