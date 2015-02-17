package jafp.messagepassing.sensor;

/**
 * Common interface for all sensors.
 */
public interface Sensor extends Runnable {
	public void connect() throws Exception;
}
