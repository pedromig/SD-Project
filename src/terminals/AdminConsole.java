package terminals;

import rmi.interfaces.RmiServerInterface;
import utils.elections.Election;
import utils.elections.EmployeeElection;
import utils.elections.StudentElection;
import utils.elections.TeacherElection;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Date;
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

    /* ############################## Menus ############################## */

    public int mainMenu() {
        String[] mainMenuOpts = {"Sign Up", "Say Olaaaaa", "Create Election"};
        return this.choose("Main Menu", mainMenuOpts);
    }

    public Person signUpMenu() {
        boolean abortFlag = false;

        /* Header */
        this.header("Sign Up Menu");

        /* Select Job */
        String[] opts = new String[]{"Student", "Teacher", "Employee"};
        int personType = this.choose("Select Job", opts);

        if (personType == 0) abortFlag = true;

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        // Username
        String username = this.parseString("Name", abortFlag);
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
                    return new Student(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
                case 2:
                    return new Teacher(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
                case 3:
                    return new Employee(username, password, address, faculty, department, phoneNumber, identityCardNumber, identityCardExpiryDate);
            }
        }
        return null;
    }

    public Election<? extends Person> createElectionMenu(){
        boolean abortFlag = false;

        /* Header */
        this.header("Create Election");

        /* Select Type */
        String[] opts = {"Student Election", "Teacher Election", "Employee Election"};
        int electionType = this.choose("Election Type", opts);

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        if (electionType == 0) abortFlag = true;

        // Election Name
        String electionName = this.parseString("Election name", abortFlag);
        abortFlag = (electionName == null);

        // Election Description
        String description = this.parseString("Description", abortFlag);
        abortFlag = (description == null);

        // Election Start Date
        GregorianCalendar startDate = this.parseDate("Start Date", abortFlag);
        abortFlag = (startDate == null);

        // Election Start Time
        Date startTime = this.parseTime("Start Time", abortFlag);
        abortFlag = (startTime == null);

        GregorianCalendar endDate = this.parseDate("End Date", abortFlag);
        abortFlag = (endDate == null);

        // Election Start Time
        Date endTime = this.parseTime("End Time", abortFlag);
        abortFlag = (endTime == null);

        if (!abortFlag){
            startDate.setTime(startTime);
            endDate.setTime(endTime);
            switch (electionType) {
                case 1:
                    return new StudentElection(electionName, description, startDate, endDate);
                case 2:
                    return new TeacherElection(electionName, description, startDate, endDate);
                case 3:
                    return new EmployeeElection(electionName, description, startDate, endDate);
            }
        }
        return null;
    }

    public void sayOlaaaaa(RmiServerInterface server) throws RemoteException {
        server.print("olaaaaa");
        server.info();
    }

    /* ################################################################### */
    public static void main(String[] args) {
        Person person;
        Election<?> election;
        AdminConsole admin = new AdminConsole();
        admin.clear();
        RmiServerInterface server = admin.connect();
        if (server == null)
            return;

        int optMainMenu = admin.mainMenu();
        while (optMainMenu != 0) {
            admin.clear();
            switch (optMainMenu) {
                case 1:
                    person = admin.signUpMenu();
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
                            admin.sayOlaaaaa(server);
                            break;
                        } catch (Exception e) {
                            server = admin.connect();
                            if (server == null) return;
                        }
                    }
                    break;
                case 3:
                    election = admin.createElectionMenu();
                    while (true) {
                        try {
                            server.createElection(election);
                            break;
                        } catch (Exception e) {
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
