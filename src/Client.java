import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {

	public final static int PORT = 23;
	public final static String DEFAULT_FILENAME = "rajat.txt";

	public static DatagramSocket socket = null;
	public static DatagramPacket packet = null;
	public static byte[] data = new byte[128];

	public static void main(String[] args) throws Exception {

		Client c = new Client();
		c.connect();
		c.readRequest(Packet.Mode.OCTECT, DEFAULT_FILENAME);
		c.writeRequest(Packet.Mode.NETASCII, DEFAULT_FILENAME);
		c.readRequest(Packet.Mode.OCTECT, DEFAULT_FILENAME);
		c.writeRequest(Packet.Mode.NETASCII, DEFAULT_FILENAME);
		c.readRequest(Packet.Mode.OCTECT, DEFAULT_FILENAME);
		c.writeRequest(Packet.Mode.NETASCII, DEFAULT_FILENAME);
		c.readRequest(Packet.Mode.OCTECT, DEFAULT_FILENAME);
		c.writeRequest(Packet.Mode.NETASCII, DEFAULT_FILENAME);
		c.readRequest(Packet.Mode.OCTECT, DEFAULT_FILENAME);
		c.writeRequest(Packet.Mode.NETASCII, DEFAULT_FILENAME);
		c.invalidRequest(Packet.Mode.INVALID, DEFAULT_FILENAME);

	}

	public Client() throws Exception {

	}

	public void connect() throws CommunicationException {

		try {
			// open socket port on assigned port
			socket = new DatagramSocket();
		} catch (SocketException e) {
			throw new CommunicationException("Unable to establish connection");
		}
	}

	public void readRequest(Packet.Mode mode, String filename)
			throws UnsupportedEncodingException, IOException, CommunicationException {

		sendRequest(new Packet(Packet.Request.READ, mode, filename));
	}

	public void writeRequest(Packet.Mode mode, String filename)
			throws UnsupportedEncodingException, IOException, CommunicationException {

		sendRequest(new Packet(Packet.Request.WRITE, mode, filename));
	}

	public void invalidRequest(Packet.Mode mode, String filename)
			throws UnsupportedEncodingException, IOException, CommunicationException {

		sendRequest(new Packet(Packet.Request.INVALID, mode, filename));
	}

	public void sendRequest(Packet req) throws CommunicationException {

		byte[] data = req.generatePacketData();
		System.out.println("\nClient Sending (Byte): " + Utils.bytesToHex(data, data.length));
		System.out.println("Client Sending (String): " + req.toString());

		try {
			// send request to server
			packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), PORT);
			socket.send(packet);
		} catch (IOException e) {
			throw new CommunicationException("Unable to send packet");
		}

		receiveResponse();
	}

	public void receiveResponse() throws CommunicationException {

		// get response from server
		packet = new DatagramPacket(data, 128);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			throw new CommunicationException("Unable to recieve packet");
		}

		// print response data
		data = packet.getData();
		System.out.println("Client received (bytes): " + Utils.bytesToHex(data, 4));
		System.out.print("Client received (string): ");
		for (int i = 0; i < 4; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
	}

	public void finalize() {

		socket.close();
	}
}
