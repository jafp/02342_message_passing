package jafp.messagepassing;

/**
 * Message sent from sensors to the central server.
 */
public class Message {
	
	/**
	 * Fixed message size in bytes
	 * (1 byte device address, 1 byte endpoint address,
	 *  and four byte integer value / payload)
	 */
	public static int MESSAGE_SIZE = 6; 
	
	private int m_device;
	private int m_endpoint;
	private int m_value;
	
	public Message(int device, int endpoint, int value) {
		m_device = device;
		m_endpoint = endpoint;
		m_value = value;
	}
	
	/**
	 * Construct message from array of bytes (e.g. received through socket).
	 * The values are expected to be encoded in Network Byte Order.
	 * 
	 * @param buffer Array of bytes (size `MESSAGE_SIZE`)
	 * @return Parsed message
	 */
	public static Message deserialize(byte[] buffer) {
		int address = buffer[0];
		int endpoint = buffer[1];
		int value = buffer[2] << 24 | buffer[3] << 16 | buffer[4] << 8 | buffer[5]; 
		return new Message(address, endpoint, value);
	}
	
	/**
	 * Serialize the message as a byte array. All values
	 * are stored with MSB first, Network Byte Order.
	 * 
	 * @return The message serialized as an array of bytes 
	 */
	public byte[] serialize() {
		byte[] buf = new byte[MESSAGE_SIZE];
		
		// Device and endpoint
		buf[0] = (byte) (m_device & 0xFF);
		buf[1] = (byte) (m_endpoint & 0xFF);
		
		// Split value into bytes in network byte order
		buf[2] = (byte) ((m_value & 0xFF000000) >> 24);
		buf[3] = (byte) ((m_value & 0x00FF0000) >> 16);
		buf[4] = (byte) ((m_value & 0x0000FF00) >> 8);
		buf[5] = (byte) ((m_value & 0x000000FF));
		return buf;
	}
	
	public int getDevice() {
		return m_device;
	}
	
	public int getEndpoint() {
		return m_endpoint;
	}
	
	public int getValue() {
		return m_value;
	}
	
	@Override
	public String toString() {
		return "[" + m_device + ":" + m_endpoint + "] " + m_value;
	}
}
