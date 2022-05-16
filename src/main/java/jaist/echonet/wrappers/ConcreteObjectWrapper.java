
package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;

/**
 * A concrete implementation of the {@link AbstractObjectWrapper}. 
 * @author Sioutis Marios
 */
public class ConcreteObjectWrapper extends AbstractObjectWrapper{
    public static ConcreteObjectWrapper createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(ConcreteObjectWrapper.class, obj);
    }
    
    public static ConcreteObjectWrapper createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(ConcreteObjectWrapper.class, obj, whoasks);
    }
}
