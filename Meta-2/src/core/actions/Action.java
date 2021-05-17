package core.actions;

import com.opensymphony.xwork2.ActionSupport;
import core.Configuration;
import core.models.RmiConnector;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

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
        System.out.println("server is " + bool);
        this.session.put(SERVER_STATUS_KEY, bool);
    }

    public boolean getServerOnline(){
        return (boolean) this.session.get(SERVER_STATUS_KEY);
    }

    public void setLogin(String username, String password, boolean isAdmin) {
        this.session.put(USERNAME_KEY, username);
        this.session.put(PASSWORD_KEY, password);
        this.session.put(ADMIN_MODE_KEY, isAdmin);
    }

    public void clearLogin() {
        this.setLogin(null, null, false);
    }
}
