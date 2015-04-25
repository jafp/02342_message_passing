package jafp.messagepassing.sensor;

import jafp.messagepassing.server.Server.SensorValue;
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
				int temperature = 14 + ((int) (Math.random() * 10) + 1);
				
				SensorValue value = new SensorValue("temp", temperature);
				m_channel.publish("sensor", value.getPacket());
			
				try {
					Thread.sleep(500);
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
