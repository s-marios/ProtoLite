package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 * Sets up the required properties for a node profile object
 * @author ymakino, Sioutis Marios
 */
public class NodeProfileInfo extends DeviceInfo {
    
    @Override
    protected final void addProperties() {
        //the following two properties are init'd
        //in DeviceInfo
        //add((byte)0x88, true, false, true, 1);
        //add((byte)0x8A, true, false, false, 3);
        add((byte)0x8B, true, false, false, 3);
        add((byte)0x8C, true, false, false, 12);
        add((byte)0x8D, true, false, false, 12);
        add((byte)0x8E, true, false, false, 4);
        //add((byte)0x9D, true, false, false, 17);
        //add((byte)0x9E, true, false, false, 17);
        //add((byte)0x9F, true, false, false, 17);
        
        
        add((byte) 0x80, true, true, false, 1, new byte[]{0x30});
        add((byte) 0x82, true, false, false, 4);
        add((byte) 0x83, true, false, false, 17);
        add((byte) 0x89, true, false, false, 2);
        add((byte) 0xBF, true, true, false, 2);
        //add((byte) 0xE0, true, true, true, 255);
        //add((byte) 0xEE, true, false, false, 1);
        //add((byte) 0xEF, true, true, false, 17);
        add((byte) 0xD3, true, false, false, 3);
        add((byte) 0xD4, true, false, false, 2);
        add((byte) 0xD5, false, false, true, 253);
        add((byte) 0xD6, true, false, false, 253);
        add((byte) 0xD7, true, false, false, 17);
    }

    @Override
    public void setObjectClass() {
        setClassEOJ(new EOJ((byte)0x0E, (byte)0xF0, (byte)0x00));
    }
}
