package core.actions;

import com.github.scribejava.apis.FacebookApi2;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuthService;

public class RmiConnectAction extends Action {
    @Override
    public String execute() {
        if (super.getRmiConnector().getServer() != null) {

            ServiceBuilder serviceBuilder = new ServiceBuilder();
            serviceBuilder.provider(FacebookApi2.class);
            serviceBuilder.apiKey(API_KEY);
            serviceBuilder.apiSecret(API_SECRET);
            serviceBuilder.callback(CALLBACK_URL);
            for (String permission : REQUIRED_PERMISSIONS)
                serviceBuilder.scope(permission);

            OAuthService service = serviceBuilder.build();
            session.put(SERVICE_KEY, service);

            String authorizationUrl = service.getAuthorizationUrl(null);
            session.put(AUTH_URL_KEY, authorizationUrl);

            return SUCCESS;
        }
        return ERROR;
    }

}
