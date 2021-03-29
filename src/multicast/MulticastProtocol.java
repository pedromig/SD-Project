package multicast;


import java.util.HashMap;

public interface MulticastProtocol {

	String GREETING = "GREET";
	String ACKNOWLEDGE = "ACK";
	String GOODBYE = "BYE";
	String LIST = "ITEM_LIST";

	static MulticastPacket greeting(String source) {
		MulticastPacket packet = new MulticastPacket();
		packet.addItem("type", GREETING);
		packet.addItem("source", source);
		return packet;
	}

	static MulticastPacket acknowledge(String source, String target) {
		MulticastPacket packet = new MulticastPacket();
		packet.addItem("type", ACKNOWLEDGE);
		packet.addItem("source", source);
		packet.addItem("target", target);
		return packet;
	}

	static <K, V> MulticastPacket itemList(String source, String target, HashMap<K, V> items) {
		MulticastPacket packet = new MulticastPacket();
		packet.addItem("type", LIST);
		packet.addItem("source", source);
		packet.addItem("target", target);
		packet.addItem("ITEM_COUNT", String.valueOf(items.size()));
		for (K key : items.keySet()) {
			packet.addItem(key.toString(), items.get(key).toString());
		}
		return packet;
	}


}
