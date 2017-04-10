/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 *
 * @author ymakino
 */
public class TemperatureSensorInfo extends DeviceSuperClassInfo {
    
    @Override
    protected final void addProperties() {
        add((byte) 0xE0, true, false, true, 2);
    }

    @Override
    public final void setObjectClass() {
        setClassEOJ(new EOJ((byte)0x00, (byte)(0x11), (byte)0x00));    
    }
}
