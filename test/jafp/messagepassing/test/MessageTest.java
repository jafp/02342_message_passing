package jafp.messagepassing.test;

import jafp.messagepassing.Message;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

	@Test
	public void testSerialize() {
		Message msg = new Message(0x10, 0x22, 0x11223344);
		byte[] bytes = msg.serialize();
		
		// Assert endpoint ID
		assertEquals(0x10, bytes[0]);
		
		// Assert value ID
		assertEquals(0x22, bytes[1]);
		
		// Assert value (MSB / Network byte order)
		assertEquals(0x11, bytes[2]);
		assertEquals(0x22, bytes[3]);
		assertEquals(0x33, bytes[4]);
		assertEquals(0x44, bytes[5]);
	}

	@Test
	public void testDeserialize() {
		byte[] bytes = { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77 };
		Message msg = Message.deserialize(bytes);
		
		assertEquals(0x11, msg.getDevice());
		assertEquals(0x22, msg.getEndpoint());
		assertEquals(0x33445566, msg.getValue());
	}
	
	@Test
	public void testToString() {
		Message msg = new Message(10, 20, 123123);
		assertEquals("[10:20] 123123", msg.toString());
	}
}
