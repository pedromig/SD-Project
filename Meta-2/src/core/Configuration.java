package core;

public interface Configuration {

    /* Custom Action Return Values */
    String USER = "user";
    String ADMIN = "admin";

    /* Connection Settings */
    String IP = "localhost";
    String PORT = "7000";
    String SERVER_NAME = "RmiServer";

    /* Session Settings*/
    String RMI_CONNECTOR_KEY = "rmiConnector";
    String SERVER_STATUS_KEY = "rmiServerOnline";

    /* Login Settings */
    String ADMIN_USERNAME = "admin";
    String ADMIN_PASSWORD = "sudo";

    String USERNAME_KEY = "username";
    String PASSWORD_KEY = "password";
    String ADMIN_MODE_KEY = "isAdmin";
}
