package jafp.messagepassing.sensor;

import jafp.pubsub.Channel;

/**
 * Temperature sensor implementation.
 */
public class TemperatureSensor implements Sensor {

	private Channel m_channel;
	
	public TemperatureSensor(Channel channel) {
		m_channel = channel;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; i++) {
				// Generate a random temperature in the range 14-24
				int temperature = 14 + ((int) (Math.random() * 10) + 1);
				
				// Publish a "sensor" event on the channel
				m_channel.publish("sensor", "temp:" + temperature);
			
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) { }
			}
			
			System.out.println("[Temperature sensor] Closing connection, and requesting shutdown");
			
			m_channel.requestShutdown();
			m_channel.close();
		}
		catch (Exception e) {}
		System.out.println("[Temperature sensor] Done.");
	}

	@Override
	public void connect() throws Exception {
		m_channel.open("localhost");
	}
}
