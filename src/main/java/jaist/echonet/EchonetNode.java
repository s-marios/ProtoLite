package jaist.echonet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jaist.echonet.wrappers.NodeProfileObject;
import jaist.echonet.config.NodeProfileInfo;
import jaist.echonet.wrappers.AbstractObjectWrapper;
import jaist.echonet.wrappers.NodeProfileObjectImpl;

/**
 * The core class of this framework, represents an ECHONET node.
 *
 * @author Sioutis Marios
 */
public class EchonetNode {

    private final Map<Integer, Query> openqueries = new ConcurrentHashMap();
    private final NotificationManager nmanager = NotificationManagerSingleThreaded.get();
    private final EchonetPayloadCreator pcreate = new EchonetPayloadCreator();
    private final EchonetPayloadCreator presponse = new EchonetPayloadCreator();
    private final EchonetPayloadParser pparse = new EchonetPayloadParser(null);
    private final CallbackRunner runner = new CallbackRunner();
    private boolean stop = false;
    private EchonetNetwork network;
    private NodeProfileObjectImpl profile;
    private RemoteEchonetObjectManager remotemanager;

    /**
     * Gets the associated with this node <code>NodeProfileObject</code>. There
     * is no need to access this object unless you know what you're doing
     *
     * @return the {@link NodeProfileObject}
     */
    public NodeProfileObject getNodeProfileObject() {
        return profile;
    }

    /**
     * Gets the local IP with which initialization was attempted. This does NOT
     * necessarily return the IP address of the outgoing network interface.
     *
     * @return the potential IP address this node has
     */
    public InetAddress getIP() {
        return network.getLocalIP();
    }

    /**
     * Gets the multicast group IP address associated with this node. May be
     * IPv4 or IPv6
     *
     * @return the multicast IP address (IPv4 or IPv6)
     */
    public InetAddress getGroupIP() {
        return network.getGroupIP();
    }

    /**
     * Null constructor. IP initialization will default to IPv4 and all
     * interfaces
     */
    public EchonetNode() {
        setup(null);
    }

    /**
     * Constructor. IP initialization will try to bind the <code>address</code>
     * argument. Can be either IPv4 or IPv6. However, the node will listen to
     * ALL AVAILABLE NETWORK INTERFACES.
     *
     * @param address the address to attempt to bind to
     */
    public EchonetNode(InetAddress address) {
        setup(address);
    }

    /**
     * Register a LocalEchonetObject with this node. This operation is necessary
     * and must be done once for each LocalEchonetObject after its
     * initialization has finished
     *
     * @param echobj the EchonetObject to register
     * @see LocalEchonetObject
     */
    public void registerEchonetObject(LocalEchonetObject echobj) {
        //this is needed for the first and only time.
        //a profile object will register with.. itself, thus having
        //circular dependency.        
        if (echobj == null) {
            return;
        }
        echobj.setEchonetNode(this);
        profile.registerEchonetObject(echobj);
    }

    public void unregisterEchonetObject(LocalEchonetObject echobj) {
        profile.unregisterEchonetObject(echobj);
    }

    public LocalEchonetObject getEchonetObject(EOJ eoj) {
        return profile.getEchonetObject(eoj);
    }

    private void setup(InetAddress address) {
        remotemanager = new RemoteEchonetObjectManager(this);
        runner.start();

        network = new EchonetNetwork(address);

        LocalEchonetObject nodeobj = new LocalEchonetObject(new NodeProfileInfo());
        profile = AbstractObjectWrapper.getLocalInstance(NodeProfileObjectImpl.class, nodeobj);
        //TODO check this out.
        registerEchonetObject(nodeobj);

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new removeFinishedQueries(), 500, 1000);
    }

    /**
     * Gets an instance of a {@link RemoteEchonetObject}. This is the only
     * method to obtain a reference to a remote object. The remote object
     * represented may be reused in many other places
     *
     * @param ip The IP address of the remote object. May be a multicast address
     * thus specifying all devices in the network (useful for multicast queries)
     * @param eoj The echonet object information of the remote object. May use
     * an instance number of 0x00 to represent all instances of a device class
     * @return a reference to the remote object
     */
    public RemoteEchonetObject getRemoteObject(InetAddress ip, EOJ eoj) {
        return this.remotemanager.getOrCreateRemoteEchonetObject(ip, eoj);
    }

    private class removeFinishedQueries extends TimerTask {

        @Override
        public void run() {
            synchronized (openqueries) {
                Iterator<Map.Entry<Integer, Query>> i = openqueries.entrySet().iterator();
                while (i.hasNext()) {
                    Query query = i.next().getValue();
                    if (query.hasExpired()) {

                        //notify waiting clients possibly?
                        //check if a) zero responses and b) a registered handler
                        if (query.getResponseAvailable() == 0) {
                            //notify listeners
                            //TODO why is this cast necessary? 
                            // why are openqueries of type Query and not EchonetQuery?
                            if (query instanceof EchonetQuery) {
                                EchonetQuery eq = (EchonetQuery) query;
                                if (eq.getListener() != null) {
                                    runner.postJob(eq.getListener(), null);
                                }
                            }
                        }

                        i.remove();
                    }
                }
            }
        }
    }

    /**
     * Starts the operation of this node. Must be called in order to start the
     * receiver thread of the node.
     *
     * @return the thread object associated with this node. The node is by
     * default a daemon thread, so create some other processing thread to avoid
     * the program exiting
     */
    public Thread start() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                mainloop();
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Stops the receiving thread of this node
     */
    public void stop() {
        this.stop = true;
    }

    private void mainloop() {
        List<LocalEchonetObject> echobjlist = new ArrayList<LocalEchonetObject>();
        List<LocalEchonetObject> echobjlist_p;
        LocalEchonetObject echobj_p;
        while (!stop) {
            try {
                InetAddress remote = network.recvEchonetPayload(this.pparse);
                if (pparse.getErrno() != EchonetPayloadParser.errorcode.SUCCESS) {
                    Logging.getLogger().log(Level.INFO, "Dropped packet. Reason: {0}", pparse.getErrno());
                    continue;
                }

                //Check if request is for an object class we know of
                //if yes, further check if it is for an instance
                //if it is not for a specific instance, process the list of 
                //objects returned previously.
                echobjlist_p = this.profile.getEchonetObjectsWithClass(pparse.getDEOJ().getClassEOJ());
                if (echobjlist_p == null) {
                    continue;
                }

                echobj_p = this.profile.getEchonetObject(pparse.getDEOJ());
                if (echobj_p != null) //specific instance, add it to the list and proceed
                {
                    echobjlist_p = echobjlist;
                    echobjlist_p.add(echobj_p);
                }

                switchLogic(remote, echobjlist_p);

                if (echobjlist_p == echobjlist) {
                    echobjlist.clear();
                }

            } catch (SocketTimeoutException ex) {
                //have this so that the thread wakes up from time to time to
                //check if it should end
            } catch (IOException ex) {
                Logger.getLogger(EchonetNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void switchLogic(InetAddress remote, List<LocalEchonetObject> echobjlist) {
        //main switching logic is here is here
        switch (ServiceCode.getOpcode(pparse.getESV())) {
            case INF_REQ:// intentional fall through!
            case Get:
                handleGetInfRequests(remote, echobjlist, ServiceCode.getOpcode(pparse.getESV()));
                break;
            case INF_SNA: //intentional
            case INF: //intentional
                handleIncomingNotifications(remote);
                break;
            case INFC:
                if (handleIncomingNotifications(remote)) {
                    synchronized (this.presponse) {
                        //the notification came for an object on this node
                        //we have to write a response.
                        this.craftEchonetResponse(this.presponse, ServiceCode.INFC_Res, pparse.getDEOJ());
                        for (EchonetProperty property : pparse.getPropertyList()) {
                            presponse.writeOperand(property.getPropertyCode(), null);
                        }
                        this.writeResponseToNetwork(remote, presponse.getPayload());
                    }
                }
                break;

            case SetGet:
                handleSetGetRequest(remote, echobjlist);
                break;

            case SetC: // this is copy-pe from SetI but with a couple of 
                //differences. 
                // TODO consider merging these two
                for (AbstractEchonetObject echo : echobjlist) {
                    //in the old days it was pcreate
                    //but there's a chance that, when writting a proerty
                    //it might generate a notification (and that would
                    //use pcreate and screw things up)
                    //so now uses presponse
                    synchronized (this.presponse) {
                        boolean error = false;
                        craftEchonetResponse(this.presponse, ServiceCode.Set_Res, echo.getEOJ());
                        for (EchonetProperty property : pparse.getPropertyList()) {
                            if (echo.writeProperty(property)) {
                                //an error occured during write!
                                error = true;
                                presponse.writeOperand(property.getPropertyCode(), property.read());
                                //pcreate.setESV(ServiceCode.SetI_SNA);
                            } else {
                                //everything went ok. write prop code and zero data
                                presponse.writeOperand(property.getPropertyCode(), null);
                            }
                        }// end of processing each argument. write response to network.
                        //in case of an error, set response to 0x51.
                        if (error) {
                            presponse.setESV(ServiceCode.SetC_SNA);
                        }
                        this.writeResponseToNetwork(remote, presponse.getPayload());
                    }// synchronized for each object requested.
                }
                break;

            case SetI:
                for (AbstractEchonetObject echo : echobjlist) {
                    synchronized (this.presponse) {
                        boolean error = false;
                        craftEchonetResponse(this.presponse, ServiceCode.SetI_SNA, echo.getEOJ());
                        for (EchonetProperty property : pparse.getPropertyList()) {
                            if (echo.writeProperty(property)) {
                                //an error occured during write!
                                error = true;
                                presponse.writeOperand(property.getPropertyCode(), property.read());
                            } else {
                                //everything went ok. write prop code and zero data
                                presponse.writeOperand(property.getPropertyCode(), null);
                                //TODO check specifications if a "break" is needed
                            }
                        }// end of processing each argument. write response to network.
                        //in case of an error, write the response to the netwrk.
                        if (error) {
                            this.writeResponseToNetwork(remote, presponse.getPayload());
                        }

                    }// synchronized for each object requested.
                }
                break;
            case SetGetI_SNA: //intentional
            case SetGet_Res: //intentional 
            case INFC_Res: //intentional
            case Get_Res: // intentional
            case Get_SNA: // intentional
            case SetC_SNA: //intentional
            case SetI_SNA: //intentional fall through!
            case Set_Res:
                this.handleResponses(remote, ServiceCode.getOpcode((byte) pparse.getESV()));
                break;
            default:
                pparse.dumpPacket();
                break; // unrecognized echonet op. do nothing

        }
    }

    private void handleSetGetRequest(InetAddress remote, List<LocalEchonetObject> echobjlist) {
        for (LocalEchonetObject object : echobjlist) {
            synchronized (this.presponse) {
                boolean error = false;
                craftEchonetResponse(this.presponse, ServiceCode.SetGet_Res, object.getEOJ());
                //first step: try to set the properties
                for (EchonetProperty property : pparse.getPropertyList()) {
                    if (object.writeProperty(property)) {
                        //an error occured during write!
                        error = true;
                        presponse.writeOperand(property.getPropertyCode(), property.read());
                    } else {
                        //everything went ok. write prop code and zero data
                        presponse.writeOperand(property.getPropertyCode(), null);
                        //TODO check specifications if a "break" is needed
                    }
                }
                //second step: do the gets
                presponse.startOPC2();
                for (EchonetProperty property : pparse.getSecondPropertyList()) {
                    byte[] data = object.readProperty(property.getPropertyCode());
                    presponse.writeOperand(property.getPropertyCode(), data);
                    if (data == null || data.length == 0) {
                        error = true;
                    }
                }

                if (error) {
                    presponse.setESV(ServiceCode.SetGetI_SNA);
                }
                this.writeResponseToNetwork(remote, presponse.getPayload());
            }
        }
    }

    /**
     * Used to setup the skeleton for a response. Seoj is the sending eoj.
     */
    private void craftEchonetResponse(EchonetPayloadCreator create, ServiceCode esv, EOJ seoj) {
        synchronized (create) {
            create.resetCreator(pparse.getTID());
            create.setESV(esv);
            create.setDEOJ(pparse.getSEOJ());
            create.setSEOJ(seoj);
        }

    }

    private void craftEchonetQuery(ServiceCode esv, AbstractEchonetObject sender, AbstractEchonetObject destination) {
        this.craftEchonetQuery(esv, sender.getEOJ(), destination.getEOJ());
    }

    private void craftEchonetQuery(ServiceCode esv, EOJ seoj, EOJ deoj) {
        synchronized (this.pcreate) {
            pcreate.resetCreator();
            pcreate.setDEOJ(deoj);
            pcreate.setSEOJ(seoj);
            pcreate.setESV(esv);
        }
    }

    private void writeResponseToNetwork(InetAddress destination, byte[] payload) {
        try {
            network.sendData(destination, payload);
            Logging.getLogger().log(Level.FINEST, "Sent response to a write.");
        } catch (IOException ex) {
            Logger.getLogger(EchonetNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Used to make notifications. Echonet objects can use this method to notify
     * the network for changes in the value of their properties
     *
     * @param sender The originator of this notification
     * @param property the property whose value will be notified across the
     * network
     */
    public void makeNotification(AbstractEchonetObject sender, EchonetProperty property) {
        synchronized (pcreate) {
            RemoteEchonetObject target = getRemoteObject(network.getGroupIP(), EchonetProtocol.NODEPROFILEOJ);
            this.craftEchonetQuery(ServiceCode.INF, sender, target);
            this.writeOperands(new EchonetProperty[]{property});
            sendQuery(target.getQueryIp());
        }
    }

    /**
     * Makes and sends an echonet query to the network. All types of services
     * are implemented using this method.
     *
     *
     * @param sender the originator of this query
     * @param target the destination of this query
     * @param service the desired echonet service
     * @param properties the main property list. For example, in the case of a
     * GET request, a list of {@link EchonetDummyProperty} objects can be used
     * to express the properties whose values to get
     * @param secondary the secondary property list (in case of SETGET requests,
     * can be null)
     * @param listener an <code>EchoEventListener</code> that will be executed
     * asynchronously as soon as an answer is received (can be null)
     * @return a query object used for synchronization and synchronous
     * processing of the answers
     * @see EchonetAnswer
     * @see EchoEventListener
     */
    public EchonetQuery makeQuery(AbstractEchonetObject sender, AbstractEchonetObject target, ServiceCode service,
            List<EchonetProperty> properties, List<EchonetProperty> secondary, EchoEventListener listener) {
        Logging.getLogger().log(Level.FINE, "xxx makeQuery target1: {0}", target.getQueryIp());
        EchonetQuery query = new EchonetQuery(service, sender, listener);
        synchronized (pcreate) {
            this.craftEchonetQuery(service, sender, target);
            //write the first set of properties
            switch (service) {
                case Get:
                case INF_REQ:
                    writeOperandsNull(properties);
                    break;
                default:
                    writeOperands(properties);
            }

            //write the second set
            if (secondary != null && secondary.size() > 0) {
                //we have a setget
                pcreate.startOPC2();
                writeOperandsNull(secondary);
            }
            openqueries.put((int) pcreate.getCurrentTID(), query);
            Logging.getLogger().log(Level.FINE, "xxx makeQuery target2: {0}", target.getQueryIp());
            sendQuery(target.getQueryIp());
        }
        return query;
    }

    private void sendQuery(InetAddress destination) {
        try {
            network.sendEchonetPayload(destination, pcreate);
        } catch (IOException ex) {
            Logger.getLogger(EchonetNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private EchonetAnswer prepareAnswerAndUpdateObjects(
            Query query, ServiceCode service,
            RemoteEchonetObject vobject,
            List<EchonetProperty> properties,
            List<EchonetProperty> secondaryproperties) {
        EchonetAnswer answer = new EchonetAnswer(query, vobject, service, properties, secondaryproperties);
        switch (service) {
            //TODO check this switch again, thoroughly, does it do what it's 
            //sypposed to do?
            case SetGet_Res: //intentional
            case SetGetI_SNA: //intentional
                for (EchonetProperty property : secondaryproperties) {
                    vobject.addProperty(property);
                }
                break;
            case Get_Res: //intentional
            case Get_SNA: //intentional,
                for (EchonetProperty property : properties) {
                    vobject.addProperty(property);
                }
            case Set_Res://intentional, everything went ok, reflect changes in vobject
            case SetC_SNA: //intentional
            case SetI_SNA: //intentional fall through!
            case INFC_Res:
            default:
                break;
        }
        return answer;
    }

    private void handleResponses(InetAddress remote, ServiceCode service) {
        Query query = this.openqueries.get((int) pparse.getTID());

        if (query instanceof EchonetQuery) {
            EchonetQuery multiquery = (EchonetQuery) query;
            synchronized (multiquery) {
                RemoteEchonetObject vobject = getRemoteObject(remote, pparse.getSEOJ());
                EchonetAnswer answer = prepareAnswerAndUpdateObjects(multiquery, service, vobject, pparse.getPropertyList(), pparse.getSecondPropertyList());
                multiquery.add(answer);
                multiquery.notifyAll();
                //invoke handler, if applicable.
                if (multiquery.getListener() != null) {
                    runner.postJob(multiquery.getListener(), answer);
                }
            }
        }
    }

    private boolean handleIncomingNotifications(InetAddress remote) {
        //handle incoming random notifications
        Logging.getLogger().log(Level.FINE, "Received notification!!!");
        RemoteEchonetObject robject = getRemoteObject(remote, pparse.getSEOJ());
        //preprocess the list. if a property has zero length prune it.
        for (EchonetProperty property : pparse.getPropertyList()) {
            if (!property.isEmpty()) {
                robject.addProperty(property);
            }
        }
        this.nmanager.invokeNotificationManager(robject, pparse.getPropertyList());
        return profile.isEchonetObjectRegisteredWithEOJ(pparse.getSEOJ());
    }

    private void handleGetInfRequests(InetAddress remote, List<LocalEchonetObject> echobjlist, ServiceCode service) {
        //setup error codes depending on the request
        ServiceCode error, ok;
        boolean multicast = false;
        switch (service) {
            case Get:
                error = ServiceCode.Get_SNA;
                ok = ServiceCode.Get_Res;
                break;
            case INF_REQ:
                error = ServiceCode.INF_SNA;
                ok = ServiceCode.INF;
                multicast = true;
                break;
            default:
                return;
        }
        for (AbstractEchonetObject obj : echobjlist) {
            synchronized (this.presponse) {
                this.craftEchonetResponse(this.presponse, ok, obj.getEOJ());
                for (EchonetProperty property : pparse.getPropertyList()) {
                    Logging.getLogger().log(Level.FINE, "xxx EPC: {0}", String.format("0x%2x", 0x000000ff & (int) property.getPropertyCode()));
                    byte[] data = obj.readProperty(property.getPropertyCode());
                    if (data != null) {
                        Logging.getLogger().log(Level.FINE, "xxx size: {0}", data.length);
                    }
                    presponse.writeOperand(property.getPropertyCode(), data);
                    if (data == null || data.length == 0) {
                        presponse.setESV(error);
                        multicast = false;
                    }
                }
                //write packet.
                if (multicast) {
                    Logging.getLogger().log(Level.FINE, "xxx multicast: {0}", network.getGroupIP());
                    this.writeResponseToNetwork(network.getGroupIP(), presponse.getPayload());
                } else {
                    Logging.getLogger().log(Level.FINE, "xxx unicast: {0}", remote.getHostAddress());
                    this.writeResponseToNetwork(remote, presponse.getPayload());
                }
            }//synchronized
        }
    }

    private void writeOperands(List<EchonetProperty> properties) {
        for (EchonetProperty property : properties) {
            pcreate.writeOperand(property.getPropertyCode(), property.read());
        }
    }

    private void writeOperands(EchonetProperty[] properties) {
        for (EchonetProperty property : properties) {
            pcreate.writeOperand(property.getPropertyCode(), property.read());
        }
    }

    private void writeOperandsNull(List<EchonetProperty> properties) {
        for (EchonetProperty property : properties) {
            pcreate.writeOperand(property.getPropertyCode(), null);
        }
    }

    /**
     * Registers a notification event listener to be executed when a
     * notification of interest is received. The notifications for which the
     * listener will be executed is decided by the parameters passed.
     *
     * semantics: if any parameter is null then it is considered as "any" match.
     *
     * @param ip the IP address of a remote object (may be a unicast address,
     * may be a subnet address, or null for "any" address)
     * @param classGroupCode the group code of objects of interest (may be null)
     * @param classCode the class code of interest (may be null)
     * @param instanceCode the instance code of interest (may be null)
     * @param property the property code of interest (may be null)
     * @param listener the listener to be executed when a notification event
     * that matches the criteria specified above is received
     */
    public void registerForNotifications(InetAddress ip,
            Byte classGroupCode, Byte classCode, Byte instanceCode,
            Byte property, EchoEventListener listener) {

        this.nmanager.register(ip, classGroupCode, classCode, instanceCode, property, listener);
    }

    /**
     * Get a RemoteNodeDiscovery instance for performing network object
     * discovery. User the methods provided by @RemoteNodeDiscovery to discover
     * all the objects present in the network. Will take time since it
     * serializes requests.
     *
     * @return a new instance of RemoteNodeDiscovery
     * @see NodeDiscovery
     */
    public NodeDiscovery getNodeDiscovery() {
        return new NodeDiscovery(this);
    }
}
