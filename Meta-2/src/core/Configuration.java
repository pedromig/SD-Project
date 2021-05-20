package core;

public interface Configuration {

	/* Custom Action Return Values */
	String USER = "user";
	String ADMIN = "admin";

	/* Connection Settings */
	String IP = "localhost";
	String PORT = "7000";
	String SERVER_NAME = "RmiServer";

	/* Login Settings */
	String ADMIN_USERNAME = "admin";
	String ADMIN_PASSWORD = "sudo";

	/* Admin Session Configs */
	String STUDENT = "Student";
	String TEACHER = "Teacher";
	String EMPLOYEE = "Employee";
	String STRUTS_DATE_FORMAT = "yyyy-MM-dd";
	String STRUTS_TIME_FORMAT = "HH:mm";

	/* Session Map Keys */
	String RMI_CONNECTOR_KEY = "rmiConnector";
	String SERVER_STATUS_KEY = "rmiServerOnline";

	String USERNAME_KEY = "username";
	String PASSWORD_KEY = "password";
	String ADMIN_MODE_KEY = "isAdmin";

	String ELECTIONS_PRINT_KEY = "elections";
	String LISTS_PRINT_KEY = "lists";
	String PEOPLE_PRINT_KEY = "people";

	String ENDED_ELECTIONS_LOG_KEY = "endedElectionsLog";
	String PEOPLE_AUDIT_KEY = "peopleAudit";

	String SELECTABLE_PEOPLE_KEY = "selectablePeopleKey";
	String SELECTABLE_ELECTIONS_KEY = "selectableElectionsKey";
	String SELECTABLE_LISTS_KEY = "selectableListsKey";

	String SELECTED_PERSON_KEY = "selectedPersonKey";
	String SELECTED_ELECTION_KEY = "selectedElectionKey";
	String SELECTED_LIST_KEY = "selectedListKey";

	String SERVICE_KEY = "oauthSession";
	String AUTH_URL_KEY = "authURL";

	String API_KEY = "1257434004717144";
	String API_SECRET = "61f5c98dd683b9d4a6d9cf51685213a3";
	String[] REQUIRED_PERMISSIONS = {"public_profile"};

	String CLIENT_ENDPOINT_URL = "https://graph.facebook.com/me/";
	String CALLBACK_URL = "http://localhost:8080/Meta_2_war_exploded/loginWithFacebook";


}
