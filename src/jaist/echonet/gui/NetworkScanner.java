package jaist.echonet.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import jaist.echonet.EOJ;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetDummyProperty;
import jaist.echonet.EchonetQuery;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.EchonetProtocol;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.ServiceCode;
import jaist.echonet.wrappers.AbstractObjectWrapper;
import jaist.echonet.wrappers.Aircondition;
import jaist.echonet.wrappers.ConcreteObjectWrapper;
import jaist.echonet.wrappers.EmergencyButton;
import jaist.echonet.sampledevices.LiveTemperatureSensor;
import jaist.echonet.config.AirconditionFullInfo;
import jaist.echonet.config.DeviceInfo;
import jaist.echonet.config.TemperatureSensorInfo;
import jaist.echonet.config.EmergencyButtonInfo;
import jaist.echonet.util.Utils;

/**
 *
 * @author Sioutis Marios
 */
public class NetworkScanner implements EchoEventListener, TreeSelectionListener {

    public EchonetNode echonode;
    private MyTreeModel mymodel = new MyTreeModel();
    private JTree jtree;
    private JScrollPane scrollpaneleft, scrollpaneright;
    private JFrame frame = new JFrame("test app");
    private LocalEchonetObject controler;
    private JSplitPane splitpane;
    private JPanel cards;
    private ObjectToSomething cardholder = new ObjectToSomething(this);
    //listeners
    private MouseAdapter mousetablelistener;
    private MouseListener mousetreelistener;
    private ActionListener refreshListener;
    //the above are like current context
    //to touch them you need the current object lock.
    private AbstractEchonetObject currentobject;
    private final Object currentlock = new Object();
    private int currentopcode = 0;
    private final int SCANINIT = 1;
    private final int SCANLISTEN = 2;
    private int status = SCANINIT;

    public void setCurrentOpcode(int opcode) {
        synchronized (currentlock) {
            currentopcode = opcode;
        }
    }

    private int getCurrentOpcode() {
        return currentopcode;
    }

    private void setCurrentObject(AbstractEchonetObject currentobj) {
        synchronized (currentlock) {
            currentobject = currentobj;
        }
    }

    private AbstractEchonetObject getCurrentObject() {
        return currentobject;
    }

    /**
     * The main method for the network scanner program. 
     * 
     * @param args a single argument (if supplied) must be an IPv4 or IPv6
     * address that will be used (as a hint) during the initialization of the
     * network.
     */
    public static void main(String[] args) {

        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkScanner.class.getName()).log(Level.SEVERE, null, ex);
        }


        NetworkScanner app = new NetworkScanner(address);
        EchonetNode node = app.echonode;

        node.registerForNotifications(null, null, null, null, null, app);
        //Thread t = node.start();


        LocalEchonetObject aircon = new LocalEchonetObject(new AirconditionFullInfo());
        Aircondition aircon2 = Aircondition.createLocalInstance(aircon);

        
        LocalEchonetObject tsensor = new LocalEchonetObject(new TemperatureSensorInfo());
        node.registerEchonetObject(tsensor);
        LiveTemperatureSensor tt = LiveTemperatureSensor.createLocalInstance(tsensor);
        tt.start();

        tsensor = new LocalEchonetObject(new TemperatureSensorInfo());
        node.registerEchonetObject(tsensor);
        tt = LiveTemperatureSensor.createLocalInstance(tsensor);
        
        tt.start();
        
        
        LocalEchonetObject ebutton = new LocalEchonetObject(new EmergencyButtonInfo());
        node.registerEchonetObject(ebutton);

        ebutton = new LocalEchonetObject(new EmergencyButtonInfo());
        node.registerEchonetObject(ebutton);

        Thread t = node.start();
        
        app.startScan();
    }

    public NetworkScanner(InetAddress address) {
        echonode = new EchonetNode(address);

        EmergencyButton ebutton = AbstractObjectWrapper.createAndRegisterLocalInstance(
                EmergencyButton.class, new EmergencyButtonInfo(), echonode);

        LocalEchonetObject local;

        local = new LocalEchonetObject(new AirconditionFullInfo());
        echonode.registerEchonetObject(local);

        local = new LocalEchonetObject(new AirconditionFullInfo());
        echonode.registerEchonetObject(local);

        local = new LocalEchonetObject(new AirconditionFullInfo());
        echonode.registerEchonetObject(local);

        controler = new LocalEchonetObject(new DeviceInfo() {

            @Override
            protected void setObjectClass() {
                setClassEOJ(new EOJ((byte) 0x05, (byte) 0xFF, (byte) 0x01));
            }

            @Override
            protected void addProperties() {
                String description = "Marios' object scanner";
                add((byte) 0xF0, true, false, false, description.getBytes());
            }
        });
        echonode.registerEchonetObject(controler);

        //init the Refresh listener
        refreshListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                refreshValue();
            }
        };

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setupGUI();
            }
        });

    }

    private void setupGUI() {
        //setup the tree
        jtree = new JTree(mymodel.getModel());
        jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtree.addTreeSelectionListener(this);
        jtree.addMouseListener(ma);
        //jtree.addMouseListener(getTreeMouseListener());

        //setup the card layout panel
        cards = new JPanel(new CardLayout());

        scrollpaneleft = new JScrollPane(jtree);
        scrollpaneright = new JScrollPane(cards);
        cards.add(new JLabel("test"), "test");
        //scrollpaneright.setLayout(new CardLayout());

        Dimension minimumsize1 = new Dimension(200, 200);
        Dimension minimumsize2 = new Dimension(400, 200);
        scrollpaneleft.setMinimumSize(minimumsize1);
        scrollpaneright.setMinimumSize(minimumsize2);


        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollpaneleft, scrollpaneright);
        splitpane.setDividerLocation(200);

        frame.setContentPane(splitpane);
        frame.setMinimumSize(new Dimension(620, 400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    protected void startScan() {
        scanNode(echonode.getGroupIP());
        //set init scan is over.
        this.status = SCANLISTEN;
    }

    protected void scanNode(InetAddress nodeaddress) {
        RemoteEchonetObject rnode = echonode.getRemoteObject(nodeaddress, EchonetProtocol.NODEPROFILEOJ);
        EchonetQuery responses = echonode.makeQuery(controler, rnode, ServiceCode.Get, Collections.singletonList((EchonetProperty) new EchonetDummyProperty((byte) 0xD6)), null, null);
        EchonetAnswer nextAnswer;
        System.out.println("xxx query ip: " + rnode.getQueryIp());
        while ((nextAnswer = responses.getNextAnswer()) != null) {

            //first add the model of the remote node to the data model
            //and scan it
            mymodel.addEchonetObject(nextAnswer.getResponder());
            defferedScanObject(nextAnswer.getResponder());


            for (EchonetProperty property : nextAnswer.getProperties()) {
                //now try to get its instance list
                //use its wrapper to get it as a byte[]
                if (property.getPropertyCode() == (byte) 0xD6) {
                    byte[] instances = property.read();
                    if (instances != null && instances.length > 0) {
                        for (int i = 0; i < instances[0]; i++) {
                            //read the instances one by one and add them to the list
                            //first is [1],[2],[3]    
                            EOJ remoteeoj = new EOJ(instances[i * 3 + 1],
                                    instances[i * 3 + 2],
                                    instances[i * 3 + 3]);
                            RemoteEchonetObject remote = echonode.getRemoteObject(nextAnswer.getResponder().getQueryIp(), remoteeoj);
                            mymodel.addEchonetObject(remote);
                            //JComponent comp = cardholder.getSimpleComponent(remote);
                            defferedScanObject(echonode.getRemoteObject(nextAnswer.getResponder().getQueryIp(), remoteeoj));
                        }
                    }
                }
            }
        }
    }

    protected void defferedScanObject(final RemoteEchonetObject robject) {
        Runnable scanit = new Runnable() {

            @Override
            public void run() {
                scanObject(robject);
            }
        };

        new Thread(scanit).start();
    }

    protected void scanObject(RemoteEchonetObject robject) {
        //synchronized(robject) { //god forgive me...
        //NodeProfileObject profile = this.echonode.getNodeProfileObject();
        ConcreteObjectWrapper wrappedobject = ConcreteObjectWrapper.createRemoteInstance(robject, controler);
        //obtain a table model
        DevicePropertiesTableModel tablemodel = cardholder.getDataModel(robject);
        //create a tablemodel
        if (tablemodel == null) {
            tablemodel = new DevicePropertiesTableModel(this);
            //force the creation of a control panel, we need the table beforehand
            //that table gets added to the cardholder.
            cardholder.getTableComponent(robject, tablemodel, getTableMouseListener());
        }
        scanProperties(wrappedobject, tablemodel, AccessRule.WRITEABLE);
        scanProperties(wrappedobject, tablemodel, AccessRule.READABLE);
        scanProperties(wrappedobject, tablemodel, AccessRule.NOTIFIES);
        //}
    }

    protected MouseListener getTableMouseListener() {
        if (mousetablelistener == null) {
            mousetablelistener = new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    Component comp = me.getComponent();
                    //we will register this listener for tables
                    //check if we indeed have a table
                    if (me.getButton() == MouseEvent.BUTTON3)//check for the right click.
                    {
                        if (comp instanceof JTable) {
                            JTable table = (JTable) comp;
                            //now proceed to find which row was clicked.
                            int row = table.rowAtPoint(me.getPoint());
                            if (row >= 0 && row < table.getRowCount()) {
                                //select the row we right-clicked
                                table.setRowSelectionInterval(row, row);
                                System.out.println("row : " + row);
                                //set the current opcode
                                DevicePropertiesTableModel datamodel = cardholder.getDataModel(currentobject);
                                setCurrentOpcode(datamodel.getOpcode(row));


                                //popup stuff
                                JPopupMenu popup = new JPopupMenu();
                                JMenuItem refresh = new JMenuItem("Refresh");
                                refresh.addActionListener(refreshListener);
                                popup.add(refresh);
                                popup.show(comp, me.getX(), me.getY());
                            }
                        }
                    }

                }
            };
        }
        return mousetablelistener;
    }

    private enum AccessRule {

        READABLE,
        WRITEABLE,
        NOTIFIES;
    }

    private void scanProperties(ConcreteObjectWrapper wrappedobject, DevicePropertiesTableModel tablemodel, AccessRule rule) {
        //get the writeable properties.
        byte[] raw;
        int access = 0;
        switch (rule) {
            case READABLE:
                raw = wrappedobject.GetPropertyMap();
                access = 1;
                break;
            case WRITEABLE:
                raw = wrappedobject.SetPropertyMap();
                access = 2;
                break;
            case NOTIFIES:
                raw = wrappedobject.NotifyPropertyMap();
                access = 4;
                break;
            default:
                return;
        }
        byte[] properties = AbstractObjectWrapper.propertyMap(raw);
        if (properties != null && properties.length > 0) {
            List<TableProperty> proplist = new ArrayList<TableProperty>();
            for (byte property : properties) {
                byte[] data = wrappedobject.readProperty(property);
                if (data != null && data.length > 0) {
                    //add them to the table model.

                    proplist.add(new TableProperty(property, access, data));

                }
            }
            tablemodel.addTableProperties(proplist);

            if(rule == AccessRule.NOTIFIES){
                this.echonode.makeQuery(controler, wrappedobject.getEchonetObject(), ServiceCode.INF_REQ, EchonetDummyProperty.getDummies(properties), null, this);
            }

        }
    }

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {

        if (!mymodel.nodeExists(robject) || property.getPropertyCode() == 0xD5) {
            //the node we just got notification from, does not exist.
            //add him and scan him promptly.
            //...also, ignore the notification, we'll get a more recent value 
            //during scan.
            scanNode(robject.getQueryIp());
            System.out.println("does not exist");
        } else {
            //just process the notification
            //1. bring up the proper table model.
            DevicePropertiesTableModel dataModel = this.cardholder.getDataModel(robject);
            dataModel.addTableProperties(Collections.singletonList(
                    new TableProperty(property.getPropertyCode(), 4, property.read())));
        }
        return true;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
        System.out.println(node.getUserObject().toString());
        if (node.isLeaf() && node.getUserObject() instanceof ToStringObjectWrapper) {
            ToStringObjectWrapper wrapped = (ToStringObjectWrapper) node.getUserObject();
            //set the current echonet object
            setCurrentObject(wrapped.getObject());

            JPanel currentcard = cardholder.getPanelComponent(wrapped.getObject());
            CardLayout cl = (CardLayout) cards.getLayout();
            System.out.println("show key: " + wrapped.getObject().toString());

            cards.add(currentcard, wrapped.getObject().toString());
            cl.show(cards, wrapped.getObject().toString());
            //cards.invalidate();
        }
    }

    protected void refreshValue() {
        //refreshes a value.
        synchronized (currentlock) {
            ConcreteObjectWrapper wrapped = ConcreteObjectWrapper.createRemoteInstance(
                    echonode.getRemoteObject(currentobject.getQueryIp(), currentobject.getEOJ()),
                    controler);

            byte[] data = wrapped.readProperty((byte) currentopcode);
            if (data != null && data.length > 0) {
                DevicePropertiesTableModel dataModel = cardholder.getDataModel(currentobject);
                dataModel.addTableProperties(Collections.singletonList(new TableProperty(currentopcode, 1, data)));
                System.out.println("Refresh data: " + Utils.toHexString(data));
            } else {
                System.out.println("The query died");
            }
        }
    }

    protected boolean requestWrite(byte[] data) {
        synchronized (currentlock) {
            ConcreteObjectWrapper wrapped = ConcreteObjectWrapper.createRemoteInstance(
                    echonode.getRemoteObject(currentobject.getQueryIp(), currentobject.getEOJ()),
                    controler);
            boolean error = wrapped.writeProperty((byte) currentopcode, data);
            if (error) {
                System.out.println("Unable to write property");
                return true;
            } else {
                DevicePropertiesTableModel dataModel = cardholder.getDataModel(currentobject);
                dataModel.addTableProperties(Collections.singletonList(new TableProperty(currentopcode, 2, data)));
                return false;
            }
        }
    }
    MouseAdapter ma = new MouseAdapter() {

        @Override
        public void mouseReleased(MouseEvent me) {
            Component comp = me.getComponent();
            //we will register this listener for tables
            //check if we indeed have a table
            if (me.getButton() == MouseEvent.BUTTON3)//check for the right click.
            {
                if (comp instanceof JTree) {
                    JTree tree = (JTree) comp;

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    System.out.println(node.getUserObject().toString());
                    if (node.isLeaf() && node.getUserObject() instanceof ToStringObjectWrapper) {
                        final ToStringObjectWrapper wrapped = (ToStringObjectWrapper) node.getUserObject();

                        AbstractEchonetObject obj = wrapped.getObject();
                        final RemoteEchonetObject robject = echonode.getRemoteObject(obj.getQueryIp(), obj.getEOJ());

                        //popup stuff
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem refresh = new JMenuItem("Rescan selected");
                        refresh.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                defferedScanObject(robject);

                                //JPanel currentcard = cardholder.getPanelComponent(wrapped.getObject());
                                CardLayout cl = (CardLayout) cards.getLayout();
                                cl.show(cards, wrapped.getObject().toString());


                            }
                        });
                        popup.add(refresh);
                        popup.show(comp, me.getX(), me.getY());
                    }
                }
            }
        }
    };
}