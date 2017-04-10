package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchonetProperty;
import jaist.echonet.LocalEchonetObject;

/**
 * Implements the writing of properties for local objects. Writes originate from
 * a device logic thread, thus they have "privileged" write access. 
 *
 * @author Sioutis Marios
 */
public class LocalWriter implements Writer {

    private LocalEchonetObject local;

    public LocalWriter(LocalEchonetObject local) {
        this.local = local;
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
        return local.adminWriteProperty(propertycode, data);
    }
}
