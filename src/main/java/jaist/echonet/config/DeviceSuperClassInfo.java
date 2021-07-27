package jaist.echonet.config;

import jaist.echonet.EchonetProtocol;

/**
 * Set up the compulsory fields for any device class object
 * 
 * @author Sioutis Marios
 */
public abstract class DeviceSuperClassInfo extends DeviceInfo {

    public DeviceSuperClassInfo(){
        add((byte)0x81, true, true, true, 1, new byte[]{(byte) 0});
        add((byte)0x82, true, false, false, 4, EchonetProtocol.ECHONETVERSION);
        
    }
}
