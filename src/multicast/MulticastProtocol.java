package multicast;

public interface MulticastProtocol {

    static MulticastPacket greeting(String origin) {
        MulticastPacket packet = new MulticastPacket();
        packet.addItem("type", "greet");
        packet.addItem("origin", origin);
        return packet;
    }

}
