package jafp.pubsub;

public interface SubscribeCallback {
	public void onMessage(String name, String message);
}
