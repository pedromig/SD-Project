package rmi;

import rmi.interfaces.RmiAdminConsoleInterface;
import rmi.interfaces.RmiMulticastServerInterface;
import rmi.interfaces.RmiServerInterface;
import utils.Vote;
import utils.lists.List;
import utils.elections.Election;
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
	private static final String
			electionsPath = "../../../database/elections.obj",
			peoplePath = "../../../database/people.obj",
			listsPath = "../../../database/lists.obj";

	private static final String[] UC_DEPARTMENTS = new String[]{
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

	@Override
	public synchronized void subscribe(RmiAdminConsoleInterface adminConsole) throws RemoteException {
		this.adminConsoles.add(adminConsole);
	}

	@Override
	public synchronized void subscribe(RmiMulticastServerInterface multicastDesk, String name) throws RemoteException {
		System.out.println("Name: " + name);
		System.out.println("msi: "+ multicastDesk);
		this.multicastServers.put(name, multicastDesk);
	}

	@Override
	public synchronized void pingDesks(RmiAdminConsoleInterface adminConsole) throws RemoteException {
		for (Map.Entry<String, RmiMulticastServerInterface> entry : this.multicastServers.entrySet()) {
			String name = entry.getKey();
			RmiMulticastServerInterface msi = entry.getValue();
			try {
				String additionalInfo = msi.ping();
				adminConsole.print("Server[" + name + "]: ON");
				adminConsole.print(additionalInfo);
			} catch (Exception e) {
				adminConsole.print("Server[" + name + "]: OFF");
			}
		}
	}

	//TODO: REMOVE THIS FUNC AFTER DEPLOY (its for debug only)
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

	@Override
	public String[] getDepartments() throws RemoteException {
		return UC_DEPARTMENTS;
	}

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

	@Override
	public synchronized void editElectionDescription(String electionName, String newDescription) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.compareDates(new GregorianCalendar(), election.getStartDate())) {
			election.setDescription(newDescription);
			this.saveElections();
		}
	}

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

	@Override
	public synchronized void editElectionEndDate(String electionName, GregorianCalendar newDate) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (this.compareDates(new GregorianCalendar(), election.getStartDate()) &&
			this.compareDates(election.getStartDate(), newDate)) {
			election.setEndDate(newDate);
			this.saveElections();
		}
	}

	@Override
	public synchronized void addDepartment(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (!election.getDepartments().contains(departmentName)) {
			election.getDepartments().add(departmentName);
			this.saveElections();
		}
	}

	@Override
	public synchronized void removeDepartment(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (election.getDepartments().contains(departmentName)) {
			election.getDepartments().remove(departmentName);
			this.saveElections();
		}
	}

	@Override
	public synchronized void addRestriction(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (!election.getRestrictions().contains(departmentName)) {
			election.getRestrictions().add(departmentName);
			this.saveElections();
		}
	}

	@Override
	public synchronized void removeRestriction(String electionName, String departmentName) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		if (election.getRestrictions().contains(departmentName)) {
			election.getRestrictions().remove(departmentName);
			this.saveElections();
		}
	}

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

	@Override
	public synchronized Election<?> getElection(String electionName) throws RemoteException {
		for (Election<?> e : this.elections)
			if (e.getName().equals(electionName))
				return e;
		return null;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Election<?>> getFutureElections() throws RemoteException {
		CopyOnWriteArrayList<Election<?>> futureElections = new CopyOnWriteArrayList<>();
		for (Election<?> e : this.elections)
			if (this.compareDates(new GregorianCalendar(), e.getStartDate()))
				futureElections.add(e);
		System.out.println("REQUEST: FUTURE ELECTIONS");
		return futureElections;
	}

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

	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = new CopyOnWriteArrayList<>();
		for (List<?> l : this.lists)
			if (l.getType() == type)
				lists.add(l);
		System.out.println("REQUEST: Lists<" + type.getName() + ">");
		return lists;
	}

	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsAssignedOfType(Class<?> type, String electionName) throws RemoteException {
		CopyOnWriteArrayList<List<?>> lists = this.getListsOfType(type);
		lists.removeIf(l -> l.getElectionName() == null || !l.getElectionName().equals(electionName));
		System.out.println("REQUEST: Lists<" + type.getName() + "> electionName == " + electionName);
		return lists;
	}

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

	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getListsUnassignedOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<List<?>> listsOfType = getListsOfType(type);
		listsOfType.removeIf(l -> l.getElectionName() != null);
		System.out.println("REQUEST: Lists<" + type.getName() + "> electionName == null");
		return listsOfType;
	}

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

	@Override
	public synchronized CopyOnWriteArrayList<List<?>> getEditableLists() throws RemoteException {
		CopyOnWriteArrayList<List<?>> futureLists = this.getFutureLists();
		CopyOnWriteArrayList<List<?>> unassignedLists = this.getListsUnassigned();
		futureLists.addAll(unassignedLists);
		return futureLists;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeople() throws RemoteException {
		return this.people;
	}

	@Override
	public synchronized Person getPerson(int personId) throws RemoteException {
		for (Person p : this.people) {
			if (p.getIdentityCardNumber() == personId) {
				return p;
			}
		}
		return null;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<Person> people = new CopyOnWriteArrayList<>();
		for (Person p : this.people) {
			if (p.getType() == type)
				people.add(p);
		}
		return people;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleAssignedOfType(Class<?> type, String listName) throws RemoteException {
		CopyOnWriteArrayList<Person> people = this.getPeopleOfType(type);
		people.removeIf(p -> p.getList() == null || !p.getList().equals(listName));
		return people;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleUnassigned() throws RemoteException {
		CopyOnWriteArrayList<Person> people = new CopyOnWriteArrayList<>();
		for (Person p : this.people) {
			if (p.getList() == null) {
				people.add(p);
			}
		}
		return people;
	}

	@Override
	public synchronized CopyOnWriteArrayList<Person> getPeopleUnassignedOfType(Class<?> type) throws RemoteException {
		CopyOnWriteArrayList<Person> people = this.getPeopleOfType(type);
		people.removeIf(p -> p.getList() != null);
		return people;
	}

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

	@Override
	public synchronized boolean hasVoted(String electionName, int personID) throws RemoteException {
		Election<?> election = this.getElection(electionName);
		for (Vote v : election.getVotes())
			if (v.getPersonID() == personID)
				return true;
		return false;
	}

	@Override
	public synchronized void printVotingProcessedData(RmiAdminConsoleInterface admin, Election<?> election) throws RemoteException {
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
		admin.print(" - " + election.getName());
		if (total + nullVotes != 0) {
			for (Map.Entry<String, Integer> entry : results.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				admin.print("\tList: " + key + "\t Votes:" + value + "\t % " + 100 * value / (float) total);
			}
			admin.print("\tNull Votes: " + nullVotes);
		} else {
			admin.print("\tNo Votes available");
		}
	}

	@Override
	public synchronized void printElectorVotesInfo(RmiAdminConsoleInterface admin, int personID) throws RemoteException {
		for (Election<?> e : this.elections) {
			for (Vote v : e.getVotes()) {
				if (v.getPersonID() == personID) {
					admin.print(" [" + e.getName() + "]\n\tDesk: " + v.getVotingDeskID() + "\n\tTime: " + v.getMoment().getTime().toString());
				}
			}
		}
	}


	/* ################################################################################# */

	public RmiServer(CopyOnWriteArrayList<Election<? extends Person>> elections,
					 CopyOnWriteArrayList<List<? extends Person>> lists,
					 CopyOnWriteArrayList<Person> people) throws RemoteException {
		super();
		this.elections = elections;
		this.lists = lists;
		this.people = people;
		this.adminConsoles = new CopyOnWriteArrayList<>();
		this.multicastServers = new Hashtable<>();
	}

	/* Save Data */
	public synchronized boolean saveElections() {
		return RmiServer.saveData(electionsPath, this.elections);
	}

	public synchronized boolean saveLists() {
		return RmiServer.saveData(listsPath, this.lists);
	}

	public synchronized boolean savePeople() {
		return RmiServer.saveData(peoplePath, this.people);
	}

	public synchronized static boolean saveData(String path, Object object) {
		try {
			FileOutputStream os = new FileOutputStream(path);
			ObjectOutputStream objOs = new ObjectOutputStream(os);
			objOs.writeObject(object);
			objOs.close();
			os.close();
		} catch (Exception e) {
			System.out.println("DEBUG: Could not write to: " + path);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/* Load Data */
	public static synchronized CopyOnWriteArrayList<Election<? extends Person>> loadElections() {
		return (CopyOnWriteArrayList<Election<? extends Person>>) RmiServer.loadData(electionsPath);
	}

	public static synchronized CopyOnWriteArrayList<List<? extends Person>> loadLists() {
		return (CopyOnWriteArrayList<List<? extends Person>>) RmiServer.loadData(listsPath);
	}

	public static synchronized CopyOnWriteArrayList<Person> loadPeople() {
		return (CopyOnWriteArrayList<Person>) RmiServer.loadData(peoplePath);
	}

	public static synchronized Object loadData(String path) {
		try {
			FileInputStream is = new FileInputStream(path);
			ObjectInputStream objIs = new ObjectInputStream(is);
			Object data = objIs.readObject();
			objIs.close();
			is.close();
			return data;
		} catch (Exception e) {
			System.out.println("DEBUG: Could not read from: " + path);
			return new CopyOnWriteArrayList<>();
		}
	}

	/* ################################################################################# */

	public static void main(String[] args) {
		final String IP, PORT;
		if  (args.length != 2) {
			System.out.println("java RmiServer <IP ADDRESS> <PORT>");
			return;
		} else {
			IP = args[0];
			PORT = args[1];
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

		CopyOnWriteArrayList<Person> people = RmiServer.loadPeople();
		CopyOnWriteArrayList<Election<? extends Person>> elections = RmiServer.loadElections();
		CopyOnWriteArrayList<List<? extends Person>> lists = RmiServer.loadLists();

		/* Run Server */
		try {
			server = new RmiServer(elections, lists, people);
			Naming.rebind("rmi://" + IP + ":" + PORT  + "/RmiServer", server);
			System.out.println("RmiServer ready! - Running on " + IP + ":" + PORT);
		} catch (Exception e) {
			System.out.println("[DEBUG] Exception in RMI Server: " + e);
		}
	}
}
