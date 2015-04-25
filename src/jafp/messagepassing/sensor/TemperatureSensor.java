package jafp.messagepassing.sensor;

import jafp.messagepassing.Message;
import jafp.messagepassing.MessageTransport;
import jafp.pubsub.Channel;
import jafp.pubsub.SocketChannel;

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
	private Channel m_channel;
	
	public TemperatureSensor(MessageTransport transport) {
		m_transport = transport;
		m_channel = new SocketChannel();
	}

	@Override
	public void run() {
		try {
			m_channel.open("localhost");
			for (int i = 0; i < 100; i++) {
				// Generate a random temperature in the range 14-24
				int temperature = 14 + ((int) (Math.random() * 10) + 1);
				
				// Publish a "sensor" event on the channel
				m_channel.publish("sensor", "temp:" + temperature);
				
				/*
				Message message = new Message(ADDRESS, VALUE_ENDPOINT, temperature);
				try {
					m_transport.send(message);
				} catch (Exception e) {
					System.err.println("Unable to send message: " + e.getMessage());
				}
				*/
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			m_transport.close();
			m_channel.close();
		}
		catch (Exception e) {}
	}

	@Override
	public void connect() throws Exception {
		m_transport.connect();
	}
}
