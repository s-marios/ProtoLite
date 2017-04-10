package jaist.echonet.wrappers;

import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;

/**
 * Wrapper class for an emergency button
 * 
 * @author Sioutis Marios
 */
public class EmergencyButton extends SensorObject {
    
    public static EmergencyButton getLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(EmergencyButton.class, obj);
    }
    
    public static EmergencyButton getRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(EmergencyButton.class, obj, whoasks);
    }

    static EchonetCharacterProperty pushme = new EchonetCharacterProperty(
            (byte) 0xBF,
            false,
            false,
            new byte[]{(byte) 0x00}
            );
    
    /*
     * Pushes the emergency button
     */
    public boolean push(){
        return writeProperty(pushme);
    }
}
