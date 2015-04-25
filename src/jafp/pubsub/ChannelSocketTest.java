package jafp.pubsub;

import java.io.IOException;
import java.net.UnknownHostException;

public class ChannelSocketTest {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		Channel ch = new SocketChannel();
		ch.open("localhost");
		ch.publish("foo", "hello foo world 222");
		ch.close();
	}
}
