package rmi.interfaces;

import utils.lists.List;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RmiServerInterface extends Remote {

    /* Default Methods */

    public default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public default String ping() throws RemoteException {
        return "Ping Pong";
    }

    public default boolean compareDates(GregorianCalendar date1, GregorianCalendar date2) throws RemoteException{
        return date1.getTimeInMillis() < date2.getTimeInMillis();
    }

    public void info() throws RemoteException;


    /* Interface Methods */

    public void signUp(Person person) throws RemoteException;

    public void createElection(Election<? extends Person> election) throws RemoteException;

    public void createList(List<? extends Person> list) throws RemoteException;

    public CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException;

    public CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException;

    public CopyOnWriteArrayList<List<?>> getListsWithAssignedElectionName(String electionName) throws RemoteException;

    public CopyOnWriteArrayList<List<?>> getListsWithoutAssignedElectionNameOfType(Class<?> type, String electionName) throws RemoteException;

    public void associateElection(String electionName, String listName) throws RemoteException;

}
