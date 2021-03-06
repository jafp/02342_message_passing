package jafp.pubsub;

import java.io.IOException;
import java.net.UnknownHostException;

public class SubscribeChannelTest {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		final Channel ch = new SocketChannel();
		ch.open("localhost");
		
		System.out.println("Subscribe to [foo]");
		
		SubscribeCallback cb = new SubscribeCallback() {
			@Override
			public void onMessage(String name, String message) {
				System.out.println("Received [" + name + "]: " + message);
			}
		};
		
		ch.subscribe("foo", cb);
		ch.subscribe("bar", cb);
		
		ch.publish("random", "Hello, world!");
		
		Thread.sleep(5000);
		
		System.out.println("Requesting server shutdown");
		ch.requestShutdown();
		
		ch.close();
		System.out.println("Done.");
	}
	
}
