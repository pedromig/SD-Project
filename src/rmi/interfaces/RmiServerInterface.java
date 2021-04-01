package rmi.interfaces;

import utils.Vote;
import utils.lists.List;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

public interface RmiServerInterface extends Remote {

    /* Default Methods */

    default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    default String ping() throws RemoteException {
        return "Ping Pong";
    }

    public default boolean compareDates(GregorianCalendar date1, GregorianCalendar date2) throws RemoteException{
        return date1.getTimeInMillis() < date2.getTimeInMillis();
    }


    /* Interface Methods */

    void subscribe(RmiAdminConsoleInterface adminConsole) throws RemoteException;
    void subscribe(RmiMulticastServerInterface multicastDesk) throws RemoteException;

    void pingDesks(RmiAdminConsoleInterface adminConsole) throws RemoteException;

    void info(RmiAdminConsoleInterface client) throws RemoteException;

    String[] getDepartments() throws RemoteException;

    void signUp(Person person) throws RemoteException;


    void createElection(Election<? extends Person> election) throws RemoteException;

    void editElectionName(String electionName, String newName) throws RemoteException;

    void editElectionDescription(String electionName, String newDescription) throws RemoteException;

    void editElectionStartDate(String electionName, GregorianCalendar newDate) throws RemoteException;

    void editElectionEndDate(String electionName, GregorianCalendar newDate) throws RemoteException;


    void createList(List<? extends Person> list) throws RemoteException;

    void associateListToElection(String electionName, String listName) throws RemoteException;

    void associatePersonToList(String listName, int personID) throws RemoteException;

    Election<?> getElection(String electionName) throws RemoteException;


    CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException;

    CopyOnWriteArrayList<Election<?>> getEndedElections() throws RemoteException;

    CopyOnWriteArrayList<Election<?>> getRunningElections() throws RemoteException;

    CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException;

    CopyOnWriteArrayList<List<?>> getListsAssignedOfType(Class<?> type, String electionName) throws RemoteException;

    CopyOnWriteArrayList<List<?>> getListsUnassigned() throws RemoteException;

    CopyOnWriteArrayList<List<?>> getListsUnassignedOfType(Class<?> type) throws RemoteException;

    CopyOnWriteArrayList<List<?>> getFutureLists() throws RemoteException;

    CopyOnWriteArrayList<List<?>> getEditableLists() throws RemoteException;

    CopyOnWriteArrayList<Person> getPeople() throws RemoteException;

    CopyOnWriteArrayList<Person> getPeopleOfType(Class<?> type) throws RemoteException;

    CopyOnWriteArrayList<Person> getPeopleAssignedOfType(Class<?> type, String listName) throws RemoteException;

    CopyOnWriteArrayList<Person> getPeopleUnassigned() throws RemoteException;

    CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException;


    void vote(String electionName, Vote vote) throws RemoteException;

    boolean hasVoted(String electionName, int personID) throws RemoteException;


    void printVotingProcessedData(RmiAdminConsoleInterface admin, Election<?> election) throws RemoteException;

    void printElectorVotesInfo(RmiAdminConsoleInterface admin, int personID) throws RemoteException;

}
