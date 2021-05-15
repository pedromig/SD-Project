package core.models;

import rmi.interfaces.RmiServerInterface;

import java.rmi.Naming;

public class RmiConnector {
    private RmiServerInterface server;
    private String username;
    private String password;

    public RmiConnector() {
        try {
            this.setServer((RmiServerInterface) Naming.lookup("rmi://localhost:7000/RmiServer"));
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
