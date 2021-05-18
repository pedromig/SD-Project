package core.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import core.Configuration;
import core.actions.Action;

import java.util.Map;

public class RmiInterceptor implements Interceptor, Configuration {

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map<String, Object> session = actionInvocation.getInvocationContext().getSession();
        // If there is a responsive RMI Server, continue
        if ((session.get(RMI_CONNECTOR_KEY) != null) && ((boolean) session.get(SERVER_STATUS_KEY))) {
            System.out.println("Done: RMI Interceptor");
            return actionInvocation.invoke();
        }
        System.out.println("Failed: RMI Interceptor");
        return Action.ERROR;
    }

    @Override
    public void init() {}

    @Override
    public void destroy() {}

}

