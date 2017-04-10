package jaist.echonet;

import java.net.InetAddress;
import java.util.Collections;

/**
 * Representation of a remote echonet object. 
 * 
 * @author Sioutis Marios
 */
public class RemoteEchonetObject extends AbstractEchonetObject {

    /**
     * This constructor is not public. To get a reference to a remote echonet 
     * object use {@link EchonetNode#getRemoteObject(java.net.InetAddress, multiunicast.EOJ) }
     * @param addr the IP address of the remote node (may be multicast address)
     * @param eoj the echonet object class information
     */
    RemoteEchonetObject(InetAddress addr, EOJ eoj) {
        super(eoj);
        this.queryip = addr;
    }

    /**
     * "Reads" the specified property of the remote object and returns its data.
     * This action will result into an echonet query being sent over the network,
     * and its results will be returned      * 
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
     * "Reads" the specified property of the remote object and returns its data.
     * This action will result into an echonet query being sent over the network,
     * and its results will be returned      * 
     * 
     * @param whoasks the echonet object that requests the read
     * @param property A property that has the same property code as the property
     * to be read. Usually an EchonetDummyProperty
     * @return the contents of this property (may be null)
     */
    public byte[] readProperty(AbstractEchonetObject whoasks, EchonetProperty property) {
        EchonetQuery query = getEchonetNode().makeQuery(whoasks, this, ServiceCode.Get, Collections.singletonList(property), null, null);

        //we do this to block until the answer returns. but there are two
        //choices to get the actual data. get it from the object, or read them
        //from the answer.
        EchonetAnswer answer = query.getNextAnswer();
        return super.readProperty(property.getPropertyCode());
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
     * @param copyfrom A property to get the property code for the property to be written
     * and also copy data from;
     * @return true in case of an error, false otherwise
     */
    @Override
    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom) {
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
}
