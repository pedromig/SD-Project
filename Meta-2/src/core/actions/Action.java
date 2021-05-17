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
        if(!this.session.containsKey(SERVER_NAME) || (((RmiConnector)(this.session.get(SERVER_NAME))).getServer() == null))
            this.setRmiConnector(new RmiConnector());
        return (RmiConnector) session.get(SERVER_NAME);
    }

    public void setRmiConnector(RmiConnector server) {
        this.session.put(SERVER_NAME, server);
    }

}
