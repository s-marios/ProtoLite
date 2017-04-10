package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 * Set up the properties for an emergency button device
 * 
 * @author ymakino
 */
public class EmergencyButtonInfo extends DeviceSuperClassInfo {
    
    @Override
    protected final void addProperties(){
        add((byte) 0xB1, true, false, true, 1);
        add((byte) 0xBF, true, true, false, 3);
        add((byte) 0xFF, true, true, false , 1);
        add((byte) 0xA1, true, true, false , 4);
    }

    @Override
    public final void setObjectClass() {
        setClassEOJ(new EOJ((byte)0x00, (byte)(0x03), (byte)0x00));
    }
}