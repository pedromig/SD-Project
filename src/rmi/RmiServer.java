package rmi;

import interfaces.RmiServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements RmiServerInterface {


    public RmiServer() throws RemoteException {
    }
}
