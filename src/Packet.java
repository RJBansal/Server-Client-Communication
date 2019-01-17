import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Packet {
		
	enum Request {
	    READ,
	    WRITE,
	    INVALID;
	}
	
	enum Mode {
	    OCTECT,
	    NETASCII,
	    INVALID;
	}
	
	public final static byte[] READ = {0, 1};
	public final static byte[] WRITE = {0, 2};
	
	private String filename = "";
	private Request request; 
	private Mode mode; 
	private boolean isValid = true;
	
	public Packet (Request request, Mode mode, String filename) {
		
		this.request = request;
		this.mode = mode; 
		this.filename = filename; 
	}

	public Packet (byte[] data, int dataLength) throws CommunicationException {

		isValid = true;
		
		//extract read or write request
		if (data[0] == READ[0] && data[1] == READ[1]) {
			request = Request.READ;
		} else if (data[0] == WRITE[0] && data[1] == WRITE[1]) {
			request = Request.WRITE;
		} else {
			isValid = false;
		}
		
		//extract filename
		int i = 1;
		while (data[++i] != 0 && i < dataLength) {
			filename += (char)data[i];
		}
		
		//must be zero
		if (data[i] != 0) {
			isValid = false;
		}
		
		//extract mode (octet or netascii)
		String modeStr = "";
		while (data[++i] != 0 && i < dataLength) {
			modeStr += (char)data[i];
		}
		
		modeStr = modeStr.toLowerCase();
		if ( modeStr.equals("netascii") ) {
			mode = Mode.NETASCII; 
		} else if ( modeStr.equals("octect") ) {
			mode = Mode.OCTECT; 
		} else {
			isValid = false;
		}
		
		//must be zero at end
		while (i < dataLength) {
			if ( data[i++] != 0 ) {
				isValid = false;
				break;
			}
		}
	}
	
	public byte[] generatePacketData() throws CommunicationException {
		
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
						
			//write request bytes
			switch (request) {
			case READ:
				stream.write(READ); // read request flag byte
				break;
			case WRITE:
				stream.write(WRITE); // write request flag byte
				break;
			case INVALID:
				stream.write(0); // undefined request 
				stream.write(0);
				break;
			default:
				throw new CommunicationException("Unable to generate packet");
			}
			
			//write filename bytes
			if ( null != filename ) {
				stream.write(filename.getBytes());
			}
			
			//add 0
			stream.write(0);
			
			//write mode bytes
			stream.write(mode.toString().getBytes());
			
			//add 0
			stream.write(0);
			
			return stream.toByteArray();
		} catch (IOException | NullPointerException e) {
			throw new CommunicationException("Unable to generate packet", e);
		}
	}
	
	public boolean isValid() {
		
		if (!isValid) {	
			return false;
		}
		if (filename == null || filename.isEmpty()) {	
			return false;
		} 		
		if (mode == null || mode == Mode.INVALID) {	
			return false;
		} 
		if (request == null || request == Request.INVALID) {	
			return false;
		}
		
		return true;
	}
	
	public Request getRequest() {
		
		return request;
	}
	
	public Mode getMode() {
		
		return mode;
	}
	
	public  String toString() {
		
		StringBuilder string = new StringBuilder();
		string.append(mode);
		string.append(filename);
		string.append((byte) 0);
		string.append(mode);
		string.append((byte) 0);
		
		return string.toString();
	}
}
