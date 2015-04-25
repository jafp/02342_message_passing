package jafp.pubsub;

import jafp.util.Sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketChannel implements Channel, Runnable {

	private Socket m_socket;
	private Thread m_thread;
	private boolean m_running;
	private Map<String, SubscribeCallback> m_subscriptions;
	
	public SocketChannel() {
		m_subscriptions = new HashMap<String, SubscribeCallback>();
		m_thread = new Thread(this);
	}
	
	@Override
	public void open(String host) throws IOException {
		m_socket = new Socket(host, PubSubServer.PORT);
		m_thread.start();
	}

	@Override
	public void close() throws IOException {
		m_socket.close();
		m_running = false;
	}

	@Override
	public void publish(String name, String message) throws IOException {
		send(Event.TYPE_PUBLISH, name, message);
	}

	@Override
	public void subscribe(String name, final SubscribeCallback callback)
			throws IOException {
		send(Event.TYPE_SUBSCRIBE, name, null);
		
		synchronized (m_subscriptions) {
			m_subscriptions.put(name, callback);
		}
	}

	@Override
	public void run() {
		m_running = true;
		while (m_running) {
			try {
				String data = null;
				while ((data = Sockets.blockingReceivePacked(m_socket)) != null) {
					Event event = Event.parse(data);
					
					synchronized (m_subscriptions) {
						if (m_subscriptions.containsKey(event.getName())) {
							m_subscriptions.get(event.getName()).onMessage(event.getName(), event.getMessage());
						}
					}
				}
			} catch (Exception e) {
				m_running = false;
			}
		}
	} 

	@Override
	public void unSubscribe(String name) throws IOException {
		synchronized (m_subscriptions) {
			m_subscriptions.remove(name);
		}
		send(Event.TYPE_UNSUBSCRIBE, name, null);
	}
	
	@Override
	public void requestShutdown() throws IOException {
		if (m_socket != null) {
			send(Event.TYPE_SHUTDOWN, null, null);
		}
	}
	
	private void send(String type, String name, String msg) throws IOException {
		if (m_socket != null) {
			Event ev = new Event(type, name, msg);
			Sockets.blockingSendPacked(m_socket, ev.getRaw());
		}
	}
	
}
