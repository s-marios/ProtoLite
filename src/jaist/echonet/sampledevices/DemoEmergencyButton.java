package jaist.echonet.sampledevices;

import jaist.echonet.*;
import jaist.echonet.wrappers.EmergencyButton;

/**
 * A demo emergency button (sample device). When the property 0xBF is written,
 * the message "Button was pressed" is printed on the standard output
 * 
 * @author Sioutis Marios
 */
public class DemoEmergencyButton extends EmergencyButton implements EchoEventListener {
    
    private int howmany = 0;
    
    public static DemoEmergencyButton createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(DemoEmergencyButton.class, obj);
    }
    
    public static DemoEmergencyButton createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(DemoEmergencyButton.class, obj, whoasks);
    }
    
    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        if(property.getPropertyCode() == (byte) 0xBF  && this.getShort(property.read()) == 0)
        {
            howmany++;
            System.out.println("Button was pressed! Total presses: " + howmany);            
            return true;
        }
        return false;
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
