package jaist.echonet.wrappers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProtocol;
import jaist.echonet.LocalEchonetObject;

/**
 * Implementation of the logic of a NodeProfileObject object. Used internally by
 * {@link EchonetNode}
 * 
 * @author Sioutis Marios
 */
public class NodeProfileObjectImpl extends NodeProfileObject {

    private Map<EOJ, LocalEchonetObject> objects = new ConcurrentHashMap();
    private Map<EOJ, List<LocalEchonetObject>> objectclasses = new ConcurrentHashMap();
    /** 
     * A convinience list that only contains the node, initialized at startup for
     * ease of use
     */
    private List<LocalEchonetObject> thenode;

    /**
     * Registeres a local echonet object with this node
     * 
     * @param echobj the local echonet object to be registered 
     */
    public void registerEchonetObject(LocalEchonetObject echobj) {
        //skip the registering of any node profile node
        if (isNode(echobj.getEOJ())) {
            // BUGFIX: ..but do refresh all the lists
            this.refreshLists();
            return;
        }

        EOJ reoj = echobj.getEOJ();
        EOJ ceoj = reoj.getClassEOJ();
        objects.put(reoj, echobj);
        List<LocalEchonetObject> list = objectclasses.get(ceoj);
        if (list == null) {
            list = new ArrayList<LocalEchonetObject>();
            objectclasses.put(ceoj, list);
        }
        list.add(echobj);
        this.refreshLists();
    }

    public void unregisterEchonetObject(LocalEchonetObject echobj) {
        if (isNode(echobj.getEOJ())){
            return;
        }
        EOJ reoj = echobj.getEOJ();
        EOJ ceoj = reoj.getClassEOJ();
        objects.remove(reoj);
        List<LocalEchonetObject> list = objectclasses.get(ceoj);
        if (list != null && list.isEmpty()) {
            //removed the last instance of this object. remove list too
            objectclasses.remove(ceoj);
        }
        this.refreshLists();
    }

    public List<LocalEchonetObject> getEchonetObjectsWithClass(EOJ eoj) {
        if (isNode(eoj)) {
            return getThenode();
        }
        return this.objectclasses.get(eoj.getClassEOJ());
    }

    public LocalEchonetObject getEchonetObject(EOJ eoj) {
        if (isNode(eoj)) {
            //return the underlying node
            return getThenode().get(0);
        }
        return this.objects.get(eoj);
    }

    public boolean isEchonetObjectRegisteredWithEOJ(EOJ eoj) {
        if (isNode(eoj)) {
            return true;
        }
        return this.objects.containsKey(eoj);
    }

    //TODO confirm that this works as intended
    private void refreshLists() {
        //refresh object lists.
        byte[] instancelist = new byte[3 * objects.values().size() + 1];
        instancelist[0] = (byte) objects.values().size();
        int i = 1;
        for (AbstractEchonetObject object : objects.values()) {
            EOJ object_eoj = object.getEOJ();
            instancelist[i++] = object_eoj.getClassGroupCode();
            instancelist[i++] = object_eoj.getClassCode();
            instancelist[i++] = object_eoj.getInstanceCode();
        }
        writeProperty((byte) 211, new byte[]{0x00, 0x00, (byte) (objects.size())});
        writeProperty((byte) 212, new byte[]{0x00, (byte) objectclasses.size()});
        //TODO this will break with more than 84 instances. fix .. sometime
        //these seem tobe the same. one of them just notifies...
        writeProperty((byte) 213, instancelist);
        writeProperty((byte) 214, instancelist);
        //refresh object class list
        ByteBuffer bb = ByteBuffer.allocate(2 * objectclasses.size() + 1);
        bb.put((byte) objectclasses.size());
        
        for (EOJ ceoj : objectclasses.keySet()) {
            bb.putShort(ceoj.shortValueForClass());
            //bb.put(ceoj.getClassGroupCode());
            //bb.put(ceoj.getClassCode());
        }
        this.getLocalEchonetObject().adminWriteProperty((byte) 215, bb.array());
    }

    private boolean isNode(EOJ eoj) {
        if (eoj.getClassGroupCode() == EchonetProtocol.NODEPROFILEOJ.getClassGroupCode()) {
            if (eoj.getClassCode() == EchonetProtocol.NODEPROFILEOJ.getClassCode()) {
                if (eoj.getInstanceCode() == 0 || eoj.getInstanceCode() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the thenode
     */
    private List<LocalEchonetObject> getThenode() {
        if (thenode == null) {
            thenode = new ArrayList<LocalEchonetObject>();
            thenode.add(this.getLocalEchonetObject());
        }
        return thenode;
    }

}
