package core.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import core.Configuration;
import core.actions.Action;

import java.util.Map;

public class UserInterceptor implements Interceptor, Configuration {
    @Override
    public void destroy() {

    }

    @Override
    public void init() {

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map<String, Object> session = actionInvocation.getInvocationContext().getSession();
        if (!(boolean) session.get(ADMIN_MODE_KEY)) {
            return actionInvocation.invoke();
        }
        return Action.ERROR;
    }
}
