import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Host {

	public final static int CLIENT_PORT = 23;
	public final static int SERVER_PORT = 69;

	public static DatagramSocket clientSocket = null;
	public static DatagramSocket socket = null;
	public static DatagramPacket packet = null;
	public static byte[] data = new byte[128];
	public static InetAddress localAddress = null;
	public static InetAddress clientAddress = null;
	public static int clientPort;

	public static void main(String[] args) throws Exception {

		// start host and listen to packets
		Host host = new Host();
		host.connect();

		while (true) {
			host.relayPacket();
		}
	}

	public Host() {

	}

	public void connect() throws CommunicationException {
		try {
			// listen on client port for packets from client
			localAddress = InetAddress.getLocalHost();
			System.out.println("Server connecting on: " + localAddress.getHostAddress() + ":" + CLIENT_PORT);
			clientSocket = new DatagramSocket(CLIENT_PORT, localAddress);
		} catch (SocketException | UnknownHostException e) {
			throw new CommunicationException("Unable to establish connection", e);
		}
	}

	public void relayPacket() throws CommunicationException {

		try {
			data = new byte[128];
			packet = new DatagramPacket(data, data.length);

			// receive packet from client
			clientSocket.receive(packet);
			data = packet.getData();
			int length = packet.getLength();
			clientAddress = packet.getAddress();
			clientPort = packet.getPort(); // must get Port of where client sends packet from

			System.out.println("Host forwarding request from: " + clientAddress.getHostAddress() + ":" + clientPort);
			System.out.println("Request bytes: " + Utils.bytesToHex(packet.getData(), packet.getLength()));
			System.out.println("Request string: " + new Packet(data, length).toString() + "\n");

			// forward packet to server
			packet = new DatagramPacket(data, length, InetAddress.getLocalHost(), SERVER_PORT);
			socket = new DatagramSocket();
			socket.send(packet);

			// receive response
			data = new byte[128];
			packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			data = packet.getData();

			System.out.println("Response bytes: " + Utils.bytesToHex(packet.getData(), packet.getLength()));
			System.out.print("Resonse string: ");
			for (int i = 0; i < packet.getLength(); i++) {
				System.out.print(data[i] + " ");
			}
			System.out.println();

			// send response back to client port
			packet = new DatagramPacket(data, length, clientAddress, clientPort);
			socket.send(packet);

		} catch (IOException e) {
			throw new CommunicationException("Unable to relay packet");
		}
	}

	public void finalize() {

		socket.close();
		clientSocket.close();
	}
}
