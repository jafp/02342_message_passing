package jafp.messagepassing.sensor;

import jafp.messagepassing.Message;
import jafp.messagepassing.MessageTransport;

/**
 * Temperature sensor implementation.
 */
public class TemperatureSensor implements Sensor {

	/**
	 * Address of this sensor
	 */
	public static int ADDRESS = 0x01;
	
	/**
	 * Endpoint of the Celcius measurement
	 */
	public static int VALUE_ENDPOINT = 0x01;
	
	private MessageTransport m_transport;
	
	public TemperatureSensor(MessageTransport transport) {
		m_transport = transport;
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			// Generate a random temperature in the range 14-24
			int temperature = 14 + ((int) (Math.random() * 10) + 1);
			Message message = new Message(ADDRESS, VALUE_ENDPOINT, temperature);
			try {
				m_transport.send(message);
			} catch (Exception e) {
				System.err.println("Unable to send message: " + e.getMessage());
			}
		}
		m_transport.close();
	}

	@Override
	public void connect() throws Exception {
		m_transport.connect();
	}
}
