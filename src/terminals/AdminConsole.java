package terminals;

import rmi.interfaces.RmiServerInterface;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;

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
                try { Thread.sleep(3000);} catch (Exception ignore) {}
                counter += 3;
                if (counter == timeout) {
                    System.out.println("RMI Server Timed Out");
                    return null;
                }
            }
        }
        return server;
    }

    public int mainMenu() {
        String[] mainMenuOpts = {"Sign Up", "Overview", "Real time Data"};
        return this.choose("Main Menu", mainMenuOpts);
    }

    public Person signUp() {
        int value = 0;
        boolean abortFlag;
        String input = null;
        Person person = null;

        /* Header */
        this.header("Sign Up Menu");

        /* Select Job */
        String[] opts = new String[]{"Student", "Teacher", "Employee"};
        int personType = this.choose("Select Job", opts);

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        // Username
        String username = this.parseString("Name", false);
        abortFlag = (username == null);

        // Password
        String password = this.parseString("Password", abortFlag);
        abortFlag = (password == null);

        // Address
        String address = this.parseString("Address", abortFlag);
        abortFlag = (address == null);

        // Faculty
        String faculty = this.parseString("Faculty", abortFlag);
        abortFlag = (faculty == null);

        // Department
        String department = this.parseString("Department", abortFlag);
        abortFlag = (department == null);

        // Phone Number
        int phoneNumber = this.parsePositiveInt("Phone Number", abortFlag);
        abortFlag = (phoneNumber == -1);

        // Identity Card Number
        int identityCardNumber = this.parsePositiveInt("Identity Card Number", abortFlag);
        abortFlag = (identityCardNumber == -1);

        // Identity Card Expiry Date
        GregorianCalendar identityCardExpiryDate = this.parseDate("Identity Card Expiry Date", abortFlag);
        abortFlag = (identityCardExpiryDate == null);

        /* Create Person Object */
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
