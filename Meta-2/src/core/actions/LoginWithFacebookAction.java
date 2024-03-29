package core.actions;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import core.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rmiserver.interfaces.RmiServerInterface;
import utils.people.Person;

import java.rmi.RemoteException;

public class LoginWithFacebookAction extends Action implements Configuration {
	private String code;

	@Override
	public String execute() {
		RmiServerInterface server = super.getRmiConnector().getServer();
		OAuthService service = (OAuthService) session.get(LOGIN_SERVICE_KEY);
		Token token = service.getAccessToken(null, new Verifier(code));

		OAuthRequest request = new OAuthRequest(Verb.GET, CLIENT_ENDPOINT_URL, service);
		service.signRequest(token, request);
		Response response = request.send();

		if (response.getCode() == 200) {
			JSONParser parser = new JSONParser();
			try {
				JSONObject json = (JSONObject) parser.parse(response.getBody());
				String id = (String) json.get("id");

				for (Person p : server.getPeople()) {
					System.out.println(p.getName());
					System.out.println(p.getFacebookID());
					if (p.getFacebookID().equals(id)) {
						if (p.getName().equals(ADMIN_USERNAME) && p.getPassword().equals(ADMIN_PASSWORD)) {
							super.getRmiConnector().getServer().ping();
							super.setLogin(p.getName(), p.getPassword(), true);
							return ADMIN;
						}

						if (super.getRmiConnector().checkLogin(p.getIdentityCardNumber(), p.getPassword())) {
							super.setLogin(p.getName(), p.getPassword(), false);
							return LOGIN;
						}
					}
				}
			} catch (ParseException | RemoteException e) {
				System.err.println("Login Exception!!");
			}
		}
		return ERROR;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
