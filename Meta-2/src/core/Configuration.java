package core;

import core.models.RmiConnector;

public interface Configuration {

    /* Custom Action Return Values */
    String USER = "user";
    String ADMIN = "admin";

    /* Connection and Session Settings */
    String IP = "localhost";
    String PORT = "7000";
    String SERVER_NAME = "RmiServer";

    /* Login Settings */
    String ADMIN_USERNAME = "admin";
    String ADMIN_PASSWORD = "sudo";
}
