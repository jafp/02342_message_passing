package jafp.messagepassing.server;

import jafp.pubsub.Channel;
import jafp.pubsub.Event;
import jafp.pubsub.SocketChannel;
import jafp.pubsub.SubscribeCallback;

import java.io.IOException;
import java.net.ServerSocket;
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
public class Server implements Runnable, SubscribeCallback {
	
	public static final String RMI_NAME = "SensorServerRemote";
	
	private ServerSocket m_socket;
	private SensorServerRemoteImpl m_remote; 
	private Channel m_channel;
	private int m_port;
	private long m_sum;
	private long m_samples;
	private double m_average;
	
	
	public Server(int port) throws IOException {
		m_port = port;
		m_socket = new ServerSocket(m_port);
		m_remote = new SensorServerRemoteImpl();
		m_channel = new SocketChannel();
		m_channel.open("localhost");
	}

	@Override
	public void run() {
		System.out.println("Starting server on port " + m_port);
		
		m_sum = 0;
		m_samples = 0;
		m_average = 0;
		
		bindRemote();
		
		try {
			m_channel.blockingSubscribe("sensor", this);
			m_channel.close();

			System.out.println("[Sensor server] Channel closed");
		} catch (IOException e) { e.printStackTrace(); }

		unbindRemote();
		
		System.out.println("[Sensor server] Done.");
	}
	
	private void bindRemote() {
		try {
			SensorServerRemote remote = (SensorServerRemote) UnicastRemoteObject.exportObject(m_remote, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(RMI_NAME, remote);
			System.out.println("[Sensor server] Bound to RMI remote");
		} catch (RemoteException e) {
			System.err.println("ERROR: Could not bind RMI remote");
		}
	}
	
	private void unbindRemote() {
		try {
			Registry registry = LocateRegistry.getRegistry();
			registry.unbind(RMI_NAME);
			System.out.println("[Sensor server] Unbound from RMI remote");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String name, String message) {
		if ("sensor".equals(name)) {	
			SensorValue val = SensorValue.parse(message);
			
			if ("temp".equalsIgnoreCase(val.getName())) {
				m_samples++;
				m_sum += val.getValue();
				m_average = (double) m_sum / m_samples;
				
				// Update the RMI remote object
				m_remote.setAverageTemp(m_average);
				m_remote.setNumberOfSamples(m_samples);
				
				System.out.format("[Temperature] Average: %.2f Current: %.2f\n", m_average, val.getValue());
			}
		}
	}
	
	public static class SensorValue {
		
		public static SensorValue parse(String data) {
			String[] parts = data.split(Event.SEPARATOR);
			return new SensorValue(parts[0], Double.valueOf(parts[1]));
		}
		
		private String m_name;
		private double m_value;
		
		public SensorValue(String name, double value) {
			m_name = name;
			m_value = value;
		}
		
		public String getName() {
			return m_name;
		}
		
		public double getValue() {
			return m_value;
		}
		
		public String getPacket() {
			return getName() + Event.SEPARATOR + String.valueOf(getValue());
		}
	}
}
