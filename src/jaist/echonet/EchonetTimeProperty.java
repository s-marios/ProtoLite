package jaist.echonet;

import java.util.Calendar;


/**
 *
 * An EchonetProperty implementation for the property with code 0x97 (Time
 * property)
 * 
 * @author Sioutis Marios
 */
public class EchonetTimeProperty extends EchonetProperty{
    
    /**
     * Constructor
     */
    public EchonetTimeProperty(){
        super((byte) 0x97, false, false);
        setCapacity(2);
    }


    /**
     * Gets the current time as measured at the local echonet object
     * @return the time in binary format, as specified in the documentation
     */
    @Override
    public byte[] read() {
        byte [] response = new byte[2];
        Calendar rightNow = Calendar.getInstance();
        response[0] = (byte) rightNow.get(Calendar.HOUR_OF_DAY);
        response[1] = (byte) rightNow.get(Calendar.MINUTE);
        return response;
    }

    @Override
    public boolean write(byte[] data) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
    public static void main(String args [] ){
        byte [] data = new EchonetTimeProperty().read();
        System.out.println("hours: " + data[0]);
        System.out.println("mins: " + data[1]);
    }

}
