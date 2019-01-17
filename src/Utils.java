
public class Utils {

	public final static String CHARSET = "UTF-8";

	public static CharSequence bytesToHex(byte[] in, int length) {

		return bytesToHex(in, length, "0x", " ");
	}

	public static CharSequence bytesToHex(byte[] in, int length, CharSequence pre, CharSequence post) {

		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;

		for (int i = 0; i < length; i ++) {
			byte b = in[i];
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
