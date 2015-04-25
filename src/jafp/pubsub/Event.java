package jafp.pubsub;

public class Event {
	
	public static String SEPARATOR = ":";
	public static String TYPE_MESSAGE = "message";
	public static String TYPE_PUBLISH = "publish";
	public static String TYPE_SUBSCRIBE = "subscribe";
	
	private String m_type;
	private String m_name;
	private String m_message;
	
	public Event(String type, String name, String message) {
		m_type = type;
		m_name = name;
		m_message = message;
	}
	
	public String getType() {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}
	
	public String getMessage() {
		return m_message;
	}
	
	public boolean isSubscribe() {
		return TYPE_SUBSCRIBE.equalsIgnoreCase(getType());
	}
	
	public boolean isPublish() {
		return TYPE_PUBLISH.equalsIgnoreCase(getType());
	}
	
	public String getRaw() {
		String raw = m_type + SEPARATOR + m_name;
		if (m_message != null) {
			raw += SEPARATOR + m_message;
		}
		return raw;
	}
	
	public static Event parse(String data) {
		String[] parts = data.split(SEPARATOR);
		
		if (parts.length >= 2) {
			String type = parts[0];
			String name = parts[1];
			String msg = null;
			
			if (parts.length > 2) {
				int offset = (type + SEPARATOR + name + SEPARATOR).length();
				msg = data.substring(offset);
			}
			return new Event(type, name, msg);
		}
		return null;
	}
}
