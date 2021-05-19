package core.actions;

import com.opensymphony.xwork2.ActionSupport;
import core.Configuration;
import core.models.RmiConnector;
import org.apache.struts2.interceptor.SessionAware;
import utils.elections.Election;
import utils.lists.List;
import utils.people.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Action extends ActionSupport implements SessionAware, Configuration {
    protected Map<String, Object> session;

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    /* Builder */
    public RmiConnector getRmiConnector() {
        try {
            ((RmiConnector)(this.session.get(RMI_CONNECTOR_KEY))).getServer().ping();
        } catch (Exception e) {
            RmiConnector rmiConnector = new RmiConnector();
            this.setRmiConnector(rmiConnector);
            this.setServerOnline(rmiConnector.getServer() != null);
        }
        return (RmiConnector) session.get(RMI_CONNECTOR_KEY);
    }
    /* Connect to RMI */
    public void setRmiConnector(RmiConnector rmiConnector) {
        this.session.put(RMI_CONNECTOR_KEY, rmiConnector);
    }
    public void setServerOnline(boolean bool) {
        this.session.put(SERVER_STATUS_KEY, bool);
    }
    public boolean getServerOnline(){
        return (boolean) this.session.get(SERVER_STATUS_KEY);
    }

    /* Login Methods */
    public void setLogin(String username, String password, Boolean isAdmin) {
        this.session.put(USERNAME_KEY, username);
        this.session.put(PASSWORD_KEY, password);
        this.session.put(ADMIN_MODE_KEY, isAdmin);
    }
    public String getUsername()  {
        return (String) this.session.get(USERNAME_KEY);
    }
    public String getPassword() {
        return (String) this.session.get(PASSWORD_KEY);
    }
    public void clearLogin() {
        this.setLogin(null, null, null);
    }

    /* Objects toString() */
    public void setElections(CopyOnWriteArrayList<Election<?>> elections) {
        ArrayList<String> prints = new ArrayList<>();
        for (Election<?> e : elections) {
            prints.add(e.toString());
        }
        this.session.put(ELECTIONS_PRINT_KEY, prints);
    }
    public void setLists(CopyOnWriteArrayList<List<?>> lists) {
        ArrayList<String> prints = new ArrayList<>();
        for (List<?> l : lists) {
            prints.add(l.toString());
        }
        this.session.put(LISTS_PRINT_KEY, prints);
    }
    public void setPeople(CopyOnWriteArrayList<Person> people) {
        ArrayList<String> prints = new ArrayList<>();
        for (Person p : people) {
            prints.add(p.toString());
        }
        this.session.put(PEOPLE_PRINT_KEY, prints);
    }

    /* Ended Log*/
    public void setEndedElectionsLog(CopyOnWriteArrayList<Election<?>> elections) {
        ArrayList<String> prints = new ArrayList<>();
        for (Election<?> e : elections) {
            prints.add(this.getRmiConnector().getEndedLog(e));
        }
        this.session.put(ENDED_ELECTIONS_LOG_KEY, prints);
    }

    /* Person Audit*/
    public void setPeopleAudit(String info) {
        this.session.put(PEOPLE_AUDIT_KEY, info);
    }

    /* Selectable objects Getters and Setters */
    public HashMap<Integer, String> makeSelectablePeople(CopyOnWriteArrayList<Person> people) {
        HashMap<Integer, String> names = new HashMap<>();
        for (Person p : people) {
            names.put(p.getIdentityCardNumber(), p.getName() + " - " + p.getIdentityCardNumber());
        }
        this.session.put(SELECTABLE_PEOPLE_KEY, people);
        return names;
    }
    public CopyOnWriteArrayList<Person> getSelectablePeople() {
        return (CopyOnWriteArrayList<Person>) this.session.get(SELECTABLE_PEOPLE_KEY);
    }
    public void setSelectedPerson(Integer personID) {
        this.session.put(SELECTED_PERSON_KEY, personID);
    }
    public Integer getSelectedPerson() {
        return (Integer) this.session.get(SELECTED_PERSON_KEY);
    }

    public HashMap<Integer, String> makeSelectableLists(CopyOnWriteArrayList<List<?>> lists) {
        HashMap<Integer, String> names = new HashMap<>();
        for (int i = 0; i < lists.size(); i++) {
            names.put(i, lists.get(i).getName());
        }
        this.session.put(SELECTABLE_LISTS_KEY, lists);
        return names;
    }
    public CopyOnWriteArrayList<List<?>> getSelectableLists() {
        return (CopyOnWriteArrayList<List<?>>) this.session.get(SELECTABLE_LISTS_KEY);
    }
    public void setSelectedList(Integer idx) {
        this.session.put(SELECTED_LIST_KEY, idx);
    }
    public Integer getSelectedList() {
        return (Integer) this.session.get(SELECTED_LIST_KEY);
    }

    public HashMap<Integer, String> makeSelectableElections(CopyOnWriteArrayList<Election<?>> elections) {
        HashMap<Integer, String> names = new HashMap<>();
        for (int i = 0; i < elections.size(); i++) {
            names.put(i, elections.get(i).getName());
        }
        this.session.put(SELECTABLE_ELECTIONS_KEY, elections);
        return names;
    }
    public CopyOnWriteArrayList<Election<?>> getSelectableElections() {
        return (CopyOnWriteArrayList<Election<?>>) this.session.get(SELECTABLE_ELECTIONS_KEY);
    }
    public void setSelectedElection(Integer idx) {
        this.session.put(SELECTED_ELECTION_KEY, idx);
    }
    public Integer getSelectedElection() {
        return (Integer) this.session.get(SELECTED_ELECTION_KEY);
    }
}
