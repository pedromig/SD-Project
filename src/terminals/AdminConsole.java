package terminals;

import rmi.interfaces.RmiClientInterface;
import rmi.interfaces.RmiServerInterface;
import utils.lists.EmployeeList;
import utils.lists.List;
import utils.elections.Election;
import utils.elections.EmployeeElection;
import utils.elections.StudentElection;
import utils.elections.TeacherElection;
import utils.lists.StudentList;
import utils.lists.TeacherList;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdminConsole extends UnicastRemoteObject implements RmiClientInterface {
    protected Parser parser;
    public AdminConsole() throws RemoteException {
        super();
        this.parser = new Parser();
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
        String[] mainMenuOpts = {"Sign Up", "Say Olaaaaa", "Create Election", "Manage Lists"};
        return this.parser.choose("Main Menu", mainMenuOpts);
    }

    public Person signUpMenu() {
        boolean abortFlag = false;

        /* Header */
        this.parser.header("Sign Up Menu");

        /* Select Job */
        String[] opts = new String[]{"Student", "Teacher", "Employee"};
        int personType = this.parser.choose("Select Job", opts);

        if (personType == 0) abortFlag = true;

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        // Username
        String username = this.parser.parseString("Name", abortFlag);
        abortFlag = (username == null);

        // Password
        String password = this.parser.parseString("Password", abortFlag);
        abortFlag = (password == null);

        // Address
        String address = this.parser.parseString("Address", abortFlag);
        abortFlag = (address == null);

        // Faculty
        String faculty = this.parser.parseString("Faculty", abortFlag);
        abortFlag = (faculty == null);

        // Department
        String department = this.parser.parseString("Department", abortFlag);
        abortFlag = (department == null);

        // Phone Number
        int phoneNumber = this.parser.parsePositiveInt("Phone Number", abortFlag);
        abortFlag = (phoneNumber == -1);

        // Identity Card Number
        int identityCardNumber = this.parser.parsePositiveInt("Identity Card Number", abortFlag);
        abortFlag = (identityCardNumber == -1);

        // Identity Card Expiry Date
        GregorianCalendar identityCardExpiryDate = this.parser.parseDate("Identity Card Expiry Date", abortFlag);
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
        this.parser.header("Create Election");

        /* Select Type */
        String[] opts = {"Student Election", "Teacher Election", "Employee Election"};
        int electionType = this.parser.choose("Election Type", opts);

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        if (electionType == 0) abortFlag = true;

        // Election Name
        String electionName = this.parser.parseString("Election name", abortFlag);
        abortFlag = (electionName == null);

        // Election Description
        String description = this.parser.parseString("Description", abortFlag);
        abortFlag = (description == null);

        // Election Start Date and Time
        GregorianCalendar startDate = this.parser.parseDateTime("Start Date/Time", abortFlag);
        abortFlag = (startDate == null);

        // Election Start Time
        GregorianCalendar endDate = this.parser.parseDateTime("End Date/Time", abortFlag);
        abortFlag = (endDate == null);

        if (!abortFlag){
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

    public int manageListsMenu(){
        String[] manageListsOpts = {"Create List", "Add List to Election", "Remove List from Election", "Add People to List", "Remove People from List"};
        return this.parser.choose("Manage Lists", manageListsOpts);
    }

    public List<? extends Person> createListMenu(){
        boolean abortFlag = false;

        /* Header */
        this.parser.header("Create List");

        /* Select List Type */
        String[] opts = new String[]{"Student List", "Teacher List", "Employee List"};
        int listType = this.parser.choose("Select List Type", opts);

        /* Insert Information */
        System.out.println("Enter \"QUIT\" to abort the operation at any time.");

        if (listType == 0) abortFlag = true;

        // Name
        String listName = this.parser.parseString("List Name", abortFlag);
        abortFlag = (listName == null);

        if (!abortFlag) {
            switch (listType) {
                case 1:
                    return new StudentList(listName);
                case 2:
                    return new TeacherList(listName);
                case 3:
                    return new EmployeeList(listName);
            }
        }
        return null;
    }

    public int chooseElectionsMenu(CopyOnWriteArrayList<Election<?>> elections) {
        ArrayList<String> electionNames = new ArrayList<>();
        for (Election<?> e: elections){
            electionNames.add(e.getName());
        }
        String[] electionOptionNames = electionNames.toArray(new String[0]);
        return this.parser.choose("Choose Election", electionOptionNames);
    }

    public int chooseListsMenu(CopyOnWriteArrayList<List<?>> lists){
        ArrayList<String> listNames = new ArrayList<>();
        for (List<?> l: lists){
            listNames.add(l.getName());
        }
        String[] electionOptionNames = listNames.toArray(new String[0]);
        return this.parser.choose("Choose List", electionOptionNames);
    }

    public int choosePeopleMenu(CopyOnWriteArrayList<Person> people){
        ArrayList<String> listNames = new ArrayList<>();
        for (Person p: people){
            listNames.add(p.getName());
        }
        String[] electionOptionNames = listNames.toArray(new String[0]);
        return this.parser.choose("Choose Person", electionOptionNames);
    }


    public void getDatabaseInfo(RmiServerInterface server) throws RemoteException {
        server.info(this);
    }

    /* ################################################################### */

    public static void main(String[] args) {
        int optElection, optList, optPeople;
        Election<?> selectedElection;
        CopyOnWriteArrayList<Election<?>> futureElections;
        CopyOnWriteArrayList<List<?>> lists;
        CopyOnWriteArrayList<Person> people;
        AdminConsole admin;
        try {
            admin = new AdminConsole();
        } catch (Exception e){
            System.out.println("Could not start Admin Console");
            return;
        }
        admin.parser.clear();
        RmiServerInterface server = admin.connect();

        if (server == null) return;

        int optMainMenu = admin.mainMenu();
        while (optMainMenu != 0) {
            admin.parser.clear();
            switch (optMainMenu) {
                /* SIGN UP */
                case 1:
                    Person person = admin.signUpMenu();
                    if (person != null) {
                        while (true) {
                            try {
                                server.signUp(person);
                                break;
                            } catch (Exception e) {
                                System.out.println("[DEBUG]");
                                e.printStackTrace();
                                server = admin.connect();
                                if (server == null) return;
                            }
                        }
                    }
                    break;

                case 2:
                    while (true) {
                        try {
                            admin.getDatabaseInfo(server);
                            break;
                        } catch (Exception e) {
                            System.out.println("[DEBUG]");
                            e.printStackTrace();
                            server = admin.connect();
                            if (server == null) return;
                        }
                    }
                    admin.parser.getEnter();
                    break;

                /* Create Election */
                case 3:
                    Election<?> election = admin.createElectionMenu();
                    if (election != null) {
                        while (true) {
                            try {
                                server.createElection(election);
                                break;
                            } catch (Exception e) {
                                System.out.println("[DEBUG]");
                                e.printStackTrace();
                                server = admin.connect();
                                if (server == null) return;
                            }
                        }
                    }
                    break;

                /* Manage Election Lists */
                case 4:
                    int manageListsOpt = admin.manageListsMenu();
                    while(manageListsOpt != 0) {
                        admin.parser.clear();
                        switch (manageListsOpt){
                            /* Create Election List*/
                            case 1:
                                List<?> list = admin.createListMenu();
                                if (list != null) {
                                    while (true) {
                                        try {
                                            server.createList(list);
                                            break;
                                        } catch (Exception e){
                                            System.out.println("[DEBUG]");
                                            e.printStackTrace();
                                            server = admin.connect();
                                            if (server == null) return;
                                        }
                                    }
                                }
                                break;
                            /* Add Election List to Election */
                            case 2:
                                while (true) {
                                    try {
                                        futureElections = server.getFutureElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if (server == null) return;
                                    }
                                }

                                optElection = admin.chooseElectionsMenu(futureElections);
                                if (optElection == 0) break;
                                selectedElection = futureElections.get(optElection - 1);
                                while (true) {
                                    try {
                                        lists = server.getListsUnassignedOfType(selectedElection.getType());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optList = admin.chooseListsMenu(lists);
                                if (optList == 0) break;

                                while (true) {
                                    try {
                                        server.associateListToElection(selectedElection.getName(), lists.get(optList - 1).getName());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                break;

                            /* Remove Election List From Election */
                            case 3:
                                while (true) {
                                    try {
                                        futureElections = server.getFutureElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optElection = admin.chooseElectionsMenu(futureElections);
                                if (optElection == 0) break;
                                selectedElection = futureElections.get(optElection - 1);
                                while (true) {
                                    try {
                                        lists = server.getListsAssignedOfType(selectedElection.getType(), selectedElection.getName());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optList = admin.chooseListsMenu(lists);
                                if (optList == 0) break;

                                while (true) {
                                    try {
                                        server.associateListToElection(null, lists.get(optList - 1).getName());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }
                                break;

                            /* Add Person To List*/
                            case 4:
                                while (true) {
                                    try {
                                        lists = server.getEditableLists();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optList = admin.chooseListsMenu(lists);
                                if (optList == 0) break;

                                list = lists.get(optList - 1);

                                while (true) {
                                    try {
                                        people = server.getPeopleUnassignedOfType(list.getType());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optPeople = admin.choosePeopleMenu(people);
                                if (optPeople == 0) break;
                                while (true) {
                                    try {
                                        server.associatePersonToList(list.getName(), people.get(optPeople - 1).getIdentityCardNumber());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }
                                break;

                            /* Remove Person From List*/
                            case 5:
                                while (true) {
                                    try {
                                        lists = server.getEditableLists();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optList = admin.chooseListsMenu(lists);
                                if (optList == 0) break;

                                list = lists.get(optList - 1);

                                while (true) {
                                    try {
                                        people = server.getPeopleAssignedOfType(list.getType(), list.getName());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }

                                optPeople = admin.choosePeopleMenu(people);
                                if (optPeople == 0) break;

                                while (true) {
                                    try {
                                        server.associatePersonToList(null, people.get(optPeople - 1).getIdentityCardNumber());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect();
                                        if(server == null) return;
                                    }
                                }
                                break;
                        }

                        admin.parser.clear();
                        manageListsOpt = admin.manageListsMenu();
                    }
                    break;

            }
            admin.parser.clear();
            optMainMenu = admin.mainMenu();
        }
        System.exit(0);
    }

}
