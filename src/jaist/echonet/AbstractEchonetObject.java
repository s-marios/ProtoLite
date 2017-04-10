package jaist.echonet;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract class that represents Echonet objects. Implements some skeleton
 * logic for both remote and local echonet objects
 * @author Sioutis Marios
 */
public abstract class AbstractEchonetObject implements QueryableByOthers {

    /**
     * The echonet node associated with this object. will be used for queries
     */
    protected EchonetNode echonetNode = null;
    private List<EchoEventListener> listeners = new ArrayList<EchoEventListener>();
    /**
     * The class information associated with this object
     */
    protected EOJ eoj = null;
    /**
     * The list of properties of this object
     */
    protected Map<Byte, EchonetProperty> properties = new ConcurrentHashMap();
    /**
     * The ip address of this object. Not really useful, get the ip address by 
     * accessing the context
     */
    protected InetAddress queryip = null;

    /**
     * Gets a collection of all the properties of this echonet object
     * @return a collection of all the properties of this object
     */
    public Collection<EchonetProperty> getPropertyList() {
        return properties.values();
    }

    /**
     * Gets a reference to the property of this echonet object with the supplied
     * property code
     * @param propertycode the property code used as key 
     * @return the echonet property associated with the key, null if not
     * available
     */
    public EchonetProperty getProperty(byte propertycode) {
        return properties.get(propertycode);
    }

    /**
     * Sets the EchonetNode context associated with this echonet object
     * @param node the EchonetNode to set
     */
    public void setEchonetNode(EchonetNode node) {
        this.echonetNode = node;
    }

    /**
     * Gets the node context of this echonet object
     * @return the associated EchonetNode instance
     */
    public EchonetNode getEchonetNode() {
        return echonetNode;
    }

    /**
     * Constructor
     * @param eoj echonet class object information
     */
    public AbstractEchonetObject(EOJ eoj) {
        this.eoj = eoj;
    }

    /**
     * Adds a property to this object
     * @param property the property to be added
     */
    public void addProperty(EchonetProperty property) {
        properties.put(property.getPropertyCode(), property);
    }

    /**
     * Get the class object information of this object
     * @return the EOJ reference that holds the class object information for this
     * object
     */
    public EOJ getEOJ() {
        return eoj;
    }

    /**
     * Set the class object information
     * @param eoj the EOJ reference that holds the class object information for this
     * object
     */
    public void setEOJ(EOJ eoj) {
        this.eoj = eoj;
    }

    private InetAddress getIP() {
        if (queryip != null) {
            return queryip;
        }
        if (echonetNode != null) {
            return echonetNode.getIP();
        }
        return null;
    }

    /**
     * Returns the IP address associated with this echonet object. For local
     * echonet objects this method is not accurate.
     * 
     * @return the ip address associated with this object
     */
    public InetAddress getQueryIp() {
        return getIP();
    }

    /**
     * Reads the specified property
     * 
     * @param propertycode the code of the property to be read
     * @return a byte array with the data of this property, null if the property
     * is not available
     */
    public byte[] readProperty(byte propertycode) {
        EchonetProperty property = properties.get(propertycode);
        //EPC error
        if (property == null) {
            return null;
        }
        //ESV error
        //TODO remove doesNotify
        if (!( property.isReadable() || property.doesNotify() ) ) {
            return null;
        }
        return property.read();
    }

    /**
     * Set the IP address of this object.
     * @param queryip the IP to set
     */
    public void setQueryip(InetAddress queryip) {
        this.queryip = queryip;
    }

    /**
     * Attempts an unprivileged write of the specified property.
     * 
     * @param propertycode the code of the propety to be written
     * @param data the data to write
     * @return true if an error occurred, false otherwise
     */
    protected boolean writeProperty(byte propertycode, byte[] data) {
        EchonetProperty property = properties.get(propertycode);
        //EPC error
        if (property == null) {
            return true;
        }
        //ESV error
        if (!property.isWriteable()) {
            return true;
        }
        //EDT size error
        if (!property.isAcceptedSize(data.length)) {
            return true;
        }
        return property.write(data);
    }

    /**
     * Makes an attempt for an unprivilleged write of the specified property.
     * Will call all the write event listeners associated with this object.
     * 
     * @param copyfrom the property to use as a source (copy data and property 
     * code from)
     * @return true if an error occurred, false otherwise
     */
    //TODO Implement the error checking in appendix 3 of chapter 2
    public boolean writeProperty(EchonetProperty copyfrom) {
        boolean error = false;
        //call the listeners
        boolean processed = true;

        for (EchoEventListener listener : this.listeners) {
            processed = listener.processWriteEvent(copyfrom);
            if (!processed) {
                return true;
                // a write request was not processed
                // don't apply changes and return write error.
            }
        }

        if (processed) {
            //write the value after the whole thing has been processed. 
            error = writeProperty(copyfrom.getPropertyCode(), copyfrom.read());
            //chech if property warrants an INF multicast
            if (!error && this.properties.get(copyfrom.getPropertyCode()).doesNotify()) {
                inform(copyfrom);
            }
        }
        return error;
    }

    /**
     * Registers the supplied write listener
     * @param listener the write listener to be registered. Will be called when
     * there are write attempts.
     */
    public void registerListener(EchoEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Used internally to make notifications when the data of a property that 
     * "notifies" are changed
     * 
     * @param property the property that will make the notify (its contents will
     * be notified to the network)
     */
    protected void inform(EchonetProperty property) {
        echonetNode.makeNotification(this, property);
    }
}
