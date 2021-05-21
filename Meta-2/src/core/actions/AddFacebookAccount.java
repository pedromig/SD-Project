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


public class AddFacebookAccount extends Action implements Configuration {
	private String code;

	@Override
	public String execute() {
		RmiServerInterface server = super.getRmiConnector().getServer();
		OAuthService service = (OAuthService) session.get(ACCOUNT_SERVICE_KEY);
		Token token = service.getAccessToken(null, new Verifier(code));

		OAuthRequest request = new OAuthRequest(Verb.GET, CLIENT_ENDPOINT_URL, service);
		service.signRequest(token, request);
		Response response = request.send();

		if (response.getCode() == 200) {
			JSONParser parser = new JSONParser();
			try {
				JSONObject json = (JSONObject) parser.parse(response.getBody());
				String id = (String) json.get("id");

				int idNumber = Integer.parseInt((String) session.get(USERNAME_KEY));
				for (Person p : server.getPeople()) {
					if (p.getIdentityCardNumber() == idNumber){
						// This cant be a setter it need to be a editPersonID();
						p.setFacebookID(id);
						return SUCCESS;
					}
				}
			} catch (ParseException | RemoteException e) {
				System.err.println("Add Account Exception!!");
			}
		}
		return ERROR;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
