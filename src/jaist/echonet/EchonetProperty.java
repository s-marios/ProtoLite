package jaist.echonet;

/**
 * The main representation of an ECHONET property. An abstract class that 
 * defines the main members and access interface
 * 
 * @author Sioutis Marios
 */
public abstract class EchonetProperty {

    public static final int UPTO = 1;
    public static final int EXACT = 0;
    private byte propcode;
    protected boolean readable;
    protected boolean writable;
    protected boolean notifies;
    protected int capacitypolicy = EXACT;
    protected int capacity = 0;

    /**
     * 
     * Constructor. All properties are Readable by default, even in cases that
     * this may not be meaningful
     * 
     * Use the non-deprecated ones, they check for capacity.
     * 
     * @param propcode The property code
     * @param iswriteable If the property supports SET commands (is writeable)
     * @param notifies If the property must notify when its value changes
     */
    @Deprecated
    public EchonetProperty(byte propcode, boolean iswriteable, boolean notifies) {
        this.propcode = propcode;
        this.readable = true;
        this.writable = iswriteable;
        this.notifies = notifies;
    }

    /**
     * Constructor. 
     * 
     * Use this constructor for properties whose capacity is unknown or cannot
     * be known ahead of time.
     * 
     * @param propcode
     * @param isreadable
     * @param iswriteable
     * @param notifies 
     */
    public EchonetProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies) {
        this.propcode = propcode;
        this.readable = isreadable;
        this.writable = iswriteable;
        this.notifies = notifies;
    }
    /**
     * 
     * @param propcode
     * @param isreadable
     * @param iswriteable
     * @param notifies
     * @param capacity SET THE CAPACITY!
     * @param capacitypolicy 0 is exact 1 is up to 
     */
    public EchonetProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, int capacity, int capacitypolicy) {
        this.propcode = propcode;
        this.readable = isreadable;
        this.writable = iswriteable;
        this.notifies = notifies;
        this.capacity = capacity;
        this.capacitypolicy = capacitypolicy;
    }

    /**
     * Returns if this property is readable or not
     * 
     * @return true if it is readable, false otherwise
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * Returns if this property is writeable or not
     * 
     * @return true if it is writeable, false otherwise
     */
    public boolean isWriteable() {
        return writable;
    }

    /**
     * Returns if this property notifies when its value changes
     * @return true if it notifies, false otherwise
     */
    public boolean doesNotify() {
        return notifies;
    }

    /**
     * Read the contents of this property
     * 
     * @return a byte array with the contents
     */
    public abstract byte[] read();

    /**
     * Attempt to write the contents of this property
     * 
     * @param data the data to be written as content
     * @return true in case of an error(operation not supported), 
     * false if no error occurred 
     */
    public abstract boolean write(byte[] data);

    /**
     * Returns the property code of this property
     * @return the property code
     */
    public byte getPropertyCode() {
        return propcode;
    }

    /**
     * Returns true if this property is empty
     * @return true if this property is empty (no content), false otherwise
     */
    public abstract boolean isEmpty();

    public final int getCapacity() {
        return capacity;
    }

    protected final void setCapacity(int capacity) {
        this.capacity = (capacity > 0) ? capacity : 0;
    }

    public final int getCapacityPolicy() {
        return capacitypolicy;
    }

    protected final void setCapacityPolicy(int policy) {
        this.capacitypolicy = (policy == UPTO) ? UPTO : EXACT;
    }

    public final boolean isAcceptedSize(int datalength) {
        switch (this.capacitypolicy) {
            case EXACT:
                return datalength == this.capacity ? true : false;
            case UPTO:
                return datalength <= this.capacity ? true : false;
            default:
                return false;
        }
    }

    /**
     * Get a memento to restore the contents of this property in the future. 
     * Used in serialization
     * 
     * @return A memento that holds the contents of this property and its
     * property code
     */
    public PropertyMemento getMemento() {
        return new PropertyMemento(this);
    }
    
    
    //public abstract boolean write();
}
