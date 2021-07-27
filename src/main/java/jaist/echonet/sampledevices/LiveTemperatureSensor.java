package jaist.echonet.sampledevices;

import java.util.logging.Level;
import java.util.logging.Logger;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.wrappers.TemperatureSensor;

/**
 * A sample temeprature sensor. It "senses" the environment and gives incrementing
 * temperature readings every second. The values are notified on the network
 * 
 * @author Sioutis Marios
 */
public class LiveTemperatureSensor extends TemperatureSensor{
    
    public static LiveTemperatureSensor createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(LiveTemperatureSensor.class, obj);
    }
    
    public static LiveTemperatureSensor createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(LiveTemperatureSensor.class, obj, whoasks);
    }
  
    public void start(){
        final LiveTemperatureSensor lts = this;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                int temperature = 5;
                while(true){
                    try {
                        lts.setTemperature(temperature++);
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LiveTemperatureSensor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
}
