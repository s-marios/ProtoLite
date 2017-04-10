package jaist.echonet;

/**
 *
 * This class represents information regarding the ECHONET object group class,
 * the ECHONET object class, and the instance code of an object. Wherever there
 * is a byte array, its length is assumed to be 3, with the first byte
 * representing the group class, the second byte the object class and the third
 * byte the instance code.
 * 
 * @author Sioutis Marios, ymakino
 */
public class EOJ {
    private byte classGroupCode;
    private byte classCode;
    private byte instanceCode;
    
    private static EOJ nodeEOJ = new EOJ((byte)0x0E, (byte)0xF0, (byte)0x00);
    
    /** 
     * 
     * Public contrstuctor
     * 
     * @param eoj a byte array of length 3. first byte is group code, second is 
     * class code, third is instance code
     */
    public EOJ(byte [] eoj) {
        this.classGroupCode = eoj[0];
        this.classCode = eoj[1];
        this.instanceCode = eoj[2];
    }
    
    /**
     * Public constructor, pass information as three bytes
     * 
     * @param classGroupCode the class group code
     * @param classCode the class code
     * @param instanceCode the desired instance code
     */
    public EOJ(byte classGroupCode, byte classCode, byte instanceCode) {
        this.classGroupCode = classGroupCode;
        this.classCode = classCode;
        this.instanceCode = instanceCode;
    }
    
    /**
     * Constructor from string of length six with integers
     * 
     * @param eoj
     */
    public EOJ(String eoj) {
        if (eoj.length() == 8){
            if (eoj.indexOf('x') == 1 || eoj.indexOf('X') == 1){
                eoj = eoj.substring(2);
            }
        }
        
        
        if (eoj.length() != 6) {
            throw new IllegalArgumentException("Invalid EOJ: " + eoj);
        }
        this.classGroupCode = (byte)Integer.parseInt(eoj.substring(0, 2), 16);
        this.classCode = (byte)Integer.parseInt(eoj.substring(2, 4), 16);
        this.instanceCode = (byte)Integer.parseInt(eoj.substring(4, 6), 16);
    }
    
    /**
     * Returns the class group code. 
     * 
     * @return The class group code this EOJ represents
     */
    public byte getClassGroupCode() {
        return this.classGroupCode;
        
    }
    
    /**
     * Returns the class code.
     * 
     * @return The class code this EOJ represents
     */
    public byte getClassCode() {
        return this.classCode;
    }
    
    /**
     * Returns the instance code. 
     * 
     * @return The instance code
     */
    public byte getInstanceCode() {
        return this.instanceCode;
    }
    
    /**
     * Gets an array of bytes that represents the information of this EOJ
     * 
     * @return A three byte array with the information this EOJ represents
     */
    public byte[] getBytes() {
        byte[] bytes = new byte[3];
        bytes[0] = classGroupCode;
        bytes[1] = classCode;
        bytes[2] = instanceCode;
        return bytes;
    }
    
    /**
     * Internal use
     * 
     * @return Generates a new eoj that has the same group code, class code, but
     * with instance code of zero
     */
    public EOJ getClassEOJ() {
        return new EOJ(classGroupCode, classCode, (byte)0x00);
    }
    
    /**
     * Check to see if this EOJ represents a Node class
     * 
     * @return true if it is a Node class, false otherwise
     */
    public boolean isNodeClass() {
        return this.getClassEOJ().equals(nodeEOJ.getClassEOJ());
    }
    
    /**
     * Internal use
     * 
     * @param newInstanceCode the instance code
     * @return A new EOJ with the same class group code, class code and the
     * given instance code
     */
    public EOJ getEOJWithInstanceCode(byte newInstanceCode) {
        return new EOJ(classGroupCode, classCode, newInstanceCode);
    }
   /* 
    @Deprecated
    public short shortValue() {
        //TODO this is borked. don't use
        return (short)intValue();
    }*/
    
    /**
     * 
     * Get a short that can be used as key in map collections. It represents
     * class group and class code information only (no instance information)
     * 
     * @return A short that represents class group and class code information
     */
    public short shortValueForClass(){
        return (short) ((intValue() >> 8 ) & 0x0000ffff);
    }
    
    /**
     * 
     * Get an int that can be used as key in map collections. It represents 
     * class group, class code and instance number information.
     * 
     * @return An in that represents class group, class code and instance
     * information
     */
    public int intValue() {
        return ((0xff & (int)classGroupCode) << 16)
                | ((0xff & (int)classCode) << 8)
                | (0xff & (int)instanceCode);
    }
    
    /**
     * 
     * Get value of this object as a hex string
     * 
     * @return A string that represents the value of this EOJ in hex
     */
    @Override
    public String toString() {
        return String.format("%06x", intValue());
    }
    
    /**
     * Check for equality 
     * 
     * @param otherObj
     * @return true if this EOJ has the same class group code, class code and
     * instance number with otherObj, false otherwise
     */
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof EOJ) {
            EOJ other = (EOJ)otherObj;
            return     this.getClassGroupCode() == other.getClassGroupCode()
                    && this.getClassCode() == other.getClassCode()
                    && this.getInstanceCode() == other.getInstanceCode();
        }
        return false;
    }
    
    /**
     * 
     * @return hash code. Same as intValue
     */
    @Override
    public int hashCode() {
        return intValue();
    }
}
