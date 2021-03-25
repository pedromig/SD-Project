package rmi;

import rmi.interfaces.RmiServerInterface;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {

    @Override
    public void signUp(Person person) throws RemoteException {
        System.out.println("sign up");
    }

    @Override
    public void createElection(Election election) throws RemoteException {
        System.out.println("create election");

    }

    @Override
    public Election searchElection(String name) throws RemoteException {
        System.out.println("search election");
        return null;
    }

    public RmiServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            RmiServer server = new RmiServer();
            Naming.rebind("RmiServer", server);
            System.out.println("RmiServer ready!");
        } catch (Exception e) {
            System.out.println("Exception in RmiServer.main: " + e);
        }
    }
}
