package terminals;

import rmi.interfaces.RmiServerInterface;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class AdminConsole extends Terminal {

    public AdminConsole() {
        super();
    }

    public RmiServerInterface connect() {
        RmiServerInterface server;
        long counter = 0, timeout = 30;
        while (true) {
            try {
                server = (RmiServerInterface) Naming.lookup("RmiServer");
                server.ping();
                break;
            } catch (Exception e) {
                for (int i = 0; i < 3; ++i) {
                    System.out.println("No Server available. Timeout in " + (timeout - counter++) + "s");
                    System.out.println("Trying to reconnect in " + (3 - i));
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignore) {
                    }
                    this.clear();
                    if (counter == timeout) {
                        System.out.println("RMI Server Timed Out");
                        return null;
                    }
                }
            }
        }
        return server;
    }

    public int mainMenu() {
        String[] mainMenuOpts = {"Sign Up", "Overview", "Real time Data"};
        return this.launchUI("Main Menu", mainMenuOpts).getOption(mainMenuOpts.length);
    }

    public Person signUp() {
        boolean abortFlag = false;
        int value = 0;
        String input = null;
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Person person = null;

        /* Header */
        this.launchUI("Sign Up Menu", new String[]{});

        /* Select Job */
        String[] opts = new String[]{"Student", "Teacher", "Employee"};
        int personType = this.launchUI("Select Job", opts).getOption(opts.length);

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        /* Username */
        while (!abortFlag) {
            System.out.print("Name: ");
            input = sc.nextLine();

            if (input.equals("QUIT")) {
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Name!");
        }
        String username = input;

        /* Password */
        while (!abortFlag) {
            System.out.print("Password: ");
            input = sc.nextLine();

            if (input.equals("QUIT")) {
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Password!");
        }
        String password = input;

        /* Address */
        while (!abortFlag) {
            System.out.print("Address: ");
            input = sc.nextLine();

            if (input.equals("QUIT")) {
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String address = input;

        /* Faculty */
        while (!abortFlag) {
            System.out.print("Faculty: ");
            input = sc.nextLine();
            if (input.equals("QUIT")) {
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String faculty = input;

        /* Department */
        while (!abortFlag) {
            System.out.print("Department: ");
            input = sc.nextLine();

            if (input.equals("QUIT")) {
                abortFlag = true;
                System.out.println("Aborting...");
            }

            if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                break;

            System.out.println("Invalid Address!");
        }
        String department = input;

        /* Phone Number */
        while (!abortFlag) {
            try {
                System.out.print("Phone Number: ");
                input = sc.nextLine();

                if (input.equals("QUIT")) {
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
        while (!abortFlag) {
            try {
                System.out.print("Identity Card Number: ");
                input = sc.nextLine();

                if (input.equals("QUIT")) {
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
        int identityCardNumber = value;

        /* Identity Card Expiry Date */
        while (!abortFlag) {
            try {
                System.out.print("Identity Card Expiry Date [dd/mm/yyyy]: ");
                input = sc.nextLine();

                if (input.equals("QUIT")) {
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

        if (!abortFlag) {
            switch (personType) {
                case 1:
                    person = new Student(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
                    break;
                case 2:
                    person = new Teacher(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
                    break;
                case 3:
                    person = new Employee(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
                    break;
            }
        }
        System.out.println(identityCardExpiryDate.getTime());
        return person;
    }

    public void overview(RmiServerInterface server) throws RemoteException {
        server.print("olaaaaa");
    }

    public void realTimeData() {
    }

    public static void main(String[] args) {
        Person person;
        AdminConsole admin = new AdminConsole();
        admin.clear();
        RmiServerInterface server = admin.connect();
        if (server == null) return;

        while (true) {
            int optMainMenu = admin.mainMenu();
            while (optMainMenu != 0) {
                admin.clear();
                switch (optMainMenu) {
                    case 1:
                        person = admin.signUp();
                        while (true) {
                            try {
                                if (person != null)
                                    server.signUp(person);
                                break;
                            } catch (Exception e) {
                                System.out.println(e);
                                server = admin.connect();
                                if (server == null) return;
                            }
                        }
                        break;
                    case 2:
                        while (true) {
                            try {
                                admin.overview(server);
                                break;
                            } catch (Exception e) {
                                server = admin.connect();
                                if (server == null) return;
                            }
                        }
                        break;
                    case 3:
                        while (true) {
                            try {
                                admin.realTimeData();
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                                server = admin.connect();
                                if (server == null) return;
                            }
                        }
                        break;
                }
                admin.clear();
                optMainMenu = admin.mainMenu();
            }
        }
    }

}
