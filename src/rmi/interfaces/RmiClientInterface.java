package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiClientInterface extends Remote {
    default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    String getRealTimeElectionName() throws RemoteException;

    boolean allowRealTimeDesks() throws RemoteException;

}
