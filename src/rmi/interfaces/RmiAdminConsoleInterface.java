package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiAdminConsoleInterface extends Remote {

    default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    String getRealTimeElectionName() throws RemoteException;

}