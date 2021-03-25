package rmi;

import rmi.interfaces.RmiServerInterface;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {

    @Override
    public void signUp(Person person) throws RemoteException {
        System.out.println("SIGNED UP");
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

    public RmiServer() throws RemoteException {
        super();
    }

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
        } catch (Exception e){
            System.out.println("Exception @ RmiServer.main.failover");
        }

        /* Run Server */
        try {
            server = new RmiServer();
            Naming.rebind("RmiServer", server);
            System.out.println("RmiServer ready!");
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception in RMI Server: " + e);
        }
    }
}
