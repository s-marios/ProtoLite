package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 *
 * @author marios
 */
public class ControllerInfo extends DeviceInfo{

    @Override
    protected void setObjectClass() {
        setClassEOJ(new EOJ((byte) 0x0e, (byte)0xff, (byte)0));
    }

    @Override
    protected void addProperties() {
        add((byte) 0xff, true, false, false, 100, 0, "controller object".getBytes());
    }
    
}
