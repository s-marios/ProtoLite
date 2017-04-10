package jaist.echonet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Part of the {@link EchonetNode}. Manages the {@link RemoteEchonetObject}
 * references. It is the implementation side of things for getting
 * RemoteEchonetObject references
 * 
 * @author Sioutis Marios
 */
class RemoteEchonetObjectManager {
    EchonetNode context = null;
    
    RemoteEchonetObjectManager(EchonetNode node){
        this.context = node;
    }
    
     //private static List<RemoteEchonetObject> vobjlist = new ArrayList<RemoteEchonetObject>();
    private Map<InetAddress, Map<EOJ, RemoteEchonetObject>> remoteObjects = new ConcurrentHashMap();
    
    private RemoteEchonetObject getRemoteEchonetObject(InetAddress queryip, EOJ eoj) {
        Map<EOJ, RemoteEchonetObject> map = remoteObjects.get(queryip);
        if(map != null) {
            return map.get(eoj);
        }
        return null;
    }
    
    public RemoteEchonetObject getOrCreateRemoteEchonetObject(InetAddress queryip, EOJ eoj){
        RemoteEchonetObject vobject = null;
        if(queryip == null)
            return null;
        vobject = getRemoteEchonetObject(queryip, eoj);
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
            map = new ConcurrentHashMap<EOJ, RemoteEchonetObject>();
            remoteObjects.put(queryip, map);
        }
        map.put(echobj.getEOJ(), echobj);
    }
   
    public static void main(String args[]) throws UnknownHostException{
        RemoteEchonetObjectManager rm = new RemoteEchonetObjectManager(null);
        rm.test();
    
    }

    private void test() throws UnknownHostException {
        InetAddress test = InetAddress.getLocalHost();
        InetAddress test2 = InetAddress.getLocalHost();
        
            RemoteEchonetObject robject = this.getOrCreateRemoteEchonetObject(test, 
                    new EOJ((byte)13,(byte)14,(byte)15));
            
            RemoteEchonetObject anotherobject = this.getOrCreateRemoteEchonetObject(test2, 
                    new EOJ((byte)44,(byte)4,(byte)16));
        
            System.out.println("robject:" + robject.toString());
            System.out.println("anobject:" + anotherobject.toString());
            
            test = InetAddress.getByName("150.65.1.1");
            test2 = InetAddress.getByName("150.65.1.1");
            
            RemoteEchonetObject robject2 = this.getOrCreateRemoteEchonetObject(test, 
                    new EOJ((byte)13,(byte)14,(byte)15));
            
            RemoteEchonetObject anotherobject2 = this.getOrCreateRemoteEchonetObject(test2, 
                    new EOJ((byte)13,(byte)14,(byte)15));
        
            System.out.println("robject2:" + robject2.toString());
            System.out.println("anobject2:" + anotherobject2.toString());
            
            RemoteEchonetObject anop = this.getOrCreateRemoteEchonetObject(InetAddress.getLocalHost(), 
                    new EOJ((byte)44,(byte)4,(byte)16));
            System.out.println("anop:" + anop.toString());
    }
    
}
