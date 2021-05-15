package core.actions;

import com.opensymphony.xwork2.ActionSupport;
import core.models.RmiConnector;
import org.apache.struts2.interceptor.SessionAware;
import rmi.interfaces.RmiServerInterface;

import java.util.Map;

public class RmiConnectAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private static final String DEFAULT_SERVER_NAME = "RmiServer";

    @Override
    public String execute() {
        return LOGIN;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public RmiConnector getRmiConnector() {
        if(!session.containsKey(DEFAULT_SERVER_NAME) || (((RmiConnector)(session.get(DEFAULT_SERVER_NAME))).getServer() == null))
            this.setRmiConnector(new RmiConnector());
        return (RmiConnector) session.get(DEFAULT_SERVER_NAME);
    }

    public void setRmiConnector(RmiConnector server) {
        this.session.put(DEFAULT_SERVER_NAME, server);
    }
}
