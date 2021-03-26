package rmi;

import rmi.interfaces.RmiServerInterface;
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
import java.util.Vector;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {
    private static final String electionsPath = "elections.obj", peoplePath = "people.obj";
    private Vector<Election<Person>> elections;
    private Vector<Person> people;

    /* ################## RmiServerInterface interface methods ######################## */

    @Override
    public void signUp(Person person) throws RemoteException {
        this.people.add(person);
        this.savePeople();
    }

    @Override
    public void createElection(Election<Person> election) throws RemoteException {
        System.out.println("create election");
    }

    @Override
    public Election<Person> searchElection(String name) throws RemoteException {
        System.out.println("search election");
        return null;
    }

    /* ################################################################################# */

    public RmiServer(Vector<Election<Person>> elections, Vector<Person> people) throws RemoteException {
        super();
        this.elections = elections;
        this.people = people;
    }

    /* Save Data */
    public synchronized boolean saveElections(){
        return RmiServer.saveData(electionsPath, this.elections);
    }

    public synchronized boolean savePeople(){
        return RmiServer.saveData(peoplePath, this.people);
    }

    public static boolean saveData(String path, Object object) {
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
    public static synchronized Vector<Election<Person>> loadElections(){
        return (Vector<Election<Person>>) RmiServer.loadData(electionsPath);
    }

    public static synchronized Vector<Person> loadPeople() {
        return (Vector<Person>) RmiServer.loadData(peoplePath);
    }

    public static Object loadData(String path) {
        try {
            FileInputStream is = new FileInputStream(path);
            ObjectInputStream objIs = new ObjectInputStream(is);
            Object data = objIs.readObject();
            objIs.close();
            is.close();
            return data;
        } catch (Exception e) {
            return new Vector<>();
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

        Vector<Person> people = RmiServer.loadPeople();
        Vector<Election<Person>> elections = RmiServer.loadElections();

        /* Run Server */
        try {
            server = new RmiServer(elections, people);
            Naming.rebind("RmiServer", server);
            System.out.println("RmiServer ready!");
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in RMI Server: " + e);
        }
    }
}
