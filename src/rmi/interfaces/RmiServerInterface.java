package rmi.interfaces;

import utils.elections.Election;
import utils.people.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerInterface extends Remote {
    /* Default Methods */

    public default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public default String ping() throws RemoteException{
        return "Ping Pong";
    }

    /* Interface Methods */

    public void signUp(Person person) throws RemoteException;

    public void createElection(Election<Person> election) throws RemoteException;

    public Election<Person> searchElection(String name) throws RemoteException;

}
