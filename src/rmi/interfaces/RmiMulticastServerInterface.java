package rmi.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiMulticastServerInterface extends Remote {

    default void print(String msg) throws RemoteException {
        System.out.println(msg);
    }

    default String ping() throws RemoteException {
        return "Ping Pong";
    }

}
