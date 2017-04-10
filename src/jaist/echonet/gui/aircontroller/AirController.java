package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetDummyProperty;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.EchonetQuery;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.ServiceCode;
import jaist.echonet.config.AirconditionFullInfo;
import jaist.echonet.config.ControllerInfo;
import jaist.echonet.wrappers.AbstractObjectWrapper;
import jaist.echonet.wrappers.Aircondition;
import jaist.echonet.wrappers.ConcreteObjectWrapper;
import jaist.echonet.util.Utils;

/**
 * A software controller that controls air condition units. Dynamically generates
 * the GUI by probing for the supported properties. Each GUI control updates an
 * {@link AirconPropertyHandler} property handler, which uses a Visitor pattern
 * to make requests and update the GUI controls.
 * <p>
 * For each air condition device on the network, a new tab will be generated in
 * the GUI.
 * 
 * @author Sioutis Marios
 */
public class AirController {

    Class testclass = PropertyHandler.class;
    JFrame frame = new JFrame("Air condition controller");
    //RemoteEchonetObject aircon;
    //Aircondition wrapper;
    EchonetNode context = new EchonetNode();
    //Collection<PropertyHandler> handlers = new ArrayList<PropertyHandler>();
    JTabbedPane tabs = new JTabbedPane();
    Map<Byte, String> bsmap = new HashMap<Byte, String>();

    {
        bsmap.put((byte) 0x80, "Operational Status");
        bsmap.put((byte) 0x90, "ON timer setting");
        bsmap.put((byte) 0x92, "ON timer (Hours : minutes)");
        bsmap.put((byte) 0x93, "Position Info");
        bsmap.put((byte) 0x94, "OFF timer setting");
        bsmap.put((byte) 0x96, "OFF timer (Hours : minutes)");
        bsmap.put((byte) 0x97, "Current Time");
        bsmap.put((byte) 0x98, "Current Date");
        bsmap.put((byte) 0xA0, "Airflow Amount");
        bsmap.put((byte) 0xA1, "Auto Airflow");
        bsmap.put((byte) 0xA3, "Direction: Auto Swing");
        bsmap.put((byte) 0xA4, "Direction: Up/Down");
        bsmap.put((byte) 0xA5, "Direction: Left/Right");
        bsmap.put((byte) 0xAA, "Special Condition");
        bsmap.put((byte) 0xAB, "Unprivilleged Operation");
        bsmap.put((byte) 0xB0, "Operation Mode");
        bsmap.put((byte) 0xB1, "Auto Temperature");
        bsmap.put((byte) 0xB2, "Power Mode");
        bsmap.put((byte) 0xB3, "Temperature");
        bsmap.put((byte) 0xB4, "Relative Humidity");
        bsmap.put((byte) 0xB5, "Cooling Temperature");
        bsmap.put((byte) 0xB6, "Heating Temperature");
        bsmap.put((byte) 0xB7, "Dehumidfy Temperature");
        bsmap.put((byte) 0xB8, "Nominal Consumption Values");
        bsmap.put((byte) 0xB9, "Measured Consumption (A)");
        bsmap.put((byte) 0xBA, "Relative Room Humidity: ");
        bsmap.put((byte) 0xBB, "Room Temperature: ");
        bsmap.put((byte) 0xBC, "Remote Control Temperature: ");
        bsmap.put((byte) 0xBD, "Exhaust Temperature");
        bsmap.put((byte) 0xBE, "Outside Temperature");
        bsmap.put((byte) 0xBF, "Relative Temperature");
        bsmap.put((byte) 0xC0, "Ventilation: mode");
        bsmap.put((byte) 0xC1, "Humidifycation: mode");
        bsmap.put((byte) 0xC2, "Ventilation: amount");
        bsmap.put((byte) 0xC4, "Humidifycation: amount");
        bsmap.put((byte) 0xC7, "Purification mode");
        bsmap.put((byte) 0xC8, "Available Refresh methods");
        bsmap.put((byte) 0xC9, "Refresh mode");
        bsmap.put((byte) 0xCA, "Available self-cleaning methods");
        bsmap.put((byte) 0xCB, "0xCB");
        bsmap.put((byte) 0xCC, "0xCC");
        bsmap.put((byte) 0xCD, "0xCD");
        bsmap.put((byte) 0xCE, "0xCE");
        bsmap.put((byte) 0xCF, "0xCF");

    }

    class AirControllerInfo extends ControllerInfo {

        @Override
        protected void addProperties() {
            super.addProperties();
            add((byte) 0xf0, true, false, false, "Air condition controller object".getBytes());
        }
    }
    LocalEchonetObject controller = new LocalEchonetObject(new AirControllerInfo());
    LocalEchonetObject aircon = new LocalEchonetObject(new AirconditionFullInfo());

    AirController() {
        context.registerEchonetObject(aircon);
        context.registerEchonetObject(controller);
        context.start();

    }

    void startGUI() {
        frame.add(tabs);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        final AirController ac = new AirController();
        ac.scanNetwork();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ac.startGUI();
            }
        });

    }

    public void scanNetwork() {
        RemoteEchonetObject aircons = context.getRemoteObject(
                context.getGroupIP(),
                new EOJ((byte) 0x01, (byte) 0x30, (byte) 0x00));
        List<EchonetProperty> aList = new ArrayList<EchonetProperty>();
        aList.add(new EchonetDummyProperty((byte) 0x80));
        EchonetQuery query = context.makeQuery(controller, aircons, ServiceCode.Get, aList, null, null);

        EchonetAnswer nextAnswer = null;
        while ((nextAnswer = query.getNextAnswer()) != null) {
            scanAircon(nextAnswer.getResponder());
            //TODO scan and add to the cards
        }
    }

    private void scanAircon(RemoteEchonetObject responder) {
        ConcreteObjectWrapper wrapper = ConcreteObjectWrapper.createRemoteInstance(responder, controller);
        byte[] setProperties = AbstractObjectWrapper.propertyMap(wrapper.SetPropertyMap());
        byte[] getProperties = AbstractObjectWrapper.propertyMap(wrapper.GetPropertyMap());
        byte[] notifyProperties = AbstractObjectWrapper.propertyMap(wrapper.NotifyPropertyMap());

        String title = responder.getQueryIp().getHostAddress() + ":" + responder.getEOJ().toString();
        addPanel(title, generateTab(responder, setProperties, getProperties, notifyProperties));
    }

    private Component generateTab(RemoteEchonetObject robject, byte[] set, byte[] get, byte[] notify) {
        
        JPanel outerpanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        Map<Byte, PropertyHandler> handlers = new ConcurrentHashMap<Byte, PropertyHandler>();
        List<AirconPropertyHandler> writers = new ArrayList<AirconPropertyHandler>();
        List<AirconPropertyHandler> readers = new ArrayList<AirconPropertyHandler>();

        for (byte opcode : set) {
            String title = bsmap.get(opcode);
            if (title == null) {
                title = Utils.toHexString(opcode);
            }
            Aircondition wrapper = AbstractObjectWrapper.getRemoteInstance(Aircondition.class, robject, controller);
            AirconPropertyHandler handler = new AirconPropertyHandler(wrapper, opcode, title);
            writers.add(handler);
            handlers.put(opcode, handler);
        }

        for (byte opcode : get) {
            if (!handlers.containsKey(opcode)) {
                String title = bsmap.get(opcode);
                if (title == null) {
                    title = Utils.toHexString(opcode);
                }
                AirconPropertyHandler handler = new AirconPropertyHandler(AbstractObjectWrapper.getRemoteInstance(Aircondition.class, robject, controller), opcode, title);
                //    panel.add(handler.getView().getPanel());
                readers.add(handler);
                handlers.put(opcode,handler);
            }
        }

        //init top (status) panel
        AircondionControlData control = new AircondionControlData(context, robject, writers, readers);
        MainControlTab maincontrol = new MainControlTab(control);
        outerpanel.add(maincontrol.getPanel(),BorderLayout.NORTH);
        
        //Add the writeable properties
        panel.add(new JLabel("Writeable properties"));
        
        Collections.sort(writers);

        for (AirconPropertyHandler handler : writers) {
            panel.add(handler.getView().getPanel());
            handler.setOutput(maincontrol);
            handler.readProperty();
        }

        //panel.add(new JSeparator());
        panel.add(new JLabel("Readable properties (Setting the following properties will result in error)"));

        Collections.sort(readers);
        for(AirconPropertyHandler handler : readers){
            panel.add(handler.getView().getPanel());
            handler.setOutput(maincontrol);
            handler.readProperty();
        }
        /*
        for (byte opcode : notify) {
        }*/
        
        outerpanel.add(new JScrollPane(panel),BorderLayout.CENTER);
        return outerpanel;
    }

    private void addPanel(final String title, final Component component) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tabs.addTab(title, component);
            }
        });
    }
}
