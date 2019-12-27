package jaist.echonet;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of the ECHONET Lite services. Defines static codes as well as
 * utility functions to convert codes to names and vise versa.
 *
 * @author Sioutis Marios
 */
public enum ServiceCode {
    /**
     * SET with no response
     */
    SetI((byte) 0x60),
    /**
     * Set with response
     */
    SetC((byte) 0x61),
    /**
     * Simple Get
     */
    Get((byte) 0x62),
    /**
     * Request notification
     */
    INF_REQ((byte) 0x63),
    /**
     * SetGET
     */
    SetGet((byte) 0x6e),
    /**
     * Response to a Set* request (no errors)
     */
    Set_Res((byte) 0x71),
    /**
     * Response to a Get request (no errors)
     */
    Get_Res((byte) 0x72),
    /**
     * Notification
     */
    INF((byte) 0x73),
    /**
     * INFC
     */
    INFC((byte) 0x74),
    /**
     * Response to INFC
     */
    INFC_Res((byte) 0x7a),
    /**
     * Response to SetGet (no error)
     */
    SetGet_Res((byte) 0x7e),
    /**
     * Response to SetI (error)
     */
    SetI_SNA((byte) 0x50),
    /**
     * Response to SetC (error)
     */
    SetC_SNA((byte) 0x51),
    /**
     * Response to Get (error)
     */
    Get_SNA((byte) 0x52),
    /**
     * Response to INFC (error)
     */
    INF_SNA((byte) 0x53),
    /**
     * Response to SetGet (error)
     */
    SetGetI_SNA((byte) 0x5e);
    private final byte opcode;
    private static final Map<Byte, ServiceCode> lookup = new HashMap();

    static {
        for (ServiceCode opc : ServiceCode.values()) {
            lookup.put(opc.getOpcode(), opc);
        }
    }

    ServiceCode(byte code) {
        this.opcode = code;
    }

    /**
     * Gets the service code (as a byte) of this service
     *
     * @return the service code
     */
    public byte getOpcode() {
        return this.opcode;
    }

    /**
     * Gets the Enum representation of a service code
     *
     * @param opcode the service code as an int
     * @return the Enum name of the corresponding service may return null
     */
    public static ServiceCode getOpcode(int opcode) {
        return lookup.get((byte) opcode);
    }

    /**
     * Checks if the supplied service code corresponds to a valid service
     *
     * @param code the service code as an int
     * @return true if it is a valid service, false otherwise
     */
    public static boolean isIncluded(int code) {
        return (lookup.get((byte) code) != null);
    }
}
