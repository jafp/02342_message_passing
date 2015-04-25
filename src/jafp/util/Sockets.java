package jafp.util;

import java.io.IOException;
import java.net.Socket;

public class Sockets {
	
	public static void blockingSendPacked(Socket sock, String data) throws IOException {
		int len = data.length();
		byte[] buf = Bytes.intToBytes(len);
		sock.getOutputStream().write(buf);
		sock.getOutputStream().write(data.getBytes());
	}
	
	public static String blockingReceivePacked(Socket sock) throws IOException {
		byte[] headerBuf = Bytes.emptyBuf();
		while (sock.getInputStream().read(headerBuf) != -1) {
			int len = Bytes.bytesToInt(headerBuf);
			byte[] bodyBuf = new byte[len];
			if (sock.getInputStream().read(bodyBuf) != -1) {
				return new String(bodyBuf);
			}
		}
		return null;
	}
}
