import terminals.VotingTerminal;
import udp.UdpClient;
import udp.UdpServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello SD-Project");

        // Terminal UI
        VotingTerminal vt = new VotingTerminal();
        String [] opts = {"Encontrar", "Fazer", "Procurar", "Listar"};
        int opt = vt.launchUI("Main Menu", opts);
        System.out.println("The program is: " + opts[opt]);

        // folder UDP
        UdpServer server = new UdpServer(1235);
        UdpClient c1 = new UdpClient(1234);
        server.start();
        c1.start();
    }
}
