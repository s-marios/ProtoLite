package jaist.echonet;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Part of the {@link EchonetNode}. Manages the {@link RemoteEchonetObject}
 * references. It is the implementation side of things for getting
 * {@link RemoteEchonetObject} references
 *
 * @author Sioutis Marios
 */
class RemoteEchonetObjectManager {

    EchonetNode context = null;

    RemoteEchonetObjectManager(EchonetNode node) {
        this.context = node;
    }

    private final Map<InetAddress, Map<EOJ, RemoteEchonetObject>> remoteObjects = new ConcurrentHashMap();

    private RemoteEchonetObject getRemoteEchonetObject(InetAddress queryip, EOJ eoj) {
        Map<EOJ, RemoteEchonetObject> map = remoteObjects.get(queryip);
        if (map != null) {
            return map.get(eoj);
        }
        return null;
    }

    public RemoteEchonetObject getOrCreateRemoteEchonetObject(InetAddress queryip, EOJ eoj) {
        if (queryip == null) {
            return null;
        }

        RemoteEchonetObject vobject = getRemoteEchonetObject(queryip, eoj);
        if (vobject == null) {
            vobject = new RemoteEchonetObject(queryip, eoj);
            registerEchonetObject(vobject);
        }
        return vobject;
    }

    public void registerEchonetObject(RemoteEchonetObject echobj) {
        echobj.setEchonetNode(this.context);
        InetAddress queryip = echobj.getQueryIp();
        Map<EOJ, RemoteEchonetObject> map = remoteObjects.get(queryip);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            remoteObjects.put(queryip, map);
        }
        map.put(echobj.getEOJ(), echobj);
    }
}
