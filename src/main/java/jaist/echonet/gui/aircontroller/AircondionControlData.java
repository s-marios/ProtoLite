/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.NotificationEventAdapter;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.WriteEventAdapter;

/**
 *
 * @author marios
 */
public class AircondionControlData {

    private List<AirconPropertyHandler> writers;
    private List<AirconPropertyHandler> readers;
    private Map<Byte, AirconPropertyHandler> hmap = new ConcurrentHashMap<Byte, AirconPropertyHandler>();
    private EchonetNode context;
    private RemoteEchonetObject raircon;
    private NotificationEventAdapter notificationlistener = new NotificationEventAdapter() {

        @Override
        public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
            return processNotification(property);
        }
    };

    public AircondionControlData(EchonetNode context, RemoteEchonetObject raircon, List<AirconPropertyHandler> writers, List<AirconPropertyHandler> readers) {
        this.context = context;
        this.raircon = raircon;
        this.writers = writers;
        this.readers = readers;
        prepareMap();
        registerNotifications();
    }

    private void prepareMap() {
        for (AirconPropertyHandler handler : readers) {
            hmap.put(handler.getOpCode(), handler);
        }

        for (AirconPropertyHandler handler : writers) {
            hmap.put(handler.getOpCode(), handler);
        }
    }

    protected boolean processNotification(EchonetProperty property) {
    
        AirconPropertyHandler handler = hmap.get(property.getPropertyCode());
        if (handler == null) {
            return false;
        }
        //handler.setHandlerData(property.read());
        return true;
      
     }

    private void registerNotifications() {
        EOJ reoj = raircon.getEOJ();
        context.registerForNotifications(raircon.getQueryIp(), reoj.getClassGroupCode(), reoj.getClassCode(), reoj.getInstanceCode(), null, notificationlistener);
    }

    public void refreshAll() {
        for (AirconPropertyHandler handler : hmap.values()) {
            handler.readProperty();
        }
    }

    public void refreshProperty(byte propertycode) {
        AirconPropertyHandler handler = hmap.get(propertycode);
        if (handler != null) {
            handler.readProperty();
        }
    }
}
