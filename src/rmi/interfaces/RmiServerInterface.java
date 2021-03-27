package rmi.interfaces;

import utils.ElectionList;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServerInterface extends Remote {
    /* Default Methods */

    public default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public default String ping() throws RemoteException {
        return "Ping Pong";
    }

    public void info() throws RemoteException;

    /* Interface Methods */

    public void signUp(Person person) throws RemoteException;

    public void createElection(Election<? extends Person> election) throws RemoteException;

    public void addList(ElectionList<? extends Person> list) throws RemoteException;

}
