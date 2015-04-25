package jafp.messagepassing;

/**
 * 
 * Defines a layer for which sensors (or other devices on the network) are able to
 * broadcast messages to servers, control boxes, etc.
 *
 */
public interface MessageTransport {
	/**
	 * Connect to the destination, i.e. a server
	 * 
	 * @throws Exception If the transport are unable to connect to its destination point
	 */
	public void connect() throws Exception;
	
	/**
	 * Send a message to the server.
	 * 
	 * @throws Exception If the transport are unable to send the message
	 */
	public void send(Message message) throws Exception;

	/**
	 * Close connection.
	 */
	public void close();
}
