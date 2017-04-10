/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet;

/**
 * Various constants regarding the ECHONET Lite protocol
 * 
 * @author Sioutis Marios
 */
public class EchonetProtocol {

    /**
     * ECHONET Lite UDP port
     */
    public static final int ECHONETPORT = 3610;
    /**
     * IPv4 multicast group
     */
    public static final String ECHONETMULTICASTV4 = "224.0.23.0";
    /**
     * IPv6 multicast group
     */
    public static final String ECHONETMULTICASTV6 = "ff02::1";
    /**
     * Timeout for unicast queries
     */
    public static final long AWAITRESPONSE = 5000; //5secs
    /**
     * Timeout for multicast queries
     */
    public static final long AWAITRESPONSEMULTICAST = 10000; //10secs;
    /**
     * Node profile eoj
     */
    public static EOJ NODEPROFILEOJ = new EOJ((byte) 0x0E, (byte) 0xF0, (byte) 0x01);
    /**
     * Version of ECHONET Lite
     */
    public static final byte[] ECHONETVERSION = {4, 0, 'a', 0};

    /**
     * This is now part of the {@link EOJ} class
     * The lower 3 bytes contain the eoj
     * @param eoj 
     * @return an int to be used in maps
     * @deprecated 
     */
    @Deprecated
    public static int getEOJAsInt(byte[] eoj) {
        if (eoj == null) {
            return -1;
        }

        int eojasint = 0;
        for (int i = 0; i < 3; i++) {
            //hack: the first time, the shift does nothing, that's what we want
            eojasint <<= 8;
            eojasint |= eoj[i];
        }
        return eojasint;
    }

    /**
     * This is now part of the {@link EOJ} class
     * 
     * the lower 2 bytes contain only the class group and class code
     * @param eoj 
     * @return an int to be used in maps 
     * @deprecated 
     */
    @Deprecated
    public static int getEOJClassAsInt(byte[] eoj) {
        if (eoj == null) {
            return -1;
        }

        int eojasint = 0;
        for (int i = 0; i < 2; i++) {
            //hack: the first time, the shift does nothing, that's what we want
            eojasint <<= 8;
            eojasint |= eoj[i];
        }
        return eojasint;
    }
}
