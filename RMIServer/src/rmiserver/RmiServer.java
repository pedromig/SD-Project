package rmiserver;

import rmiserver.interfaces.RmiAdminConsoleInterface;
import rmiserver.interfaces.RmiMulticastServerInterface;
import rmiserver.interfaces.RmiServerInterface;
import utils.Vote;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {
	private final String dirPath;
	private static final String
			electionsFilePath = "elections.obj",
			peopleFilePath = "people.obj",
			listsFilePath = "lists.obj";

	private static final String[] UC_DEPARTMENTS = new String[]{

			/* Meta 2 - Online */
			"Online",

			/* Faculdade de Letras */

			"DFCI",             // Departamento de Filosofia, Comunicação e Informação
			"DEPGEOTUR",        // Departamento de Geografia e Turismo
			"DHEEAA",           // Departamento de História, Estudos Europeus, Arqueologia e Artes
			"DLLC",             // Departamento de Linguas, Literaturas e Culturas

			/* Faculdade de Direito */

			"FD",               // Faculdade de Direito (itself)

			/* Faculdade de Medicina */

			"FM - Polo I",      // Faculdade de Medicina (polo 1) (itself)
			"FM - Polo III",    // Faculdade de Medicina (polo 3) (itself)

			/* Faculdade de Ciências e Tecnologias */

			"DARQ",             // Departamento de Arquitetura
			"DCT",              // Departamento de Ciências da Terra
			"DCV",              // Departamento de Ciências da VIda
			"DEC",              // Departamento de Engenharia Civil
			"DEEC",             // Departamento de Engenharia Eletrotécnica e de Computadores
			"DEI",              // Departamento de Engenharia Informática
			"DEM",              // Departamento de Engenharia Mecânica
			"DEQ",              // Departamento de Engenharia Química
			"DF",               // Departamento de Física
			"DM",               // Departamento de Matemática
			"DQ",               // Departamento de Química

			/* Faculdade de Farmácia */

			"FF",               // Faculdade de Farmácia (itself)

			/* Faculdade de Economia */

			"FE",               // Faculdade de Economia (itself)

			/* Faculdade de Psicologia e Ciências da Educação */

			"FPCE",             // Faculdade de Psicologia e Ciências da Educação (itself)

			/* Faculdade de Ciências do Desporto e Educação Física */

			"FCDEF"             // Faculdade de Ciências do Desporto e Educação Física (itself)

	};

	private CopyOnWriteArrayList<Election<? extends Person>> elections;
	private CopyOnWriteArrayList<List<? extends Person>> lists;
	private CopyOnWriteArrayList<Person> people;
	private CopyOnWriteArrayList<RmiAdminConsoleInterface> adminConsoles;
	private Hashtable<String, RmiMulticastServerInterface> multicastServers;

	/* ################## RmiServerInterface interface methods ######################## */

	/**
	 * A method to save the Administrator Consoles that connect to the RMI Server
	 * @param adminConsole The Remote Administrator Console Interface
	 * @throws RemoteException
	 */
	@Override
	public synchronized void subscribe(RmiAdminConsoleInterface adminConsole) throws RemoteException {
		this.adminConsoles.add(adminConsole);
	}

	/**
	 *  method to save the Voting Desks that connect to the RMI Server
	 * @param multicastDesk The remote Voting Desk Interface
	 * @param name identification of the department that this voting desk is in
	 * @throws RemoteException
	 */
	@Override
	public synchronized void subscribe(RmiMulticastServerInterface multicastDesk, String name) throws RemoteException {
		System.out.println("Name: " + name);
		System.out.println("msi: "+ multicastDesk);
		this.multicastServers.put(name, multicastDesk);
	}

	/**
	 * Callback where the RMI server gets a request from an Administrator Console about the state of the voting desks
	 * and their respective terminals, pings all the desks subscribed to him, and prints the replies on the requester
	 * @param adminConsole admin console that requested the information
	 * @return String with the message output
	 * @throws RemoteException
	 */
	@Override
	public synchronized String pingDesks(RmiAdminConsoleInterface adminConsole) throws RemoteException {
		for (Map.Entry<String, RmiMulticastServerInterface> entry : this.multicastServers.entrySet()) {
			String output = "";
			String name = entry.getKey();
			RmiMulticastServerInterface msi = entry.getValue();
			try {
				String additionalInfo = msi.ping();
				if (adminConsole != null) {
					adminConsole.print("Server[" + name + "]: ON");
					adminConsole.print(additionalInfo);
				}
				output = output + "Server[" + name + "]: ON" + "\n";
				output = output + additionalInfo + "\n";

			} catch (Exception e) {
				if (adminConsole != null)
					adminConsole.print("Server[" + name + "]: OFF");
				output = output + "Server[" + name + "]: OFF" + "\n";
			}
		}
		return output;
	}

	/**
	 * Callback to get an overview of the objects in the database
	 * @param client console.AdminConsole that requested the information
	 * @throws RemoteException
	 */
	@Override
	public synchronized void info(RmiAdminConsoleInterface client) throws RemoteException {
		client.print("\n*************************************************************************");
		client.print("Elections: ");
		for (Election<?> e : this.elections) client.print(e.toString());
		client.print("\nLists: ");
		for (List<?> l : this.lists) client.print(l.toString());
		client.print("\nPeople: ");
		for (Person p : this.people) client.print(p.toString());
		client.print("\n*************************************************************************");
	}

	/**
	 * Getter for all available Departments
	 * @return String array of all available departments
	 * @throws RemoteException
	 */
	@Override
	public String[] getDepartments() throws RemoteException {
		return UC_DEPARTMENTS;
	}

	/**
	 * A method to sign up a person in the database
	 * @param person the Person object to be saved
	 * @throws RemoteException
	 */
	@Override
	public synchronized void signUp(Person person) throws RemoteException {
		for (Person p : this.people) {
			if (p.getIdentityCardNumber() == person.getIdentityCardNumber()) {
				System.out.println("SIGN UP FAILED: Person Already exists");
				return;
			}
		}
		this.people.add(person);
		this.savePeople();
		System.out.println("[" + person.getName() + "] SIGNED UP");
	}

	/**
	 * A method to create an election in the database
	 * @param election Election object to be saved
	 * @throws RemoteException
	 */
	@Override
	public synchronized void createElection(Election<? extends Person> election) throws RemoteException {
		for (Election<?> e : this.elections) {
			if (e.getName().equals(election.getName())) {
				System.out.println("FAILED: CREATE ELECTION - Elections cannot have the same name");
				return;
			}
		}
		this.elections.add(election);
		this.saveElections();
		System.out.println("CREATED: Election [" + election.getName() + "] ");
	}

	/**
	 * Method to Edit an Election Name
	 * @param electionName election name of the target Election object
	 * @param newName replace name
	 * @throws RemoteException
	 */
	@Override
	public synchronized void editElectionName(String electionName, String newName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.getElection(newName) == null && this.compareDates(new GregorianCalendar(), election.getStartDate())) {
			election.setName(newName);
			this.saveElections();
			for (List<?> l : this.lists) {
				if (l.getElectionName() != null && l.getElectionName().equals(electionName)) {
					l.setElectionName(newName);
				}
			}
			this.saveLists();
		}
	}

	/**
	 * Method to Edit an Election Description
	 * @param electionName election name of the target Election object
	 * @param newDescription replace description
	 * @throws RemoteException
	 */
	@Override
	public synchronized void editElectionDescription(String electionName, String newDescription) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.compareDates(new GregorianCalendar(), election.getStartDate())) {
			election.setDescription(newDescription);
			this.saveElections();
		}
	}

	/**
	 * Method to Edit an Election Start Date
	 * @param electionName election name of the target Election object
	 * @param newDate replace date
	 * @throws RemoteException
	 */
	@Override
	public synchronized void editElectionStartDate(String electionName, GregorianCalendar newDate) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.compareDates(new GregorianCalendar(), election.getStartDate()) &&
				this.compareDates(new GregorianCalendar(), newDate) &&
				this.compareDates(newDate, election.getEndDate())) {
			election.setStartDate(newDate);
			this.saveElections();
		}
	}

	/**
	 * Method to Edit an Election End Date
	 * @param electionName election name of the target Election object
	 * @param newDate replace date
	 * @throws RemoteException
	 */
	@Override
	public synchronized void editElectionEndDate(String electionName, GregorianCalendar newDate) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.compareDates(new GregorianCalendar(), election.getStartDate()) &&
				this.compareDates(election.getStartDate(), newDate)) {
			election.setEndDate(newDate);
			this.saveElections();
		}
	}

	/**
	 * Method to associate an election to a certain department
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void addDepartment(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (!election.getDepartments().contains(departmentName)) {
			election.getDepartments().add(departmentName);
			this.saveElections();
		}
	}

	/**
	 * Method to dissociate an election of a certain department
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void removeDepartment(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (election.getDepartments().contains(departmentName)) {
			election.getDepartments().remove(departmentName);
			this.saveElections();
		}
	}

	/**
	 * Method to add a department restriction to a given election
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void addRestriction(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (!election.getRestrictions().contains(departmentName)) {
			election.getRestrictions().add(departmentName);
			this.saveElections();
		}
	}

	/**
	 * Method to remove a department restriction of a given election
	 * @param electionName election name of the target Election object
	 * @param departmentName department name of the target Department object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void removeRestriction(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (election.getRestrictions().contains(departmentName)) {
			election.getRestrictions().remove(departmentName);
			this.saveElections();
		}
	}

	/**
	 * Method to create a List in the database
	 * @param list List object to be saved
	 * @throws RemoteException
	 */
	@Override
	public synchronized void createList(List<? extends Person> list) throws RemoteException {
		for (List<?> l : this.lists) {
			if (l.getName().equals(list.getName())) {
				System.out.println("FAILED: CREATE LIST - Lists cannot have the same name");
				return;
			}
		}
		this.lists.add(list);
		this.saveLists();
		System.out.println("CREATE: List [" + list.getName() + "]");
	}

	/**
	 * A method to associate a List to an Election
	 * @param electionName election name of the target Election object
	 * @param listName list name of the target List object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void associateListToElection(String electionName, String listName) throws RemoteException {
		for (List<?> l : this.lists) {
			if (l.getName().equals(listName)) {
				l.setElectionName(electionName);
				this.saveLists();
				System.out.println("SET: List [" + l.getName() + "] to election [" + electionName + "]");
				return;
			}
		}
	}

	/**
	 * A method to associate a Person to a List
	 * @param listName list name of the target List object
	 * @param personID ID of the target Person object
	 * @throws RemoteException
	 */
	@Override
	public synchronized void associatePersonToList(String listName, int personID) throws RemoteException {
		for (Person p : this.people) {
			if (p.getIdentityCardNumber() == personID) {
				p.setList(listName);
				this.savePeople();
				System.out.println("SET: People [" + p.getName() + " | " + p.getIdentityCardNumber() + "] to List [" + listName + "]");
				return;
			}
		}
	}

	/**
	 * Search method for an Election object given an election name
	 * @param electionName the name of the election to search by
	 * @return an Election object if found, else null
	 * @throws RemoteException
	 */
	@Override
	public synchronized Election<?> getElection(String electionName) throws RemoteException {
		for (Election<?> e : this.elections)
			if (e.getName().equals(electionName))
				return e;
		return null;
	}

	/**
	 * Getter for Elections that did not start yet
	 * @return CopyOnWriteArrayList with all the elections that did not start yet
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException {
		CopyOnWriteArrayList<Election<?>> futureElections = new CopyOnWriteArrayList<>();
		for (Election<?> e : this.elections)
			if (this.compareDates(new GregorianCalendar(), e.getStartDate()))
				futureElections.add(e);
		System.out.println("REQUEST: FUTURE ELECTIONS");
		return futureElections;
	}

	/**
	 * Getter for Elections that have ended
	 * @return CopyOnWriteArrayList of all the elections that have ended
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Election<?>> getEndedElections() throws RemoteException {
		CopyOnWriteArrayList<Election<?>> endedElections = new CopyOnWriteArrayList<>();
		for (Election<?> e : this.elections) {
			if (this.compareDates(e.getEndDate(), new GregorianCalendar())) {
				endedElections.add(e);
			}
		}
		return endedElections;
	}

	/**
	 * Getter for Elections that are currently running
	 * @return CopyOnWriteArrayList with all the elections that are currently running
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Election<?>> getRunningElections() throws RemoteException {
		CopyOnWriteArrayList<Election<?>> running = new CopyOnWriteArrayList<>();
		for (Election<?> e : this.elections) {
			if (this.compareDates(e.getStartDate(), new GregorianCalendar()) &&
					this.compareDates(new GregorianCalendar(), e.getEndDate())) {
				running.add(e);
			}
		}
		return running;
	}

	/**
	 * Getter for Elections that are currently running on a given department
	 * @param department name of the department
	 * @return CopyOnWriteArrayList with all running elections in the given department
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Election<?>> getRunningElectionsByDepartment(String department) throws RemoteException {
		CopyOnWriteArrayList<Election<?>> elections = new CopyOnWriteArrayList<>();
		for (Election<?> e : this.getRunningElections()) {
			if (e.getDepartments().contains(department)) {
				elections.add(e);
			}
		}
		return elections;
	}

	/**
	 * Getter for Lists of a given Type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @return CopyOnWriteArrayList with all the lists of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
		for (List<?> l : this.lists)
			if (l.getType() == type)
				lists.add(l);
		System.out.println("REQUEST: Lists<" + type.getName() + ">");
		return lists;
	}

	/**
	 * Getter for Lists assigned to a given election of a given type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @param electionName the name of the election to search by
	 * @return CopyOnWriteArrayList with all the lists assigned to a given election of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsAssignedOfType(Class<?> type, String electionName) throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = this.getListsOfType(type);
		lists.removeIf(l -> l.getElectionName() == null || !l.getElectionName().equals(electionName));
		System.out.println("REQUEST: Lists<" + type.getName() + "> electionName == " + electionName);
		return lists;
	}

	/**
	 * Getter for Lists unassigned to any election
	 * @return CopyOnWriteArrayList of all the lists that are unassigned to any election
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsUnassigned() throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
		for (List<?> l : this.lists) {
			if (l.getElectionName() == null)
				lists.add(l);
		}
		System.out.println("REQUEST: Lists<Any> electionName == null");
		return lists;
	}

	/**
	 * Getter for Lists unassigned to any election of a given type
	 * @param type type of the People allowed to vote and to be a part of the list
	 * @return CopyOnWriteArrayList of all the lists that are unassigned to any election of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsUnassignedOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<List<?>> listsOfType = getListsOfType(type);
		listsOfType.removeIf(l -> l.getElectionName() != null);
		System.out.println("REQUEST: Lists<" + type.getName() + "> electionName == null");
		return listsOfType;
	}

	/**
	 * Getter for assigned Lists which election has not yet started
	 * @return CopyOnWriteArrayList with all of the Lists which election has not yet started
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getFutureLists() throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
		Election<?> election;
		for (List<?> l : this.lists) {
			if (l.getElectionName() != null) {
				election = this.getElection(l.getElectionName());
				if (election != null && this.compareDates(new GregorianCalendar(), election.getStartDate()))
					lists.add(l);
			}
		}
		return lists;
	}

	/**
	 * Getter for the Lists that are not assigned or which election has not yet started
	 * @return CopyOnWriteArrayList with all of the Lists that are not assigned or which election has not yet started
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getEditableLists() throws RemoteException {
		CopyOnWriteArrayList<List<?>> futureLists = this.getFutureLists();
		CopyOnWriteArrayList<List<?>> unassignedLists = this.getListsUnassigned();
		futureLists.addAll(unassignedLists);
		return futureLists;
	}

	/**
	 * Getter for all of the People in the database
	 * @return CopyOnWriteArrayList with all of the People in the database
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeople() throws RemoteException {
		return this.people;
	}

	/**
	 * Search method to get a Person given his ID
	 * @param personId id of the Person object
	 * @return a Person object if the given ID matches, null otherwise
	 * @throws RemoteException
	 */
	@Override
	public synchronized Person getPerson(int personId) throws RemoteException {
		for (Person p : this.people) {
			if (p.getIdentityCardNumber() == personId) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Getter for people of a given type
	 * @param type type of a Person Object
	 * @return CopyOnWriteArrayList with all the people of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<Person> people = new CopyOnWriteArrayList<>();
		for (Person p : this.people) {
			if (p.getType() == type)
				people.add(p);
		}
		return people;
	}

	/**
	 * Getter for people Assigned to a list and of a given type
	 * @param type type of a Person object
	 * @param listName name of the List target object
	 * @return CopyOnWriteArrayList of People who are assigned to the given list name and are of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleAssignedOfType(Class<?> type, String listName) throws RemoteException {
		CopyOnWriteArrayList<Person> people = this.getPeopleOfType(type);
		people.removeIf(p -> p.getList() == null || !p.getList().equals(listName));
		return people;
	}

	/**
	 * Getter for people unassigned of the given type
	 * @param type type of a Person object
	 * @return CopyOnWriteArrayList of People who are unassigned and of the given type
	 * @throws RemoteException
	 */
	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<Person> people = this.getPeopleOfType(type);
		people.removeIf(p -> p.getList() != null);
		return people;
	}

	/**
	 * A method to add a vote to an election the in the database
	 * @param vote Vote Object that contains the voting details
	 * @return true if the message is communicated to all the subscribed administrator consoles, false otherwise
	 * @throws RemoteException
	 */
	@Override
	public synchronized boolean vote(Vote vote) throws RemoteException {
		boolean status = false;
		Election<?> election = this.getElection(vote.getElectionName());
		if (this.compareDates(new GregorianCalendar(), election.getEndDate()) &&
				!this.hasVoted(vote.getElectionName(), vote.getPersonID())) {
			election.addVote(vote);
			status = true;

			this.saveElections();
			for (RmiAdminConsoleInterface admin : this.adminConsoles) {
				try {
					if ((admin.getRealTimeElectionName() != null) && (admin.getRealTimeElectionName().equals(vote.getElectionName()))) {
						admin.print("Update");
						this.printVotingProcessedData(admin, election);
					}
				} catch (Exception e) {
					status = false;
					/* In case the admin doesnt respond anymore, to keep iterating over the other ones */
					// This might be a bad Idea because the admin console can have a short network failure... might as
					// well just
					// keep it all
					// this.adminConsoles.remove(admin);
				}
			}
		}
		return status;
	}

	/**
	 * A method to check if a Person has already voted on a given Election
	 * @param electionName election name of the target Election object
	 * @param personID id of the Person object
	 * @return true if the person has already voted on the given election, false otherwise
	 * @throws RemoteException
	 */
	@Override
	public synchronized boolean hasVoted(String electionName, int personID) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		for (Vote v : election.getVotes())
			if (v.getPersonID() == personID)
				return true;
		return false;
	}

	/**
	 * Method to process and then print the voting details on an Administrator console
	 * @param admin the administrator console that made the request
	 * @param election The Election object to retrieve the details
	 * @return string with the printed message
	 * @throws RemoteException
	 */
	@Override
	public synchronized String printVotingProcessedData(RmiAdminConsoleInterface admin, Election<?> election) throws RemoteException {
		String output = "";
		int total = 0;
		int nullVotes = 0;
		String listName;
		HashMap<String, Integer> results = new HashMap<>();
		for (Vote v : election.getVotes()) {
			listName = v.getVotedListName();

			if (!listName.equals(Vote.NULL_VOTE)) {
				results.putIfAbsent(listName, 0);
				results.put(listName, results.get(listName) + 1);
				total++;
			} else {
				nullVotes++;
			}
		}

		/* Printing */
		if(admin != null)
			admin.print(" - " + election.getName());
		output = output + " - " + election.getName() + "\n";
		if (total + nullVotes != 0) {
			for (Map.Entry<String, Integer> entry : results.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				if(admin != null)
					admin.print("\tList: " + key + "\t Votes:" + value + "\t % " + 100 * value / (float) total);
				output = output + "\tList: " + key + "\t Votes:" + value + "\t % " + 100 * value / (float) total  + "\n";
			}
			if(admin != null)
				admin.print("\tNull Votes: " + nullVotes);
			output = output + "\tNull Votes: " + nullVotes + "\n";
		} else {
			if(admin != null)
				admin.print("\tNo Votes available");
			output = output + "\tNo Votes available" + "\n";
		}
		return output;
	}

	/**
	 * Method to print the voting acts of a given person
	 * @param admin the administrator console that made the request
	 * @param personID the ID of the target Person
	 * @return string with the printed message
	 * @throws RemoteException
	 */
	@Override
	public synchronized String printElectorVotesInfo(RmiAdminConsoleInterface admin, int personID) throws RemoteException {
		String output = "";
		for (Election<?> e : this.elections) {
			for (Vote v : e.getVotes()) {
				if (v.getPersonID() == personID) {
					if (admin != null)
						admin.print(" [" + e.getName() + "]\n\tDesk: " + v.getVotingDeskID() + "\n\tTime: " + v.getMoment().getTime().toString());
					output = output + " [" + e.getName() + "]\n\tDesk: " + v.getVotingDeskID() + "\n\tTime: " + v.getMoment().getTime().toString() + "\n";
				}
			}
		}
		return output;
	}


	/* ################################################################################# */

	/**
	 * Builder
	 * @param elections elections CopyOnWriteArrayList
	 * @param lists lists CopyOnWriteArrayList
	 * @param people people CopyOnWriteArrayList
	 * @param dirPath path of the database directory
	 * @throws RemoteException
	 */
	public RmiServer(CopyOnWriteArrayList<Election<? extends Person>> elections,
					 CopyOnWriteArrayList<List<? extends Person>> lists,
					 CopyOnWriteArrayList<Person> people,
					 String dirPath) throws RemoteException {
		super();
		this.elections = elections;
		this.lists = lists;
		this.people = people;
		this.dirPath = dirPath;
		this.adminConsoles = new CopyOnWriteArrayList<>();
		this.multicastServers = new Hashtable<>();
	}

	/* Save Data */

	/**
	 * Method to save all elections
	 * @return
	 */
	public synchronized boolean saveElections() {
		return RmiServer.saveData(dirPath + electionsFilePath, this.elections);
	}

	/**
	 * Method to save all lists
	 * @return
	 */
	public synchronized boolean saveLists() {
		return RmiServer.saveData(dirPath + listsFilePath, this.lists);
	}

	/**
	 * Method to save all people
	 * @return
	 */
	public synchronized boolean savePeople() {
		return RmiServer.saveData(dirPath + peopleFilePath, this.people);
	}

	/**
	 * Method to save Objects on a obj File
	 * @param path path of the obj file
	 * @param object object to be stored
	 * @return true in case of success, false otherwise
	 */
	public synchronized static boolean saveData(String path, Object object) {
		try {
			FileOutputStream os = new FileOutputStream(path);
			ObjectOutputStream objOs = new ObjectOutputStream(os);
			objOs.writeObject(object);
			objOs.close();
			os.close();
		} catch (Exception e) {
			System.out.println("Could not write to: " + path);
			return false;
		}
		return true;
	}

	/* Load Data */

	/**
	 * Method to load all elections from an obj file
	 * @param dirPath path to the obj file
	 * @return CopyOnWriteArrayList with all elections
	 */
	public static synchronized CopyOnWriteArrayList<Election<? extends Person>> loadElections(String dirPath) {
		return (CopyOnWriteArrayList<Election<? extends Person>>) RmiServer.loadData(dirPath + electionsFilePath);
	}

	/**
	 * Method to load all lists from an obj file
	 * @param dirPath path to the obj file
	 * @return CopyOnWriteArrayList with all lists
	 */
	public static synchronized CopyOnWriteArrayList<List<? extends Person>> loadLists(String dirPath) {
		return (CopyOnWriteArrayList<List<? extends Person>>) RmiServer.loadData(dirPath + listsFilePath);
	}

	/**
	 * Method to load every person from an obj file
	 * @param dirPath path to the obj file
	 * @return CopyOnWriteArrayList with every person
	 */
	public static synchronized CopyOnWriteArrayList<Person> loadPeople(String dirPath) {
		return (CopyOnWriteArrayList<Person>) RmiServer.loadData(dirPath + peopleFilePath);
	}

	/**
	 * Method to load an Object from a obj file
	 * @param path path to the obj file
	 * @return true if data is loaded from the files, false otherwise
	 */
	public static synchronized Object loadData(String path) {
		try {
			FileInputStream is = new FileInputStream(path);
			ObjectInputStream objIs = new ObjectInputStream(is);
			Object data = objIs.readObject();
			objIs.close();
			is.close();
			return data;
		} catch (Exception e) {
			System.out.println("Could not read from: " + path);
			return new CopyOnWriteArrayList<>();
		}
	}

	/* ################################################################################# */

	/**
	 * Main static method - Instance of a RMI Server
	 * Controls Failover, STONITH and the RMI itself
	 * @param args socket: arg#1 = IP | arg#2 = port | arg#3 = database directory
	 */
	public static void main(String[] args) {
		final String IP, PORT, DIR;
		if  (args.length != 3) {
			System.out.println("java RmiServer <IP ADDRESS> <PORT> <Database_directory/>");
			return;
		} else {
			IP = args[0];
			PORT = args[1];
			DIR = args[2];
		}

		RmiServerInterface server = null;

		/* Failover */
		try {
			server = (RmiServerInterface) Naming.lookup("rmi://" + IP + ":" + PORT  + "/RmiServer");
			server.ping();
			System.out.println("Waiting for primary crash.");
			while (true) {
				System.out.println(server.ping());
				Thread.sleep(1000);
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("Starting Server");
		} catch (Exception e) {
			System.out.println("Exception @ RmiServer.main.failover");
		}

		/* STONITH */
		try {
			System.out.print("Trying to remove: ");
			System.out.println(server);
			Naming.unbind("rmi://" + IP + ":" + PORT  + "/RmiServer");
			System.out.println("STONITH Success");
		} catch (Exception e) {
			System.out.println("STONITH Failed: No name in RMI matches the specified one");
		}

		/* Bootloading */

		CopyOnWriteArrayList<Person> people = RmiServer.loadPeople(DIR);
		CopyOnWriteArrayList<Election<? extends Person>> elections = RmiServer.loadElections(DIR);
		CopyOnWriteArrayList<List<? extends Person>> lists = RmiServer.loadLists(DIR);

		/* Run Server */
		try {
			server = new RmiServer(elections, lists, people, DIR);
			Naming.rebind("rmi://" + IP + ":" + PORT  + "/RmiServer", server);
//			Naming.rebind("rmi://localhost:7000/RmiServer", server);
			System.out.println("RmiServer ready! - Running on " + IP + ":" + PORT);
		} catch (Exception e) {
			System.out.println("Exception in RMI Server: Shutting Down");
		}
	}
}

