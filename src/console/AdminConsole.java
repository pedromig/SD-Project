package console;

import rmi.interfaces.RmiAdminConsoleInterface;
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

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An implementation of an Administrator Console
 */
public class AdminConsole extends UnicastRemoteObject implements RmiAdminConsoleInterface, Serializable {
    protected boolean realTimeDesks;
    protected String realTimeElectionName;
    protected Parser parser;

    /**
     * Builder
     * @throws RemoteException
     */
    public AdminConsole() throws RemoteException {
        super();
        this.parser = new Parser();
        this.realTimeDesks = false;
        this.realTimeElectionName = null;
    }

    /* ############################## Rmi Related Methods ############################## */

    /**
     * A remote getter implementation for the realTimeElectionName attribute
     * @return this realTimeElectionName
     * @throws RemoteException
     */
    @Override
    public String getRealTimeElectionName() throws RemoteException {
        return this.realTimeElectionName;
    }

    /**
     * A function to connect the Administrator console to the running RMI server.
     * It tries to connect to a running RMI server within a gap of 3s repeatedly
     * @return RmiServerInterface object if connection is successful (reconnection < 30s), null otherwise
     */
    public RmiServerInterface connect(String ip, String port) {
        RmiServerInterface server;
        long counter = 0, timeout = 30;
        while (true) {
            try {
                server = (RmiServerInterface) Naming.lookup("rmi://" + ip + ":" + port  + "/RmiServer");
                server.subscribe((RmiAdminConsoleInterface) this);
                break;
            } catch (Exception e) {
                System.out.println("[DEBUG]");
                e.printStackTrace();
                try { Thread.sleep(3000);} catch (Exception ignore) {}
                counter += 3;
                if (counter == timeout) {
                    System.out.println("RMI Server Timed Out");
                    System.exit(0);
                    return null;
                }
            }
        }
        return server;
    }

    /**
     * A RMI callback for listing the database information
     * @param server RMI server interface object
     * @throws RemoteException
     */
    public void getDatabaseInfo(RmiServerInterface server) throws RemoteException {
        server.info(this);
    }

    /**
     * A RMI callback to get the results of all ended elections
     * @param server RmiServerInterface object corresponding to the primary RMI server
     * @param endedElections CopyOnWriteArrayList of all ended Elections
     */
    public void showEndedElectionStatsMenu(RmiServerInterface server, CopyOnWriteArrayList<Election<?>> endedElections) {
        System.out.println("\n*************************************************************************");
        for (Election<?> election : endedElections) {
            try {
                server.printVotingProcessedData(this, election);
            } catch (Exception e) {
                System.out.println("[DEBUG]");
                e.printStackTrace();
            }
        }
        System.out.println("\n*************************************************************************");

    }

    /* ############################## Menus ############################## */

    /**
     * Main Menu of the Administrator Console
     * @return Option Selected
     */
    public int mainMenu() {
        String[] mainMenuOpts = {"Sign Up", "Database Info", "Manage Elections", "Manage Lists", "Real Time Data"};
        return this.parser.choose("Main Menu", mainMenuOpts);
    }

    /**
     * Sign Up Menu of the Administrator Console
     * @return Person object with the respective attributes, null if the operation is cancelled
     */
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

    /**
     * Create Election Menu of the Administrator Console
     * @return Election object with the respective type and attributes, null if the operation is cancelled
     */
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
        GregorianCalendar startDate;
        do {
            startDate = this.parser.parseDateTime("Start Date/Time", abortFlag);
            abortFlag = (startDate == null);
        } while ((startDate != null) && (startDate.getTimeInMillis() < new GregorianCalendar().getTimeInMillis()));

        // Election Start Time
        GregorianCalendar endDate;
        do {
            endDate = this.parser.parseDateTime("End Date/Time", abortFlag);
            abortFlag = (endDate == null);
        } while((startDate != null) && (endDate != null) && (endDate.getTimeInMillis() <= startDate.getTimeInMillis()));

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

    /**
     * Manage Lists Menu of the Administrator Console
     * @return Option Selected
     */
    public int manageListsMenu(){
        String[] manageListsOpts = {"Create List", "Add List to Election", "Remove List from Election", "Add People to List", "Remove People from List"};
        return this.parser.choose("Manage Lists", manageListsOpts);
    }

    /**
     * Manage Elections Menu of the Administrator Console
     * @return Option Selected
     */
    public int manageElectionsMenu() {
        String[] manageElectionsOpts = {"Create Election", "Edit Election", "Ended Elections Log", "Person Audit"};
        return this.parser.choose("Manage Elections",manageElectionsOpts);
    }

    /**
     * Edit Elections Menu of the Administrator Console
     * @return Option Selected
     */
    public int editElectionsMenu() {
        String[] opts = new String[] {"Edit Name", "Edit Description", "Edit Start Date", "Edit End Date", "Add Department", "Remove Department", "Add Restraint", "Remove Restraint"};
        return this.parser.choose("Edit Options", opts);
    }

    /**
     * List of departments available to be added on the add Department Menu
     * Shows all departments except the ones already associated with the given election
     * @param election respective Election object
     * @param departments String array of all departments
     * @return String array of the available department names
     */
    public String[] addDepartmentMenuFilter(Election<?> election, String[] departments) {
        ArrayList<String> selectableDepartments = new ArrayList<>();
        for (String deptName : departments) {
            if (!election.getDepartments().contains(deptName)){
                selectableDepartments.add(deptName);
            }
        }
        return selectableDepartments.toArray(new String[0]);
    }

    /**
     * List of departments available to be added on the add Restriction Menu
     * Shows all possible restrictions except the ones already associated with the given election
     * @param election respective Election object
     * @param departments String array of all departments
     * @return String array of the available department names
     */
    public String[] addRestrictionMenuFilter(Election<?> election, String[] departments) {
        ArrayList<String> selectableDepartments = new ArrayList<>();
        for (String deptName : departments) {
            if (!election.getRestrictions().contains(deptName)){
                selectableDepartments.add(deptName);
            }
        }
        return selectableDepartments.toArray(new String[0]);
    }

    /**
     * Create List Menu of the Administrator Console
     * @return List object with the respective type and attributes, null if the operation is cancelled
     */
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

    /**
     * Choose elections Sub-Menu
     * @param elections CopyOnWriteArrayList of the options
     * @return Option Selected
     */
    public int chooseElectionsMenu(CopyOnWriteArrayList<Election<?>> elections) {
        ArrayList<String> electionNames = new ArrayList<>();
        for (Election<?> e: elections){
            electionNames.add(e.getName());
        }
        String[] electionOptionNames = electionNames.toArray(new String[0]);
        return this.parser.choose("Choose Election", electionOptionNames);
    }

    /**
     * Choose lists Sub-Menu
     * @param lists CopyOnWriteArrayList of the options
     * @return Option Selected
     */
    public int chooseListsMenu(CopyOnWriteArrayList<List<?>> lists){
        ArrayList<String> listNames = new ArrayList<>();
        for (List<?> l: lists){
            listNames.add(l.getName());
        }
        String[] electionOptionNames = listNames.toArray(new String[0]);
        return this.parser.choose("Choose List", electionOptionNames);
    }

    /**
     * Choose people Sub-Menu
     * @param people CopyOnWriteArrayList of the options
     * @return Option Selected
     */
    public int choosePeopleMenu(CopyOnWriteArrayList<Person> people){
        ArrayList<String> listNames = new ArrayList<>();
        for (Person p: people){
            listNames.add(p.getName());
        }
        String[] electionOptionNames = listNames.toArray(new String[0]);
        return this.parser.choose("Choose Person", electionOptionNames);
    }

    /**
     * Real Time Menu
     * @return Option Selected
     */
    public int realTimeMenu() {
        String[] opts = new String[]{"Real Time Elections", "Real Time Voting Desks"};
        return this.parser.choose("Real Time Menu", opts);
    }


    /* ################################################################### */

    /**
     * Main static method - Instance of an Administrator Console
     * Controls the whole flow on the Administrator Console
     * @param args socket: arg#1 = IP arg#2 = port
     */
    public static void main(String[] args) {
        final String IP, PORT;
        if  (args.length != 2) {
            System.out.println("java AdminConsole <RMI SERVER - IP ADDRESS> <RMI SERVER - PORT>");
            return;
        } else {
            IP = args[0];
            PORT = args[1];
        }

        int optElection, optList, optPeople;
        String[] departments;
        Election<?> selectedElection;
        CopyOnWriteArrayList<Election<?>> futureElections, runningElections;
        CopyOnWriteArrayList<List<?>> lists;
        CopyOnWriteArrayList<Person> people;
        AdminConsole admin;
        Thread realTimeThread;
        try {
            admin = new AdminConsole();
        } catch (Exception e){
            System.out.println("Could not start Admin Console");
            return;
        }
        admin.parser.clear();
        RmiServerInterface server = admin.connect(IP, PORT);

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
                                server = admin.connect(IP, PORT);
                                if (server == null) return;
                            }
                        }
                    }
                    break;

                /* Info */
                case 2:
                    while (true) {
                        try {
                            admin.getDatabaseInfo(server);
                            break;
                        } catch (Exception e) {
                            System.out.println("[DEBUG]");
                            e.printStackTrace();
                            server = admin.connect(IP, PORT);
                            if (server == null) return;
                        }
                    }
                    admin.parser.getEnter();
                    break;

                /* Manage Election */
                case 3:
                    admin.parser.header("Manage Elections");
                    int manageElectionsOpt = admin.manageElectionsMenu();
                    while (manageElectionsOpt != 0) {
                        admin.parser.clear();
                        switch (manageElectionsOpt) {

                            /* Create Election*/
                            case 1:
                                Election<?> election = admin.createElectionMenu();
                                if (election != null) {
                                    while (true) {
                                        try {
                                            server.createElection(election);
                                            break;
                                        } catch (Exception e) {
                                            System.out.println("[DEBUG]");
                                            e.printStackTrace();
                                            server = admin.connect(IP, PORT);
                                            if (server == null) return;
                                        }
                                    }
                                }
                                break;

                            /* Edit Election */
                            case 2:
                                admin.parser.header("Edit Election");
                                while (true) {
                                    try {
                                        futureElections = server.getFutureElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if (server == null) return;
                                    }
                                }

                                optElection = admin.chooseElectionsMenu(futureElections);
                                if (optElection == 0) break;
                                election = futureElections.get(optElection - 1);

                                int editOpt = admin.editElectionsMenu();
                                if (editOpt == 0) break;
                                String editString;
                                GregorianCalendar newDate;
                                System.out.println("Enter \"QUIT\" to abort the operation at any time.");
                                switch (editOpt) {
                                    /* Edit Name */
                                    case 1:
                                        editString = admin.parser.parseString("New Name",false);
                                        if (editString == null) break;
                                        while (true) {
                                            try {
                                                server.editElectionName(election.getName(), editString);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;
                                    /* Edit Description*/
                                    case 2:
                                        editString = admin.parser.parseString("New Description", false);
                                        if (editString == null) break;
                                        while (true) {
                                            try {
                                                server.editElectionDescription(election.getName(), editString);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;
                                    /* Edit Start Date */
                                    case 3:
                                        newDate = admin.parser.parseDateTime("New Start Date/Time", false);
                                        if (newDate == null) break;
                                        while (true) {
                                            try {
                                                server.editElectionStartDate(election.getName(), newDate);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;
                                    /* Edit End Date */
                                    case 4:
                                        newDate = admin.parser.parseDateTime("New End Date/Time", false);
                                        if (newDate == null) break;
                                        while (true) {
                                            try {
                                                server.editElectionEndDate(election.getName(), newDate);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;

                                    /* Add Department */
                                    case 5:

                                        while (true) {
                                            try {
                                                departments = server.getDepartments();
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        departments = admin.addDepartmentMenuFilter(election, departments);

                                        admin.parser.clear();
                                        admin.parser.header("Add Department");
                                        int addDeptOpt = admin.parser.choose("Choose a Department", departments);
                                        if (addDeptOpt == 0) break;

                                        while (true) {
                                            try {
                                                server.addDepartment(election.getName(), departments[addDeptOpt - 1]);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;

                                    /* Remove Department */
                                    case 6:
                                        admin.parser.clear();
                                        admin.parser.header("Remove Department");
                                        int removeDeptOpt = admin.parser.choose("Choose a Department", election.getDepartments().toArray(new String[0]));
                                        if (removeDeptOpt == 0) break;

                                        while (true) {
                                            try {
                                                server.removeDepartment(election.getName(), election.getDepartments().get(removeDeptOpt - 1));
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }

                                        break;
                                    /* Add Restriction */
                                    case 7:
                                        while (true) {
                                            try {
                                                departments = server.getDepartments();
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }

                                        departments = admin.addRestrictionMenuFilter(election, departments);

                                        admin.parser.clear();
                                        admin.parser.header("Add Restriction");
                                        int addRestDeptOpt = admin.parser.choose("Choose a Department", departments);
                                        if (addRestDeptOpt == 0) break;

                                        while (true) {
                                            try {
                                                server.addRestriction(election.getName(), departments[addRestDeptOpt - 1]);
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }
                                        break;

                                    /* Remove Restriction */
                                    case 8:
                                        admin.parser.clear();
                                        admin.parser.header("Remove Restriction");
                                        int removeRestDeptOpt = admin.parser.choose("Choose a Department", election.getRestrictions().toArray(new String[0]));
                                        if (removeRestDeptOpt == 0) break;

                                        while (true) {
                                            try {
                                                server.removeRestriction(election.getName(), election.getRestrictions().get(removeRestDeptOpt - 1));
                                                break;
                                            } catch (Exception e) {
                                                System.out.println("[DEBUG]");
                                                e.printStackTrace();
                                                server = admin.connect(IP, PORT);
                                                if (server == null) return;
                                            }
                                        }

                                        break;
                                }
                                break;
                            /* Past Election Log */
                            case 3:
                                admin.parser.header("Past Election Log");
                                CopyOnWriteArrayList<Election<?>> endedElections;
                                while (true) {
                                    try {
                                        endedElections = server.getEndedElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if (server == null) return;
                                    }
                                }

                                admin.showEndedElectionStatsMenu(server, endedElections);
                                admin.parser.getEnter();
                                break;

                            /* Person Audit */
                            case 4:
                                admin.parser.header("Audit Person");
                                while (true) {
                                    try {
                                        people = server.getPeople();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if (server == null) return;
                                    }
                                }
                                int personOpt = admin.choosePeopleMenu(people);
                                if (personOpt == 0) break;
                                Person p = people.get(personOpt - 1);
                                while (true) {
                                    try {
                                        server.printElectorVotesInfo(admin, p.getIdentityCardNumber());
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if (server == null) return;
                                    }
                                }
                                admin.parser.getEnter();
                                break;
                        }
                        admin.parser.clear();
                        admin.parser.header("Manage Elections");
                        manageElectionsOpt = admin.manageElectionsMenu();
                    }
                    break;

                /* Manage Election Lists */
                case 4:
                    admin.parser.header("Manage Election Lists");
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
                                            server = admin.connect(IP, PORT);
                                            if (server == null) return;
                                        }
                                    }
                                }
                                break;
                            /* Add Election List to Election */
                            case 2:
                                admin.parser.header("Add Election List to Election");
                                while (true) {
                                    try {
                                        futureElections = server.getFutureElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }

                                break;

                            /* Remove Election List From Election */
                            case 3:
                                admin.parser.header("Remove Election List from Election");
                                while (true) {
                                    try {
                                        futureElections = server.getFutureElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }
                                break;

                            /* Add Person To List*/
                            case 4:
                                admin.parser.header("Add Person to List");
                                while (true) {
                                    try {
                                        lists = server.getEditableLists();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }
                                break;

                            /* Remove Person From List*/
                            case 5:
                                admin.parser.header("Remove Person from List");
                                while (true) {
                                    try {
                                        lists = server.getEditableLists();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
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
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }
                                break;
                        }

                        admin.parser.clear();
                        admin.parser.header("Manage Election Lists");
                        manageListsOpt = admin.manageListsMenu();
                    }
                    break;
                /* Real Time Data */
                case 5:
                    admin.parser.header("Real Time Data");
                    int realTimeOpt = admin.realTimeMenu();
                    while (realTimeOpt != 0) {
                        admin.parser.clear();
                        switch (realTimeOpt) {

                            /* Real Time Elections */
                            case 1:
                                while (true) {
                                    try {
                                         runningElections = server.getRunningElections();
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }
                                admin.parser.header("Real Time Elections");
                                optElection = admin.chooseElectionsMenu(runningElections);
                                if (optElection == 0) break;
                                Election<?> election = runningElections.get(optElection - 1);
                                admin.parser.clear();
                                admin.parser.header("Real Time [" + election.getName() + "]");
                                admin.realTimeElectionName = election.getName();

                                 realTimeThread = new Thread(() -> {
                                    admin.parser.getEnter();
                                    admin.realTimeElectionName = null;
                                });

                                realTimeThread.start();

                                while (admin.realTimeElectionName != null) {
                                    try {
                                        System.out.println(new GregorianCalendar().getTime());
                                        election = server.getElection(election.getName());
                                        server.printVotingProcessedData((RmiAdminConsoleInterface) admin, election);
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }

                                break;

                            /* Real Time Voting Desks */
                            case 2:
                                admin.parser.header("Real Time Voting Desks");

                                realTimeThread = new Thread(() -> {
                                    admin.parser.getEnter();
                                    admin.realTimeDesks = false;
                                });

                                admin.realTimeDesks = true;
                                realTimeThread.start();

                                while (admin.realTimeDesks) {
                                    try {
                                        System.out.println(new GregorianCalendar().getTime());
                                        server.pingDesks(admin);
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        System.out.println("[DEBUG]");
                                        e.printStackTrace();
                                        server = admin.connect(IP, PORT);
                                        if(server == null) return;
                                    }
                                }

                                break;
                        }
                        admin.parser.clear();
                        admin.parser.header("Real Time Data");
                        realTimeOpt = admin.realTimeMenu();
                    }
                    break;
            }
            admin.parser.clear();
            optMainMenu = admin.mainMenu();
        }
        System.exit(0);
    }

}
