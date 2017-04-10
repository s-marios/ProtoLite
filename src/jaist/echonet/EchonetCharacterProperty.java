package jaist.echonet;

import java.nio.ByteBuffer;

/**
 * An EchonetPrpoerty implementation that acts only as a byte buffer
 * @author Sioutis Marios
 */
public class EchonetCharacterProperty extends EchonetProperty {
    //we don't stipulate anything about the size of the array and we don't
    //preallocate anything.

    private ByteBuffer databuffer;

    //use when data is not going to change.
    /**
     * Constructor
     * 
     * @param propcode the property code
     * @param iswriteable true if the property is writable, false otherwise
     * @param notifies true if the property should notify, false otherwise
     * @param data the original data the property will be initialized with
     */
    @Deprecated
    public EchonetCharacterProperty(byte propcode, boolean iswriteable, boolean notifies, byte[] data) {
        super(propcode, iswriteable, notifies);
        initContent(data.length, data);
        setCapacity(data.length);
    }
    
    @Deprecated
    public EchonetCharacterProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, byte[] data) {
        super(propcode, isreadable, iswriteable, notifies);
        initContent(data.length, data);
        setCapacity(data.length);
    }
    
    public EchonetCharacterProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, int capacity, int capacitypolicy, byte[] data) {
        super(propcode, isreadable, iswriteable, notifies, capacity, capacitypolicy);
        initContent(capacity, data);
    }

    private void initContent(int datalength, byte[] data) {
        this.initContentSize(datalength);
        this.write(data);
    }

    private void initContentSize(int datalength) {
        if (datalength > 255) {
            throw new RuntimeException("Property exceeds 255bytes");
        }
        this.databuffer = ByteBuffer.allocate(datalength);
        this.databuffer.limit(datalength);
        this.setCapacity(datalength);
    }
    //use this when you want to allocate the space and expect that data can
    //grow up to data length

    /**
     * Constructor
     * 
     * @param propcode the property code
     * @param iswriteable iswriteable true if the property is writable, false otherwise
     * @param notifies true if the property should notify, false otherwise
     * @param datalength the initial data length. Data will be initialized to zero
     */
    @Deprecated
    public EchonetCharacterProperty(byte propcode, boolean iswriteable, boolean notifies, int datalength) {
        super(propcode, iswriteable, notifies);
        initContentSize(datalength);
    }
    
    @Deprecated
    public EchonetCharacterProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, int datalength){
        super(propcode, isreadable, iswriteable, notifies);
        initContentSize(datalength);
    }
    
    public EchonetCharacterProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, int capacity, int capacitypolicy){
        super(propcode, isreadable, iswriteable, notifies, capacity, capacitypolicy);
        initContentSize(capacity);
    }

    /**
     * Returns the data held in this property.
     * 
     * @return It will never be null, but the length can be zero. Check against this.
     */
    @Override
    public synchronized byte[] read() {
        databuffer.rewind();
        byte[] data = new byte[databuffer.limit()];
        databuffer.get(data);
        return data;
    }

    @Override
    public synchronized boolean write(byte[] data) {
        //erased the data.length == 0 condition, might want to completely erase a string or sthng...
        if (data == null || data.length == 0 || data.length > 255 || data.length > databuffer.capacity()) {
            return true;
        }
        try {
            this.databuffer.rewind();
            this.databuffer.limit(data.length);
            this.databuffer.put(data);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean isEmpty() {
        return databuffer.limit() == 0 ? true : false;
    }
}
