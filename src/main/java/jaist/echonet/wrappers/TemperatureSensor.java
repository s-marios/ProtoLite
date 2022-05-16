package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;

/**
 * Wrapper class for a temperature sensor. Names of the methods are self-explanatory
 * 
 * @author Sioutis Marios
 */
public class TemperatureSensor extends SensorObject{
    
    public static TemperatureSensor createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(TemperatureSensor.class, obj);
    }
    
    public static TemperatureSensor createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(TemperatureSensor.class, obj, whoasks);
    }
    
    public int getTemperature(){
        return  this.getShort(readProperty((byte)0xE0));
    }
    
    public boolean setTemperature(int temperature){
        return this.writeProperty((byte) 0xE0, ShortToBytes((short) temperature));
    }
}
