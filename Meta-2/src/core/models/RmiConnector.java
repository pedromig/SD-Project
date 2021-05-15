package core.models;

import rmiserver.interfaces.RmiServerInterface;

import java.rmi.Naming;

public class RmiConnector {
    private RmiServerInterface server;
    private String username;
    private String password;

    public RmiConnector() {
        try {
            //FIXME: read file!!!! "rmiserver" is wrong, should be something like localhost:8000/..... (i think)
            this.setServer((RmiServerInterface) Naming.lookup("rmiserver"));
        } catch (Exception e) {
            this.setServer(null);
            e.printStackTrace();
        }
    }

    public RmiServerInterface getServer() {
        return this.server;
    }

    public void setServer(RmiServerInterface server) {
        this.server = server;
    }
}
