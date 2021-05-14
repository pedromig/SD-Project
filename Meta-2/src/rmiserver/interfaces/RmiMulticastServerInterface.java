package rmiserver.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An RMI MulticastServer Client Interface
 */
public interface RmiMulticastServerInterface extends Remote {

	/**
	 * Method to get the name of the corresponding department
	 * @return name of the department
	 * @throws RemoteException
	 */
	String getName() throws RemoteException;

	/**
	 * Remote Print
	 * @param msg String to be printed in the server
	 * @throws RemoteException
	 */
	default void print(String msg) throws RemoteException {
		System.out.println(msg);
	}

	/**
	 * A method to ping the server
	 * @return String "Ping Pong"
	 * @throws RemoteException
	 */
	default String ping() throws RemoteException {
		return "Ping Pong";
	}

}
