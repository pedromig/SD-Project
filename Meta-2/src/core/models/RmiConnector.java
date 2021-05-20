package core.models;

import core.Configuration;
import rmiserver.interfaces.RmiServerInterface;
import utils.Vote;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Employee;
import utils.people.Person;
import utils.people.Student;
import utils.people.Teacher;

import java.rmi.Naming;
import java.rmi.Remote;
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

    public CopyOnWriteArrayList<Election<?>> getRunningElectionsByDepartment(String dept) throws RemoteException {
        return server.getRunningElectionsByDepartment(dept);
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

    public CopyOnWriteArrayList<List<?>> getListsUnassignedOfType(Class<?> type) throws RemoteException {
        return server.getListsUnassignedOfType(type);
    }

    public CopyOnWriteArrayList<List<?>> getListsAssignedOfType(Class<?> type, String electionName) throws RemoteException {
        return server.getListsAssignedOfType(type, electionName);
    }

    public CopyOnWriteArrayList<Person> getPeople() throws RemoteException {
        return server.getPeople();
    }

    public CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException {
        return server.getPeopleUnassignedOfType(type);
    }

    public CopyOnWriteArrayList<Person> getPeopleAssignedOfType(Class<?> type, String listName) throws RemoteException {
        return server.getPeopleAssignedOfType(type, listName);
    }

    public Person getPerson(int personId) throws RemoteException {
        return server.getPerson(personId);
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
    public void associateListToElection(String electionName, String listName) throws RemoteException {
        server.associateListToElection(electionName, listName);
    }

    public String[] getDepartments() throws RemoteException {
        return this.server.getDepartments();
    }

    public void addDepartment(String electionName, String dept) throws RemoteException {
        this.server.addDepartment(electionName, dept);
    }

    public void removeDepartment(String electionName, String dept) throws RemoteException {
        this.server.removeDepartment(electionName, dept);
    }

    public void addRestriction(String electionName, String dept) throws RemoteException {
        this.server.addRestriction(electionName, dept);
    }

    public void removeRestriction(String electionName, String dept) throws RemoteException {
        this.server.removeRestriction(electionName, dept);
    }

    public Election<?> getElection(String name) throws RemoteException {
        return this.server.getElection(name);
    }

    public void vote(Vote vote) throws RemoteException {
        this.server.vote(vote);
    }

    public boolean hasVoted(String electionName, int personID) throws RemoteException {
        return this.server.hasVoted(electionName, personID);
    }
}