package jaist.echonet.util;

/**
 * Utilities class, mostly sting to hex, hex to string conversions. Uses the 
 * singleton pattern
 *
 * @author Sioutis Marios
 */
public class Utils {

    static final byte convertbase = 65;
    private static final Utils util = new Utils();

    /**
     * Convert a byte array to a hex string
     * 
     * @param rawdata the data to be converted
     * @return a hex string
     */
    public static String toHexString(byte[] rawdata) {
        return util.privateToHexString(rawdata);
    }

    /**
     * Convert a single byte to its hex representation
     * @param code the byte to convert
     * @return a hex string
     */
    public static String toHexString(byte code) {
        return util.privateToHexString(new byte[]{code});
    }

    private Utils() {
    }

    private String privateToHexString(byte[] rawdata) {
        String result = "0x";
        if (rawdata == null || rawdata.length == 0) {
            return result += "00";
        }
        for (byte b : rawdata) {
            String high = privateCharToHex((byte) ((b & 0x000000f0) >> 4));
            String low = privateCharToHex((byte) (b & 0x0000000f));
            result += high + low;
        }

        return result;
    }

    private String privateCharToHex(byte letter) {
        if (letter > 0x0f || letter < 0) {
            return null;
        }
        if (letter < 10) {
            return new String(new char[]{(char) (letter + 48)});
        } else {
            return new String(new char[]{(char) (letter + convertbase - 10)});
        }
    }

    /**
     * Check for node class
     * @param eoj the eoj to check
     * @return true if the supplied eoj represents a node profile class, 
     * false otherwise
     */
    public static boolean isNode(byte[] eoj) {
        if (eoj != null && eoj.length == 3) {
            if (eoj[0] == (byte) 0x0E && eoj[1] == (byte) 0xF0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a string that represents hexadecimal data to a byte array that 
     * contains that data
     * @param s the string that holds the data in hex 
     * @return a byte array with the data in byte form
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len == 0) {
            return null;
        }
        int offset = 0;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            len -= 2;
            offset = 2;
        }
        if (len % 2 == 1) {
            return null;
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int higher = Character.digit(s.charAt(i + offset), 16);
            int lower = Character.digit(s.charAt(i + 1 + offset), 16);
            if (higher == -1 || lower == -1) {
                return null;
            }
            higher <<= 4;
            data[i / 2] = (byte) (higher + lower);
        }
        return data;
    }
}
