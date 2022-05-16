package jaist.echonet;

import java.util.Calendar;

/**
 *
 * An {@link EchonetProperty} implementation for the property with code 0x98 (Date
 * property)
 *
 * @author Sioutis Marios
 */
public class EchonetDateProperty extends EchonetProperty {

    public EchonetDateProperty() {
        super((byte) 0x98, false, false);
        setCapacity(4);
    }

    /**
     * Returns the date, as a byte array of length 4, as described in the
     * documentation
     */
    @Override
    public byte[] read() {
        byte[] response = new byte[4];
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        year &= 0x0000FFFF;
        int month = rightNow.get(Calendar.MONTH);
        int day = rightNow.get(Calendar.DAY_OF_MONTH);

        response[0] = (byte) ((year >> 8));
        response[1] = (byte) (year & 0x0000FF);
        response[2] = (byte) (month + 1);
        response[3] = (byte) day;
        return response;
    }

    /**
     * Attempt to write this property will result in an error
     *
     * @return error (true - this is a read-only property)
     */
    @Override
    public boolean write(byte[] data) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static void main(String args[]) {
        EchonetDateProperty date = new EchonetDateProperty();
        byte[] data = date.read();
        short year = 0;
        year |= data[0] & 0x00FF;
        year <<= 8;
        year |= data[1] & 0x00FF;
        System.out.println(" DATE: " + year + " " + data[2] + " " + data[3]);
    }
}
