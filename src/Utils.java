
public class Utils {

	public final static String CHARSET = "UTF-8";

	public static CharSequence bytesToHex(byte[] in) {

		return bytesToHex(in, "0x", " ");
	}

	public static CharSequence bytesToHex(byte[] in, CharSequence pre, CharSequence post) {

		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;

		for (byte b : in) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(post);
			}
			sb.append(pre);
			sb.append(String.format("%02x", b));
		}
		return sb;
	}

}
