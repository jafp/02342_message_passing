package jafp.util;

public class Bytes {
	
	public static byte[] emptyBuf() {
		return new byte[4];
	}
	
	public static byte[] intToBytes(int x) {
		byte[] b = emptyBuf();
		b[0] = (byte) ((x & 0xFF000000) >> 24);
		b[1] = (byte) ((x & 0x00FF0000) >> 16);
		b[2] = (byte) ((x & 0x0000FF00) >> 8);
		b[3] = (byte) ((x & 0x000000FF));
		return b;
	}
	
	public static int bytesToInt(byte[] b) {
		int x = b[0] << 24 | b[1] << 16 | b[2] << 8 | b[3]; 
		return x;
	}
}
