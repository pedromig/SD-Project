package core.models;

import core.Configuration;
import rmiserver.interfaces.RmiServerInterface;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.concurrent.CopyOnWriteArrayList;

public class RmiConnector implements Configuration {
    private RmiServerInterface server;

    public RmiConnector() {
        try {
            this.server = (RmiServerInterface) Naming.lookup("rmi://" + IP + ":" + PORT + "/" + SERVER_NAME);
            this.server.ping();
            System.out.println("it runs baby");
        } catch (Exception e) {
            this.server = null;
            e.printStackTrace();
        }
    }

    public RmiServerInterface getServer() {
        return this.server;
    }

    public boolean checkLogin(int idCardNumber, String password) throws RemoteException {
        Person person = this.server.getPerson(idCardNumber);
        return (person != null) && person.getPassword().equals(password);
    }

    public void signUp(Person person) throws RemoteException {
        this.server.signUp(person);
    }

    public void createElection(Election<?> election) throws RemoteException {
        this.server.createElection(election);
    }

    public void createList(List<?> list) throws RemoteException {
        this.server.createList(list);
    }

    public CopyOnWriteArrayList<Election<?>> getRunningElections() throws RemoteException {
        return server.getRunningElections();
    }
    public CopyOnWriteArrayList<Election<?>> getEndedElections() throws RemoteException {
        return server.getEndedElections();
    }
    public CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException {
        return server.getFutureElections();
    }
    public CopyOnWriteArrayList<Election<?>> getAllElections() throws RemoteException {
        CopyOnWriteArrayList<Election<?>> allElections = new CopyOnWriteArrayList<>();
        allElections.addAll(this.getEndedElections());
        allElections.addAll(this.getRunningElections());
        allElections.addAll(this.getFutureElections());
        return allElections;
    }

    public CopyOnWriteArrayList<List<?>> getLists() throws RemoteException {
        CopyOnWriteArrayList<List<?>> allLists = new CopyOnWriteArrayList<>();
        allLists.addAll(server.getListsOfType(Student.class));
        allLists.addAll(server.getListsOfType(Teacher.class));
        allLists.addAll(server.getListsOfType(Employee.class));
        return allLists;
    }
    public CopyOnWriteArrayList<List<?>> getEditableLists() throws RemoteException {
        return server.getEditableLists();
    }

    public CopyOnWriteArrayList<Person> getPeople() throws RemoteException {
        return server.getPeople();
    }
    public Person getPerson(int personId) throws RemoteException {
        return server.getPerson(personId);
    }

    public CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException {
        return server.getPeopleUnassignedOfType(type);
    }

    public String getEndedLog(Election<?> election) {
        try {
            return server.printVotingProcessedData(null, election);
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not fetch the data";
        }
    }

    public String getElectorVotesInfo(int idCardNumber) throws RemoteException {
        return this.server.printElectorVotesInfo(null, idCardNumber);
    }

    public void associatePersonToList(String listName, int personID) throws RemoteException {
        server.associatePersonToList(listName, personID);
    }
}
