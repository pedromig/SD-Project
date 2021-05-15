package core.models;

import rmiserver.interfaces.RmiServerInterface;

import java.rmi.Naming;

public class RmiConnector {
    private RmiServerInterface server;
    private String username;
    private String password;

    public RmiConnector() {
        try {
            this.setServer((RmiServerInterface) Naming.lookup("rmi://localhost:7000/RmiServer"));
            System.out.println("it runs baby");
            server.print("hellp");
        } catch (Exception e) {
            this.setServer(null);
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new RmiConnector();
    }

    public RmiServerInterface getServer() {
        return this.server;
    }

    public void setServer(RmiServerInterface server) {
        this.server = server;
    }
}
