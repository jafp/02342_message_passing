package jafp.pubsub;

import jafp.util.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PubSubServer extends Thread {
	
	public static int PORT = 45332;
	
	private boolean m_running;
	private ServerSocket m_socket;
	private ArrayList<Connection> m_connections;
	
	public PubSubServer() {
		m_connections = new ArrayList<Connection>();
	}
	
	@Override
	public void run() {
		m_running = true;
		m_socket = null;
		try {
			int socketCounter = 0;
			m_socket = new ServerSocket(PORT);
			
			while (m_running) {
				Socket sock = m_socket.accept();
				
				Connection conn = new Connection(++socketCounter, this, sock);
				addConnection(conn);
				conn.start();
				
				System.out.println("New connection #" + conn.getNo() +  " - " + sock.getInetAddress());
			}
			
		} catch (IOException e) {
			// Ignore
			
		} finally {
			try {
				m_socket.close();
			} catch (IOException e) { }	
		}
	}

	public void publishEvent(Event event) {
		synchronized (m_connections) {
			System.out.println("Publish event: " + event.getName() + " (" + event.getRaw() + ")");
			for (Connection conn : m_connections) {
				conn.sendIfSubscribed(event);
			}
		}
	}
	
	public void addConnection(Connection conn) {
		synchronized (m_connections) {
			m_connections.add(conn);
		}
	}
	
	public void removeConnection(Connection conn) {
		synchronized (m_connections) {
			m_connections.remove(conn);
		}
	}
	
	public void shutdown() {
		try {
			m_running = false;
			m_socket.close();
		} catch (IOException e) { }
	}

	private class Connection extends Thread {
		
		private int m_no;
		private Socket m_socket;
		private PubSubServer m_server;
		private ArrayList<String> m_subscriptions;
	
		
		public Connection(int no, PubSubServer server, Socket socket) {
			m_no = no;
			m_socket = socket;
			m_server = server;
			m_subscriptions = new ArrayList<String>();
		}
		
		public int getNo() {
			return m_no;
		}
		
		public void sendIfSubscribed(Event event) {
			try {
				if (m_subscriptions.contains(event.getName())) {
					Sockets.blockingSendPacked(m_socket, event.getRaw());
				}
			} catch (IOException e) {
				System.out.println("[" + m_no + "] Broken socket (1)");
				m_server.removeConnection(this);
			}
			
		}
		
		@Override
		public void run() {
			try {
				String data = null;
				while ((data = Sockets.blockingReceivePacked(m_socket)) != null) {
					Event ev = Event.parse(data);
					
					if (ev.isPublish()) {
						m_server.publishEvent(ev);
						
					} else if (ev.isSubscribe()) {
						System.out.println("[" + m_no + "] Subscribe to '" + ev.getName() + "'");
						m_subscriptions.add(ev.getName());
						
					} else if (ev.isUnsubscribe()) {
						System.out.println("[" + m_no + "] Unsubscribe from '" + ev.getName() + "'");
						m_subscriptions.remove(ev.getName());
						
					} else if (ev.isShutdown()) {
						System.out.println("[" + m_no + "] Requested shutdown");
						
						m_server.shutdown();
						break;
					}
				}
			
				System.out.println("[" + m_no + "] Closing");
				m_server.removeConnection(this);
				m_socket.close();
				
			} catch (IOException e) {
				System.out.println("[" + m_no + "] Broken socket (2)");
				m_server.removeConnection(this);
			}
		}
	}
	

	public static void main(String[] args) throws InterruptedException {
		PubSubServer server = new PubSubServer();
		server.start();
		
		System.out.println("Pubsub server started on port " + PORT);
		server.join();
		
		System.out.println("Stopped.");
	}
}
