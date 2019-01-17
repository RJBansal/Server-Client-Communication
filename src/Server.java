import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

	public final static int PORT = 69;
	public final static byte[] READ_RESPONSE = { 0, 3, 0, 1 };
	public final static byte[] WRITE_RESPONSE = { 0, 4, 0, 0 };

	public static DatagramSocket receiveSocket = null;
	public static DatagramSocket sendSocket = null;
	public static DatagramPacket packet = null;
	public static byte[] data = new byte[128];
	public static InetAddress clientAddress = null;

	public static void main(String[] args) throws Exception {

		// start server and listen to incoming packages
		Server server = new Server();
		server.connect();

		while (true) {
			server.listen();
		}
	}

	public Server() throws Exception {

		clientAddress = InetAddress.getLocalHost();
	}

	public void connect() throws CommunicationException {

		try {
			// listen on ClientPort for incoming packets
			System.out.println("Server connecting on: " + clientAddress.getHostAddress() + ":" + PORT + "\n");
			receiveSocket = new DatagramSocket(PORT, clientAddress);
		} catch (SocketException e) {
			throw new CommunicationException("Unable to establish connection");
		}
	}

	public void listen() throws CommunicationException {

		// wait for request from client
		data = new byte[128];
		packet = new DatagramPacket(data, data.length);

		try {
			receiveSocket.receive(packet);
		} catch (IOException e) {
			throw new CommunicationException("Unable to recieve packet");
		}

		data = packet.getData();
		System.out.println("Server received (bytes): " + Utils.bytesToHex(data, packet.getLength()));
		System.out.println("Server received (string): " + new Packet(data, packet.getLength()).toString());

		respond(packet);
	}

	public void respond(DatagramPacket packet) throws CommunicationException {

		Packet request = new Packet(packet.getData(), packet.getLength());
		// check validity of packet
		if (!request.isValid()) {
			throw new CommunicationException("Invalid request received");
		}

		try {
			// format correct response
			if (request.getRequest() == Packet.Request.READ) {
				data = READ_RESPONSE;
			} else if (request.getRequest() == Packet.Request.WRITE) {
				data = WRITE_RESPONSE;
			} else {
				throw new CommunicationException("Invalid request received");
			}

			System.out.println("Server responding to " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
			System.out.println("Response bytes: " + Utils.bytesToHex(packet.getData(), packet.getLength()));
			System.out.print("Response string: ");
			for (int i = 0; i < data.length; i++) {
				System.out.print(data[i] + " ");
			}
			System.out.println("\n");

			// send response back to client
			sendSocket = new DatagramSocket();
			packet = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
			sendSocket.send(packet);
			sendSocket.close();

		} catch (IOException e) {
			throw new CommunicationException("Unable to respond to request");
		}

	}
}
