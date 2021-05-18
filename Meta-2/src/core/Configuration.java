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

    String ELECTIONS_KEY = "elections";
    String LISTS_KEY = "lists";
    String PEOPLE_KEY = "people";

    String ENDED_ELECTIONS_LOG_KEY = "endedElectionsLog";

}
