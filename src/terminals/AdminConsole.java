package terminals;

import rmi.interfaces.RmiServerInterface;
import utils.people.Person;
import utils.people.Student;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class AdminConsole extends Terminal {

    public AdminConsole(){
        super();
    }

    public int mainMenu(){
        String[] mainMenuOpts = {"Sign Up", "Overview","Real time Data"};
        return this.launchUI("Main Menu", mainMenuOpts).getOption(mainMenuOpts.length);
    }

    public void signUp() {
        boolean abortFlag = false;
        int value = 0;
        String input = null;
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.launchUI("Sign Up Menu", new String[]{});
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");
        /* Username */
        while (!abortFlag) {
            System.out.print("Name: ");
            input = sc.nextLine();

            if (input.equals("QUIT")){
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (!input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Name!");
        }
        String username = input;

        /* Password */
        while (!abortFlag) {
            System.out.print("Password: ");
            input = sc.nextLine();

            if (input.equals("QUIT")){
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (!input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Password!");
        }
        String password = input;

        /* Address */
        while (!abortFlag) {
            System.out.print("Address: ");
            input = sc.nextLine();

            if (input.equals("QUIT")){
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (!input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String address = input;

        /* Faculty */
        while (!abortFlag) {
            System.out.print("Faculty: ");
            input = sc.nextLine();
            if (input.equals("QUIT")){
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (!input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String faculty = input;

        /* Department */
        while (!abortFlag) {
            System.out.print("Department: ");
            input = sc.nextLine();

            if (input.equals("QUIT")){
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (!input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String department = input;

        /* Phone Number */
        while (!abortFlag){
            try {
                System.out.print("Phone Number: ");
                input = sc.nextLine();

                if (input.equals("QUIT")){
                    abortFlag = true;
                    System.out.println("Aborting...");
                    break;
                }

                value = Integer.parseInt(input);
                break;
            } catch (Exception e) {
                System.out.println("Invalid Number!");
            }
        }
        int phoneNumber = value;

        /* Identity Card Number */
        while (!abortFlag){
            try {
                System.out.print("Identity Card Number: ");
                input = sc.nextLine();

                if (input.equals("QUIT")){
                    abortFlag = true;
                    System.out.println("Aborting...");
                    break;
                }

                value = Integer.parseInt(input);
                break;
            } catch (Exception e){
                System.out.println("Invalid Number!");
            }
        }
        int identityCardNumber = value;

        /* Identity Card Expiry Date */
        while (!abortFlag) {
            try {
                System.out.print("Identity Card Expiry Date [dd/mm/yyyy]: ");
                input = sc.nextLine();

                if (input.equals("QUIT")){
                    abortFlag = true;
                    System.out.println("Aborting...");
                }
                sdf.parse(input);
                break;
            } catch (Exception e) {
                System.out.println("Invalid Date!");
            }
        }
        GregorianCalendar identityCardExpiryDate = (GregorianCalendar) sdf.getCalendar();

        if (!abortFlag){
            Person p = new Student(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
        }

    }

    public void overview(RmiServerInterface server) throws RemoteException {
        server.print("olaaaaa");
    }

    public void realTimeData(){

    }

    public static void main(String[] args) {
        RmiServerInterface server = null, lastServer = null;
        AdminConsole admin = new AdminConsole();
        admin.clear();
        while(true){
            try {
                while(true) {
                    // Trying to find an RMI Server
                    try {
                        server = (RmiServerInterface) Naming.lookup("RmiServer");
                        if (!server.equals(lastServer)){
                            if (lastServer != null)
                                System.out.println("Please Re-enter the previous form.");
                            break;
                        }

                        /* Animation */
                        System.out.print("Waiting for available server");
                        for (int i = 0; i < 3; i++){
                            System.out.print(".");
                            Thread.sleep(1000);
                        }
                        admin.clear();
                    } catch (Exception ignore){}
                }

                /* User Interface*/
                int optMainMenu = admin.mainMenu();
                while(optMainMenu != 0){
                    admin.clear();
                    switch (optMainMenu) {
                        case 1:
                            admin.signUp();
                            break;
                        case 2:
                            admin.overview(server);
                            break;
                        case 3:
                            admin.realTimeData();
                            break;
                    }
                    admin.clear();
                    optMainMenu = admin.mainMenu();
                }
            } catch (Exception e){
                /* Server crash Handling */
                System.out.println("Current Server Stopped Responding... Switching Servers!");
                lastServer = server;
            }
        }
    }

}
