package core.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import core.Configuration;
import core.actions.Action;


import java.util.Map;

public class LoginInterceptor implements Interceptor, Configuration {

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map<String, Object> session = actionInvocation.getInvocationContext().getSession();
        // If there is a user logged
        if ((session.get(USERNAME_KEY) != null) && (session.get(PASSWORD_KEY) != null)) {
            System.out.println("Done: Login Interceptor");
            return actionInvocation.invoke();
        }
        System.out.println("Failed: Login Interceptor");
        return Action.ERROR;
    }

    @Override
    public void destroy() {}

    @Override
    public void init() {

    }


}
