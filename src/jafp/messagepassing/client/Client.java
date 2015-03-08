package jafp.messagepassing.client;

import jafp.messagepassing.server.SensorServerRemote;
import jafp.messagepassing.server.Server;

import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            SensorServerRemote remote = (SensorServerRemote) registry.lookup(Server.RMI_NAME);
            
            double prev = 0.0;
            while (true) {
            	double avg = remote.getAverageTemperature();
            	if (avg != prev) {
            		prev = avg;
            		System.out.println("[" + remote.getNumberOfSamples() + "] Average temperature: " + avg);
            	}
            	
            	// Check if remote is still bound
            	// (Trying to lookup an service that is not 
            	// bound will throw a Not bound exception)
            	try {
            		registry.lookup(Server.RMI_NAME);
            	} catch (NotBoundException e) {
            		break;
            	}
            	
            	Thread.sleep(50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client done (connection to server closed)");
	}
}
