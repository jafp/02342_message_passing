package jafp.pubsub;

import java.io.IOException;

public interface Channel {
	public void open(String host) throws IOException;
	public void close() throws IOException;
	public void publish(String name, String message) throws IOException;
	public void subscribe(String name, SubscribeCallback callback) throws IOException;
	public void unSubscribe(String name) throws IOException;
	public void requestShutdown() throws IOException;
	public void blockingSubscribe(String name, SubscribeCallback callback)
			throws IOException;
}
