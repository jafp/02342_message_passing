package jafp.messagepassing.server;

import jafp.messagepassing.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server.
 * 
 * Waits for a socket to connect, and then continuously reads messages
 * till the sensor closes the socket. 
 * 
 * All the values received from the temperature sensor is summed, 
 * and the average is printed.
 * 
 */
public class Server implements Runnable {
	
	public static final String RMI_NAME = "SensorServerRemote";
	
	private ServerSocket m_socket;
	private SensorServerRemoteImpl m_remote; 
	private int m_port;
	private long m_samples;
	private double m_average;
	
	public Server(int port) throws IOException {
		m_port = port;
		m_socket = new ServerSocket(m_port);
		m_remote = new SensorServerRemoteImpl();	
	}

	@Override
	public void run() {
		System.out.println("Starting server on port " + m_port);
		bindRemote();
		
		try { 
			Socket socket = m_socket.accept();
			System.out.println("Accepted socket / sensor connection");
			
			long sum = 0;
			m_samples = 0; 
			m_average = 0;
			
			// Buffer for storing messages when read from the socket,
			// before they are de-serialized into messages
			byte[] buf = new byte[Message.MESSAGE_SIZE];
			
			// Read at minimum the size of message
			while (socket.getInputStream().read(buf) != -1) {
				Message message = Message.deserialize(buf);
				
				// Sum values and calculate average
				m_samples++;
				sum += message.getValue();
				m_average = (double) sum / m_samples;
				
				// Update the RMI remote object
				m_remote.setAverageTemp(m_average);
				m_remote.setNumberOfSamples(m_samples);
				
				System.out.format("[Temperature] Current: %d C, Average: %.2f C\n", 
						message.getValue(), m_average);
			}
			
			System.out.println("Done (sensor closed connection)");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		unbindRemote();
	}
	
	private void bindRemote() {
		try {
			SensorServerRemote remote = (SensorServerRemote) UnicastRemoteObject.exportObject(m_remote, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(RMI_NAME, remote);
		} catch (RemoteException e) {
			System.err.println("ERROR: Could not bind RMI remote");
		}
	}
	
	private void unbindRemote() {
		try {
			Registry registry = LocateRegistry.getRegistry();
			registry.unbind(RMI_NAME);
		} catch (RemoteException | NotBoundException e) {
		}
	}
}
