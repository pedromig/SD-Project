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
		OAuthService service = (OAuthService) session.get(SERVICE_KEY);
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
					// get facebook id check if it is equal and then redirect to the user or admin page
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
