package core.models;

import rmiserver.interfaces.RmiServerInterface;

import java.rmi.Naming;

public class RmiConnection {
    private RmiServerInterface server;
    private String username;
    private String password;

    public RmiConnection() {
        try {
            //FIXME: "rmiserver" is wrong, should be something like localhost:8000/..... (i think)
            this.server = (RmiServerInterface) Naming.lookup("rmiserver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
