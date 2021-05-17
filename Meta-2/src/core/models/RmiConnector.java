package core.models;

import core.Configuration;
import rmiserver.interfaces.RmiServerInterface;
import utils.people.Person;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class RmiConnector implements Configuration {
    private RmiServerInterface server;

    public RmiConnector() {
        try {
            this.server = (RmiServerInterface) Naming.lookup("rmi://" + IP + ":" + PORT + "/" + SERVER_NAME);
            System.out.println("it runs baby");
            server.print("hellp");
        } catch (Exception e) {
            this.server = null;
            e.printStackTrace();
        }
    }

    public RmiServerInterface getServer() {
        return this.server;
    }

    public void setServer(RmiServerInterface server) {
        this.server = server;
    }

    public boolean checkLogin(int idCardNumber, String password) throws RemoteException {
        Person person = this.server.getPerson(idCardNumber);
        return (person != null) && person.getPassword().equals(password);
    }
}
