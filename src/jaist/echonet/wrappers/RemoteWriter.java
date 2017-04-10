package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;

/**
 * Implements the writer logic for remote objects. Each write request will 
 * appear as a SetC request on the network, originating from the echonet object
 * "whoasks"
 * 
 * @author Sioutis Marios
 */
class RemoteWriter implements Writer {

    private RemoteEchonetObject remote;

    RemoteWriter(RemoteEchonetObject remote) {
        this.remote = remote;
    }

    @Override
    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty property) {
        return this.writeProperty(whoasks, property.getPropertyCode(), property.read());
    }

    @Override
    public boolean writeProperty(AbstractEchonetObject whoasks, byte propertycode, byte[] data) {
        if (data == null || data.length == 0) {
            return true;
        }
        return remote.writeProperty(whoasks, propertycode, data);
    }
}