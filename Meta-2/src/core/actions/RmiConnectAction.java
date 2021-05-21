package core.actions;

import com.github.scribejava.apis.FacebookApi2;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuthService;

public class RmiConnectAction extends Action {
    @Override
    public String execute() {
        if (super.getRmiConnector().getServer() != null) {

            // Login with facebook account auth URL
            ServiceBuilder serviceBuilderLogin = new ServiceBuilder();
            serviceBuilderLogin.provider(FacebookApi2.class);
            serviceBuilderLogin.apiKey(API_KEY);
            serviceBuilderLogin.apiSecret(API_SECRET);
            serviceBuilderLogin.callback(LOGIN_CALLBACK_URL);
            for (String permission : REQUIRED_PERMISSIONS)
                serviceBuilderLogin.scope(permission);

            OAuthService service = serviceBuilderLogin.build();
            session.put(LOGIN_SERVICE_KEY, service);

            String authorizationUrl = service.getAuthorizationUrl(null);
            session.put(LOGIN_AUTH_URL_KEY, authorizationUrl);

            // Add facebook account auth URL
            ServiceBuilder serviceBuilderAddAccount = new ServiceBuilder();
            serviceBuilderAddAccount.provider(FacebookApi2.class);
            serviceBuilderAddAccount.apiKey(API_KEY);
            serviceBuilderAddAccount.apiSecret(API_SECRET);
            serviceBuilderAddAccount.callback(ACCOUNT_CALLBACK_URL);
            for (String permission : REQUIRED_PERMISSIONS)
                serviceBuilderAddAccount.scope(permission);

            OAuthService serviceAddAccount = serviceBuilderAddAccount.build();
            session.put(ACCOUNT_SERVICE_KEY, serviceAddAccount);

            String authorizationUrlAddAccount = serviceAddAccount.getAuthorizationUrl(null);
            session.put(ACCOUNT_AUTH_URL_KEY, authorizationUrlAddAccount);
            return SUCCESS;
        }
        return ERROR;
    }

}
