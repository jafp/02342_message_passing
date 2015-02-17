package jafp.messagepassing.server;

import jafp.messagepassing.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	
	private ServerSocket m_socket;
	private int m_port;
	
	public Server(int port) throws IOException {
		m_port = port;
		m_socket = new ServerSocket(m_port);
	}

	@Override
	public void run() {
		System.out.println("Starting server on port " + m_port);
		
		try { 
			Socket socket = m_socket.accept();
			System.out.println("Accepted socket / sensor connection");
			
			long sum = 0;
			long samples = 0; 
			double average = 0;
			
			// Buffer for storing messages when read from the socket,
			// before they are de-serialized into messages
			byte[] buf = new byte[Message.MESSAGE_SIZE];
			
			// Read at minimum the size of message
			while (socket.getInputStream().read(buf) != -1) {
				Message message = Message.deserialize(buf);
				
				// Sum values and calculate average
				samples++;
				sum += message.getValue();
				average = (double) sum / samples;
				
				System.out.format("[Temperature] Current: %d C, Average: %.2f C\n", 
						message.getValue(), average);
			}
			
			System.out.println("Done (sensor closed connection)");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
