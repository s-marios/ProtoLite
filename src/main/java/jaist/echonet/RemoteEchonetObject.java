package jaist.echonet;

import jaist.echonet.wrappers.AbstractObjectWrapper;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a remote ECHONET object.
 *
 * @author Sioutis Marios
 */
public class RemoteEchonetObject extends AbstractEchonetObject {

    /**
     * default expiration for queries on this object
     */
    private long timeout = EchonetProtocol.AWAITRESPONSE;

    /**
     * Get the service code used when Setting this property.
     *
     * @return the service code used with writeProperty, either
     * {@link WriteMode#SetC SetC} or {@link WriteMode#SetI SetI}.
     */
    public WriteMode getWriteMode() {
        return writemode;
    }

    /**
     * Set the service code to be used when using writeProperty, either
     * {@link WriteMode#SetC SetC} or {@link WriteMode#SetI SetI}.
     *
     * @param setmode the service code to use with writeProperty
     */
    public void setWriteMode(WriteMode setmode) {
        this.writemode = setmode;
    }

    /**
     * Representation of the service code used when setting a property.
     */
    public enum WriteMode {
        /**
         * Set request, response required
         */
        SetC,
        /**
         * Set request, response NOT required
         */
        SetI
    }

    private WriteMode writemode;

    /**
     * This constructor is not public. To get a reference to a remote echonet
     * object use {@link EchonetNode#getRemoteObject(java.net.InetAddress, multiunicast.EOJ)
     * }
     *
     * @param addr the IP address of the remote node (may be multicast address)
     * @param eoj the echonet object class information
     */
    RemoteEchonetObject(InetAddress addr, EOJ eoj) {
        super(eoj);
        this.queryip = addr;
        writemode = WriteMode.SetC;
    }

    /**
     * Set expiration time of queries regarding this object
     *
     * @param timeout the elapsed time in milliseconds after which queries on
     * this object will expire
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the expiration time of queries regarding this object
     *
     * @return the elapsed time in milliseconds after which queries on this
     * object will expire
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * "Reads" the specified property of the remote object and returns its data.
     * This action will result into an echonet query being sent over the
     * network, and its results will be returned *
     *
     * @param whoasks the echonet object that requests the read
     * @param property the property code to be read (as byte)
     * @return the contents of this property (may be null)
     */
    @Override
    public byte[] readProperty(AbstractEchonetObject whoasks, byte property) {
        return readProperty(whoasks, new EchonetDummyProperty(property));
    }

    /**
     * Reads the specified property of the remote object and returns its data.
     * This action will result into an echonet query being sent over the
     * network, and its results will be returned. The originating EOJ will be
     * that of the local node profile object.
     *
     * @param property the property code to be read (as byte)
     * @return the contents of this property (may be null)
     */
    @Override
    public byte[] readProperty(byte property) {
        return readProperty(this.getEchonetNode().getNodeProfileObject().getEchonetObject(), property);
    }

    /**
     * Reads the specified property of the remote object and returns its data.
     * This action will result into an echonet query being sent over the
     * network, and its results will be returned *
     *
     * @param whoasks the echonet object that requests the read
     * @param property A property that has the same property code as the
     * property to be read. Usually an EchonetDummyProperty
     * @return the contents of this property (may be null)
     */
    @Override
    public byte[] readProperty(AbstractEchonetObject whoasks, EchonetProperty property) {
        EchonetQuery query = getEchonetNode().makeQuery(whoasks, this, ServiceCode.Get, Collections.singletonList(property), null, null);
        //set the query timeout
        query.setTimeout(this.getTimeout());

        //return what we read from the first property in the answer
        EchonetAnswer answer = query.getNextAnswer();
        if (answer != null && !answer.getProperties().isEmpty()) {
            return answer.getProperties().get(0).read();
        }
        //properties are empty, the operation failed.
        return null;
    }

    /**
     * Attempt to write the specified property of the remote object with the
     * given data. A network packet will be generated with each such call.
     *
     * @param whoasks the echonet object that requests the write
     * @param property the property code (as byte) of the property to be written
     * @param data the data to set the property
     * @return true in case of an error, false otherwise
     */
    public boolean writeProperty(AbstractEchonetObject whoasks, byte property, byte[] data) {
        return writeProperty(whoasks, new EchonetCharacterProperty(property, true, true, data));
    }

    /**
     * Attempt to write the specified property of the remote object with the
     * given data. A network packet will be generated with each such call.
     *
     * @param whoasks the echonet object that requests the write
     * @param copyfrom A property to get the property code for the property to
     * be written and also copy data from;
     * @return true in case of an error, false otherwise
     */
    @Override
    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom) {
        if (this.writemode == WriteMode.SetI) {
            EchonetQuery query = getEchonetNode().makeQuery(whoasks, this, ServiceCode.SetI, Collections.singletonList(copyfrom), null, null);
            return false;
        }

        EchonetQuery query = getEchonetNode().makeQuery(whoasks, this, ServiceCode.SetC, Collections.singletonList(copyfrom), null, null);
        EchonetAnswer answer = query.getNextAnswer();

        if (answer == null) {
            return true;
        }
        if (answer.getResponseCode() == ServiceCode.SetC_SNA) {
            return true;
        }
        if (answer.getResponseCode() == ServiceCode.SetI_SNA) {
            return true;
        }
        return false;
    }

    /**
     * Attempt to write the specified property of the remote object with the
     * given data. A network packet will be generated with each such call. The
     * originating EOJ will be that of the local node profile object.
     *
     * @param property the property code (as byte) of the property to be written
     * @param data the data to set the property
     * @return true in case of an error, false otherwise
     */
    @Override
    public boolean writeProperty(byte property, byte[] data) {
        return writeProperty(this.getEchonetNode().getNodeProfileObject().getEchonetObject(), property, data);
    }

    /**
     * Update the list of properties that a remote object has. This will
     * initiate reads to the read/write/announce property maps and generate the
     * appropriate properties. Use this method before any calls to
     * {@link AbstractEchonetObject#getPropertyList() getPropertyList} to ensure
     * a valid list of properties.
     *
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePropertyList() {
        byte[] announcemap = this.readProperty((byte) 0x9D);
        byte[] writemap = this.readProperty((byte) 0x9E);
        byte[] readmap = this.readProperty((byte) 0x9F);
        byte[] announcecodes = AbstractObjectWrapper.propertyMap(announcemap);
        byte[] writecodes = AbstractObjectWrapper.propertyMap(writemap);
        byte[] readcodes = AbstractObjectWrapper.propertyMap(readmap);

        ArrayList<EchonetRemoteProperty> property_list = new ArrayList<>();
        //start from the read map
        if (readcodes == null) {
            return false;
        }
        for (Byte propcode : readcodes) {
            property_list.add(new EchonetRemoteProperty(this, propcode, true, false, false));
        }

        //write properties
        if (writecodes == null) {
            return false;
        }
        for (Byte propcode : writecodes) {
            EchonetRemoteProperty property = getOrCreateProperty(propcode, property_list);
            property.setWritable(true);
        }

        //finally, notify properties;
        if (announcecodes == null) {
            return false;
        }
        for (Byte propcode : announcecodes) {
            EchonetRemoteProperty property = getOrCreateProperty(propcode, property_list);
            property.setNotifies(true);
        }

        //add the properties to the remote object
        for (EchonetRemoteProperty property : property_list) {
            this.properties.put(property.getPropertyCode(), property);
        }

        return true;
    }

    private EchonetRemoteProperty getOrCreateProperty(Byte propertyCode, List<EchonetRemoteProperty> properties) {
        for (EchonetRemoteProperty property : properties) {
            if (property.getPropertyCode() == propertyCode) {
                return property;
            }
        }
        //no  property found, create a new one
        EchonetRemoteProperty result = new EchonetRemoteProperty(this, propertyCode, false, false, false);
        properties.add(result);
        return result;
    }
}
