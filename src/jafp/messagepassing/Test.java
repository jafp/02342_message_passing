package jafp.messagepassing;

import jafp.messagepassing.sensor.Sensor;
import jafp.messagepassing.sensor.TemperatureSensor;
import jafp.messagepassing.server.Server;
import jafp.pubsub.PubSubServer;

/**
 * Test of the server and the temperature sensor.
 * 
 * First, a server is created and a separate thread is created
 * in which the server is ready to accept connections.
 * 
 * Next, an instance of the temperature sensor is created with
 * a socket message transport that points to the server (same address
 * and port).
 * 
 * The sensor thread is started, and the sensors sends 100 messages
 * with temperature measurements to the server.
 */
public class Test {
	public static void main(String[] args) throws Exception {
		System.out.println("Distributed Systems - Message Passing");
		System.out.println("Hand-In 1 - Jacob A. F. Pedersen\n");
		
		Server server = new Server(34567);
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		Sensor sensor = new TemperatureSensor(new SocketMessageTransport("127.0.0.1", 34567));
		sensor.connect();
		
		Thread sensorThread = new Thread(sensor);
		sensorThread.start();
		
		sensorThread.join();
		serverThread.join();
	}
}
