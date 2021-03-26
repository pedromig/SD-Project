package rmi.interfaces;

import java.rmi.Remote;

public interface RmiClientInterface extends Remote {
    default void print(String msg){
        System.out.println(msg);
    }

}
