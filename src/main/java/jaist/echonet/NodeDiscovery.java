/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used for performing node and object discovery in the network. For
 * simple discovery, use {@link #discoverAllObjectsBlocking() discoverAllObjectsBlocking}
 *
 * @author haha
 */
public class NodeDiscovery implements EchoEventListener{

    private final EchonetNode context;

    /**
     * Use {@link EchonetNode#getNodeDiscovery() getNodeDiscovery()} or this constructor
     * to get an instance.
     * @param context the echonet lite context to use.
     * @see EchonetNode#getNodeDiscovery() 
     */
    public NodeDiscovery(EchonetNode context) {
        this.context = context;
    }

    /**
     * Performs an active scan for echonet lite nodes in the network using
     * multicast. This will probably take some time (until timeout)
     *
     * @return a list of nodes that can be used with
     * {@link #getObjectsFromNode(jaist.echonet.RemoteEchonetObject) getObjectsFromNode}
     */
    public List<RemoteEchonetObject> discoverNodesBlocking() {
        List<RemoteEchonetObject> result = new ArrayList<>();

        //poll all nodeprofile objects for their list of objects.
        byte[] objectlistpc = new byte[]{(byte) 0xD6};
        RemoteEchonetObject rnode = context.getRemoteObject(context.getGroupIP(), EchonetProtocol.NODEPROFILEOJ);
        EchonetQuery responses = context.makeQuery(context.getNodeProfileObject().getLocalEchonetObject(), rnode, ServiceCode.Get, EchonetDummyProperty.getDummies(objectlistpc), null, null);
        EchonetAnswer nextAnswer;
        while ((nextAnswer = responses.getNextAnswer()) != null) {
            result.add(nextAnswer.getResponder());
        }

        return result;
    }

    /**
     * Returns a list of remote objects present in the remote node
     *
     * @param rnode the remote node. Must be a remote object with eoj of 0EF0xx.
     * @return list of remote objects in the node, a zero size list if the operation failed.
     */
    public List<RemoteEchonetObject> getObjectsFromNode(RemoteEchonetObject rnode) {
        List<RemoteEchonetObject> objects = new ArrayList<>();
        
        //check that we actually have a remote node object.
        if (!rnode.getEOJ().isNodeClass()) {
            return objects;
        }
        
        byte[] response = rnode.readProperty((byte) 0xD6);
        if (response != null && response.length != 0) {
            for (int i = 0; i < response[0]; i ++) {
                //the +1 offset skips the first byte in the response.
                EOJ eoj = new EOJ(response[i*3 + 1], response[i*3 + 1 + 1], response[i*3 + 2 + 1]);
                objects.add(context.getRemoteObject(rnode.getQueryIp(), eoj));
            }
        }
        return objects;
    }

    /**
     * Attempt to actively discover all the echonet lite objects present in the
     * network, using active requests.
     * @return list of all the objects in the network (may be zero-length).
     */
    public List<RemoteEchonetObject> discoverAllObjectsBlocking() {
        List<RemoteEchonetObject> objects = new ArrayList<>();
        List<RemoteEchonetObject> nodes = this.discoverNodesBlocking();
        for (RemoteEchonetObject node : nodes) {
            objects.addAll(this.getObjectsFromNode(node));
        }
        objects.addAll(nodes);
        return objects;
    }
    
    public void test() {
        this.context.registerForNotifications(null, (byte) 0x0E, (byte) 0xF0, (byte) 0x00, (byte) 0xD5, this);
    }

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        return true;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
