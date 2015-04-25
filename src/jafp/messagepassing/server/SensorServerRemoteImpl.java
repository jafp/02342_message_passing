package jafp.messagepassing.server;

import java.rmi.RemoteException;

public class SensorServerRemoteImpl implements SensorServerRemote {

	private double m_average;
	private long m_samples;
	
	public void setAverageTemp(double temp) {
		m_average = temp;
	}
	
	public void setNumberOfSamples(long samples) {
		m_samples = samples;
	}
	
	@Override
	public double getAverageTemperature() throws RemoteException {
		return m_average;
	}

	@Override
	public long getNumberOfSamples() throws RemoteException {
		return m_samples;
	}

}
