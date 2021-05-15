package rmi.interfaces;

import utils.Vote;
import utils.lists.List;
import utils.elections.Election;
import utils.people.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A RMI server interface
 */
public interface RmiServerInterface extends Remote {

	/* Default Methods */

	/**
	 * Remote Print
	 * @param msg String to be printed in the server
	 * @throws RemoteException
	 */
	default void print(String msg) throws RemoteException {
		System.out.println(msg);
	}

	/**
	 * A method to ping the server
	 * @return String "Ping Pong"
	 * @throws RemoteException
	 */
	default String ping() throws RemoteException {
		return "Ping Pong";
	}

	/**
	 * A method to compare dates. Checks if date1 is before date2
	 * @param date1 GregorianCalendar of the first date
	 * @param date2 GregorianCalendar of the second date
	 * @return true if date1 < date2, else false
	 * @throws RemoteException
	 */
	default boolean compareDates(GregorianCalendar date1, GregorianCalendar date2) throws RemoteException {
		return date1.getTimeInMillis() < date2.getTimeInMillis();
	}

	/* Interface Methods */

	/**
	 * A method to save the Administrator Consoles that connect to the RMI Server
	 * @param adminConsole The Remote Administrator Console Interface
	 * @throws RemoteException
	 */
	void subscribe(RmiAdminConsoleInterface adminConsole) throws RemoteException;

	/**
	 *  method to save the Voting Desks that connect to the RMI Server
	 * @param multicastDesk The remote Voting Desk Interface
	 * @param name identification of the department that this voting desk is in
	 * @throws RemoteException
	 */
	void subscribe(RmiMulticastServerInterface multicastDesk, String name) throws RemoteException;

	/**
	 * Callback where the RMI server gets a request from an Administrator Console about the state of the voting desks
	 * and their respective terminals, pings all the desks subscribed to him, and prints the replies on the requester
	 * @param adminConsole admin console that requested the information
	 * @throws RemoteException
	 */
	void pingDesks(RmiAdminConsoleInterface adminConsole) throws RemoteException;

	/**
	 * Callback to get an overview of the objects in the database
	 * @param client AdminConsole that requested the information
	 * @throws RemoteException
	 */
	void info(RmiAdminConsoleInterface client) throws RemoteException;

	/**
	 * Getter for all available Departments
	 * @return String array of all available departments
	 * @throws RemoteException
	 */
	String[] getDepartments() throws RemoteException;

	/**
	 * A method to sign up a person in the database
	 * @param person the Person object to be saved
	 * @throws RemoteException
	 */
	void signUp(Person person) throws RemoteException;

	/**
	 * A method to create an election in the database
	 * @param election Election object to be saved
	 * @throws RemoteException
	 */
	void createElection(Election<? extends Person> election) throws RemoteException;

	/**
	 * Method to Edit an Election Name
	 * @param electionName election name of the target Election object
	 * @param newName replace name
	 * @throws RemoteException
	 */
	void editElectionName(String electionName, String newName) throws RemoteException;

	/**
	 * Method to Edit an Election Description
	 * @param electionName election name of the target Election object
	 * @param newDescription replace description
	 * @throws RemoteException
	 */
	void editElectionDescription(String electionName, String newDescription) throws RemoteException;

	/**
	 * Method to Edit an Election Start Date
	 * @param electionName election name of the target Election object
	 * @param newDate replace date
	 * @throws RemoteException
	 */
	void editElectionStartDate(String electionName, GregorianCalendar newDate) throws RemoteException;

	/**
	 * Method to Edit an Election End Date
	 * @param electionName election name of the target Election object
	 * @param newDate replace date
	 * @throws RemoteException
	 */
	void editElectionEndDate(String electionName, GregorianCalendar newDate) throws RemoteException;

	/**
	 * Method to associate an election to a certain department
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	void addDepartment(String electionName, String departmentName) throws RemoteException;

	/**
	 * Method to dissociate an election of a certain department
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	void removeDepartment(String electionName, String departmentName) throws RemoteException;

	/**
	 * Method to add a department restriction to a given election
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	void addRestriction(String electionName, String departmentName) throws RemoteException;

	/**
	 * Method to remove a department restriction of a given election
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	void removeRestriction(String electionName, String departmentName) throws RemoteException;

	/**
	 * Method to create a List in the database
	 * @param list List object to be saved
	 * @throws RemoteException
	 */
	void createList(List<? extends Person> list) throws RemoteException;

	/**
	 * A method to associate a List to an Election
	 * @param electionName election name of the target Election object
	 * @param listName list name of the target List object
	 * @throws RemoteException
	 */
	void associateListToElection(String electionName, String listName) throws RemoteException;

	/**
	 * A method to associate a Person to a List
	 * @param listName list name of the target List object
	 * @param personID ID of the target Person object
	 * @throws RemoteException
	 */
	void associatePersonToList(String listName, int personID) throws RemoteException;

	/**
	 * Search method for an Election object given an election name
	 * @param electionName the name of the election to search by
	 * @return an Election object if found, else null
	 * @throws RemoteException
	 */
	Election<?> getElection(String electionName) throws RemoteException;

	/**
	 * Getter for Elections that did not start yet
	 * @return CopyOnWriteArrayList with all the elections that did not start yet
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException;

	/**
	 * Getter for Elections that have ended
	 * @return CopyOnWriteArrayList of all the elections that have ended
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Election<?>> getEndedElections() throws RemoteException;

	/**
	 * Getter for Elections that are currently running
	 * @return CopyOnWriteArrayList with all the elections that are currently running
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Election<?>> getRunningElections() throws RemoteException;

	/**
	 * Getter for Elections that are currently running on a given department
	 * @param department name of the department
	 * @return CopyOnWriteArrayList with all running elections in the given department
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Election<?>> getRunningElectionsByDepartment(String department) throws RemoteException;

	/**
	 * Getter for Lists of a given Type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @return CopyOnWriteArrayList with all the lists of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException;

	/**
	 * Getter for Lists assigned to a given election of a given type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @param electionName the name of the election to search by
	 * @return CopyOnWriteArrayList with all the lists assigned to a given election of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getListsAssignedOfType(Class<?> type, String electionName) throws RemoteException;

	/**
	 * Getter for Lists unassigned to any election
	 * @return CopyOnWriteArrayList of all the lists that are unassigned to any election
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getListsUnassigned() throws RemoteException;

	/**
	 * Getter for Lists unassigned to any election of a given type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @return CopyOnWriteArrayList of all the lists that are unassigned to any election of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getListsUnassignedOfType(Class<?> type) throws RemoteException;

	/**
	 * Getter for assigned Lists which election has not yet started
	 * @return CopyOnWriteArrayList with all of the Lists which election has not yet started
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getFutureLists() throws RemoteException;

	/**
	 * Getter for the Lists that are not assigned or which election has not yet started
	 * @return CopyOnWriteArrayList with all of the Lists that are not assigned or which election has not yet started
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<List<?>> getEditableLists() throws RemoteException;

	/**
	 * Getter for all of the People in the database
	 * @return CopyOnWriteArrayList with all of the People in the database
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Person> getPeople() throws RemoteException;

	/**
	 * Search method to get a Person given his ID
	 * @param personId id of the Person object
	 * @return a Person object if the given ID matches, null otherwise
	 * @throws RemoteException
	 */
	Person getPerson(int personId) throws RemoteException;

	/**
	 * Getter for people of a given type
	 * @param type type of a Person Object
	 * @return CopyOnWriteArrayList with all the people of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Person> getPeopleOfType(Class<?> type) throws RemoteException;

	/**
	 * Getter for people Assigned to a list and of a given type
	 * @param type type of a Person object
	 * @param listName name of the List target object
	 * @return CopyOnWriteArrayList of People who are assigned to the given list name and are of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Person> getPeopleAssignedOfType(Class<?> type, String listName) throws RemoteException;

	/**
	 * Getter for people unassigned of the given type
	 * @param type type of a Person object
	 * @return CopyOnWriteArrayList of People who are unassigned and of the given type
	 * @throws RemoteException
	 */
	CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException;

	/**
	 * A method to add a vote to an election the in the database
	 * @param vote Vote Object that contains the voting details
	 * @return true if the message is communicated to all the subscribed administrator consoles, false otherwise
	 * @throws RemoteException
	 */
	boolean vote(Vote vote) throws RemoteException;

	/**
	 * A method to check if a Person has already voted on a given Election
	 * @param electionName election name of the target Election object
	 * @param personID id of the Person object
	 * @return true if the person has already voted on the given election, false otherwise
	 * @throws RemoteException
	 */
	boolean hasVoted(String electionName, int personID) throws RemoteException;


	/**
	 * Method to process and then print the voting details on an Administrator console
	 * @param admin the administrator console that made the request
	 * @param election The Election object to retrieve the details
	 * @throws RemoteException
	 */
	void printVotingProcessedData(RmiAdminConsoleInterface admin, Election<?> election) throws RemoteException;

	/**
	 * Method to print the voting acts of a given person
	 * @param admin the administrator console that made the request
	 * @param personID the ID of the target Person
	 * @throws RemoteException
	 */
	void printElectorVotesInfo(RmiAdminConsoleInterface admin, int personID) throws RemoteException;

}
