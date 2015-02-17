package jafp.messagepassing;

import java.io.IOException;
import java.net.Socket;

/**
 * Message transport implemented with sockets.
 */
public class SocketMessageTransport implements MessageTransport {

	private Socket m_socket;
	private String m_host;
	private int m_port;
	
	public SocketMessageTransport(String host, int port) {
		m_host = host;
		m_port = port;
	}
	
	@Override
	public void connect() throws Exception {
		m_socket = new Socket(m_host, m_port);
	}

	@Override
	public void send(Message message) throws Exception {
		m_socket.getOutputStream().write(message.serialize());
	}

	@Override
	public void close() {
		try {
			m_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
