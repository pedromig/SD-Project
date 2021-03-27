package rmi;

import rmi.interfaces.RmiServerInterface;
import utils.lists.List;
import utils.elections.Election;
import utils.people.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {
    private static final String
            electionsPath = "../../../database/elections.obj",
            peoplePath = "../../../database/people.obj",
            listsPath = "../../../database/lists.obj";

    private CopyOnWriteArrayList<Election<? extends Person>> elections;
    private CopyOnWriteArrayList<List<? extends Person>> lists;
    private CopyOnWriteArrayList<Person> people;

    /* ################## RmiServerInterface interface methods ######################## */

    //TODO: REMOVE THIS FUNC AFTER DEPLOY (its for debug only)
    @Override
    public synchronized void info() throws RemoteException {
        System.out.println("\n*************************************************************************");
        System.out.println("Elections: ");
        this.elections.forEach(System.out::println);
        System.out.println("\nLists: ");
        this.lists.forEach(System.out::println);
        System.out.println("\nPeople: ");
        this.people.forEach(System.out::println);
        System.out.println("\n*************************************************************************");
    }

    @Override
    public synchronized void signUp(Person person) throws RemoteException {
        for (Person p: this.people) {
            if (p.getIdentityCardNumber() == person.getIdentityCardNumber()) {
                System.out.println("SIGN UP FAILED: Person Already exists");
                return;
            }
        }
        this.people.add(person);
        this.savePeople();
        System.out.println("[" + person.getName() + "] SIGNED UP");
    }

    @Override
    public synchronized void createElection(Election<? extends Person> election) throws RemoteException {
        for (Election<?> e : this.elections) {
            if (e.getName().equals(election.getName())){
                System.out.println("FAILED: CREATE ELECTION - Elections cannot have the same name");
                return;
            }
        }
        this.elections.add(election);
        this.saveElections();
        System.out.println("CREATED: Election [" + election.getName() + "] ");
    }

    @Override
    public synchronized void createList(List<? extends Person> list) throws RemoteException {
        for (List<?> l : this.lists) {
            if (l.getName().equals(list.getName())){
                System.out.println("FAILED: CREATE LIST - Lists cannot have the same name");
                return;
            }
        }
        this.lists.add(list);
        this.saveLists();
        System.out.println("CREATE: List [" + list.getName() + "]");
    }

    @Override
    public synchronized CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException {
        CopyOnWriteArrayList<Election<?>> futureElections = new CopyOnWriteArrayList<>();
        for (Election<?> e : this.elections)
            if (this.compareDates(new GregorianCalendar(), e.getStartDate()))
                futureElections.add(e);
        System.out.println("REQUEST: FUTURE ELECTIONS");
        return futureElections;
    }

    @Override
    public synchronized CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException {
        CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
        for (List<?> l : this.lists)
            if(l.getType() == type)
                lists.add(l);
        System.out.println("REQUEST: Lists<" + type.getName() + ">");
        return lists;
    }

    @Override
    public synchronized CopyOnWriteArrayList<List<?>> getListsWithAssignedElectionName(String electionName) throws RemoteException {
        CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
        for (List<?> l : this.lists)
            if(l.getElectionName() != null && l.getElectionName().equals(electionName))
                lists.add(l);
        System.out.println("REQUEST: Lists<Any> electionName == " + electionName);
        return lists;
    }

    @Override
    public synchronized CopyOnWriteArrayList<List<?>> getListsWithoutAssignedElectionNameOfType(Class<?> type, String electionName) throws RemoteException {
        CopyOnWriteArrayList<List<?>> listsOfType = getListsOfType(type);
        listsOfType.removeIf(l -> l.getElectionName() != null);
        System.out.println("REQUEST: Lists<" + type.getName() + "> electionName == null");
        return listsOfType;
    }

    @Override
    public synchronized void associateElection(String electionName, String listName) throws RemoteException {
        for (List<?> l : this.lists){
            if (l.getName().equals(listName)) {
                l.setElectionName(electionName);
                this.saveLists();
                System.out.println("SET: List ["+ l.getName()+"] to election [" + electionName + "]");
                return ;
            }
        }
    }


    /* ################################################################################# */

    public RmiServer(CopyOnWriteArrayList<Election<? extends Person>> elections,
                     CopyOnWriteArrayList<List<? extends Person>> lists,
                     CopyOnWriteArrayList<Person> people) throws RemoteException {
        super();
        this.elections = elections;
        this.lists = lists;
        this.people = people;
    }

    /* Save Data */
    public synchronized boolean saveElections(){
        return RmiServer.saveData(electionsPath, this.elections);
    }

    public synchronized boolean saveLists(){return RmiServer.saveData(listsPath, this.lists);}

    public synchronized boolean savePeople(){
        return RmiServer.saveData(peoplePath, this.people);
    }

    public synchronized static boolean saveData(String path, Object object) {
        try {
            FileOutputStream os = new FileOutputStream(path);
            ObjectOutputStream objOs = new ObjectOutputStream(os);
            objOs.writeObject(object);
            objOs.close(); os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Load Data */
    public static synchronized CopyOnWriteArrayList<Election<? extends Person>> loadElections(){
        return (CopyOnWriteArrayList<Election<? extends Person>>) RmiServer.loadData(electionsPath);
    }

    public static synchronized CopyOnWriteArrayList<List<? extends Person>> loadLists(){
        return (CopyOnWriteArrayList<List<? extends Person>>) RmiServer.loadData(listsPath);
    }

    public static synchronized CopyOnWriteArrayList<Person> loadPeople() {
        return (CopyOnWriteArrayList<Person>) RmiServer.loadData(peoplePath);
    }

    public static synchronized Object loadData(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            ObjectInputStream objIs = new ObjectInputStream(is);
            Object data = objIs.readObject();
            objIs.close();
            is.close();
            return data;
        } catch (Exception e) {
            System.out.println("Could not read from: " + path);
            return new CopyOnWriteArrayList<>();
        }
    }

    /* ################################################################################# */

    public static void main(String[] args) {
        RmiServerInterface server;
        /* Failover */
        try {
            server = (RmiServerInterface) Naming.lookup("RmiServer");
            server.ping();
            System.out.println("Waiting for primary crash.");
            while (true) {
                System.out.println(server.ping());
                Thread.sleep(1000);
            }
        } catch (NotBoundException | RemoteException e){
            System.out.println("Starting Server");
        } catch (Exception e) {
            System.out.println("Exception @ RmiServer.main.failover");
        }

        /* Bootloading */

        CopyOnWriteArrayList<Person> people = RmiServer.loadPeople();
        CopyOnWriteArrayList<Election<? extends Person>> elections = RmiServer.loadElections();
        CopyOnWriteArrayList<List<? extends Person>> lists = RmiServer.loadLists();

        /* Run Server */
        try {
            server = new RmiServer(elections, lists, people);
            Naming.rebind("RmiServer", server);
            System.out.println("RmiServer ready!");
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in RMI Server: " + e);
        }
    }
}
