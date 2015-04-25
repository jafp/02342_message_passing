package jafp.pubsub;

import jafp.util.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class QueueServer extends Thread {
	
	public static int PORT = 45332;
	
	private ArrayList<Connection> m_connections;
	
	public QueueServer() {
		m_connections = new ArrayList<Connection>();
	}
	
	@Override
	public void run() {
		ServerSocket socket = null;
		try {
			int socketCounter = 0;
			socket = new ServerSocket(PORT);
			
			while (true) {
				Socket sock = socket.accept();
				
				Connection conn = new Connection(++socketCounter, this, sock);
				addConnection(conn);
				conn.start();
				
				System.out.println("New connection #" + conn.getNo() +  " - " + sock.getInetAddress());
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	private class Connection extends Thread {
		
		private int m_no;
		private Socket m_socket;
		private QueueServer m_server;
		private ArrayList<String> m_subscriptions;
	
		
		public Connection(int no, QueueServer server, Socket socket) {
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
				System.out.println("Broken (1) #" + m_no);
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
						m_subscriptions.add(ev.getName());
					}
				}
			
				System.out.println("Closing connection #" + m_no);
				m_server.removeConnection(this);
				m_socket.close();
			} catch (IOException e) {
				System.out.println("Broken (2) #" + m_no);
				m_server.removeConnection(this);
			}
		}
	}
	

	public static void main(String[] args) throws InterruptedException {
		QueueServer server = new QueueServer();
		server.start();
		
		System.out.println("Queue server started on port " + PORT);
		server.join();
		
		System.out.println("Stopped.");
	}
}
