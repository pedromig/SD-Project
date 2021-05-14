package actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;

    @Override
    public String execute() {
        return LOGIN;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
