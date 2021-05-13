package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An RMI Administrator Console Client Interface
 */
public interface RmiAdminConsoleInterface extends Remote {

    /**
     * Remote Print
     * @param msg String to be printed in the server
     * @throws RemoteException
     */
    default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    /**
     * A remote getter implementation for the realTimeElectionName attribute
     * @return this realTimeElectionName
     * @throws RemoteException
     */
    String getRealTimeElectionName() throws RemoteException;

}
