package jafp.messagepassing.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SensorServerRemote extends Remote {
	double getAverageTemperature() throws RemoteException;
	long getNumberOfSamples() throws RemoteException;
}
