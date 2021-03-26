import rmi.RmiServer;
import terminals.VotingTerminal;

import utils.elections.StudentElection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Vector;


public class Main {

    public static void main(String[] args) throws ParseException {
        System.out.println("Hello SD-Project");

        // Terminal UI
        VotingTerminal vt = new VotingTerminal();
        String [] opts = {"Encontrar", "Fazer", "Procurar", "Listar"};
        int opt = vt.choose("Main Menu", opts);
        System.out.println("The program is: " + opts[opt - 1]);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        sdf.setLenient(false);
        sdf.parse(s);
        System.out.println(sdf.getCalendar().getTime());
    }
}
