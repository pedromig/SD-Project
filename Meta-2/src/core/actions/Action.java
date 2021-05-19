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

    public void setRmiConnector(RmiConnector rmiConnector) {
        this.session.put(RMI_CONNECTOR_KEY, rmiConnector);
    }

    public void setServerOnline(boolean bool) {
        this.session.put(SERVER_STATUS_KEY, bool);
    }

    public boolean getServerOnline(){
        return (boolean) this.session.get(SERVER_STATUS_KEY);
    }


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


    public void setEndedElectionsLog(CopyOnWriteArrayList<Election<?>> elections) {
        ArrayList<String> prints = new ArrayList<>();
        for (Election<?> e : elections) {
            prints.add(this.getRmiConnector().getEndedLog(e));
        }
        this.session.put(ENDED_ELECTIONS_LOG_KEY, prints);
    }


}
