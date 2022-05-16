package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 *
 * @author Sioutis Marios
 */
public class VirtualAirconditionInfo extends DeviceSuperClassInfo{

    @Override
    protected void setObjectClass() {
        setClassEOJ(new EOJ((byte)0x01, (byte)0x30, (byte)0x00));
    }

    @Override
    protected void addProperties() {
        add((byte) 0xB0, true, true, true, 1);
        add((byte) 0xB1, true, true, false, 1);
        add((byte) 0xB3, true, true, false, 1);
        add((byte) 0xB5, true, true, false, 1);
        add((byte) 0xB6, true, true, false, 1);
        add((byte) 0xB7, true, true, false, 1);
        
        add((byte) 0xA0, true, true, false, 1);
        add((byte) 0xA1, true, true, false, 1);
        add((byte) 0xA4, true, true, false, 1);
        
        add((byte) 0x90, true, true, false, 1);
        add((byte) 0x92, true, true, false, 2);
        add((byte) 0x94, true, true, false, 1);
        add((byte) 0x96, true, true, false, 2);
    
    }
    
}
