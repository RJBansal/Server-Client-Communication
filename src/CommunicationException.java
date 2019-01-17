
public class CommunicationException extends Exception {

	private static final long serialVersionUID = 4569467572726215310L;

	public CommunicationException(String s) {

		super(s);
	}

	public CommunicationException(Throwable cause) {

		super(cause.getLocalizedMessage(), cause);
	}

	public CommunicationException(String message, Throwable cause) {

		super(cause.getLocalizedMessage() + ": " + message, cause);
	}
}
