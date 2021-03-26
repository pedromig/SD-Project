import rmi.RmiServer;
import terminals.VotingTerminal;

import utils.elections.StudentElection;

import java.util.Vector;


public class Main {

    public static void main(String[] args) {
        System.out.println("Hello SD-Project");

        // Terminal UI
        VotingTerminal vt = new VotingTerminal();
        String [] opts = {"Encontrar", "Fazer", "Procurar", "Listar"};
        int opt = vt.launchUI("Main Menu", opts).getOption(opts.length);
        System.out.println("The program is: " + opts[opt - 1]);
    }
}
