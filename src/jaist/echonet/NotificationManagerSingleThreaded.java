package jaist.echonet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A single threaded implementation of a notification manager
 * @author Sioutis Marios
 */
class NotificationManagerSingleThreaded extends NotificationManager {

    private final List<Map.Entry<RemoteEchonetObject, List<EchonetProperty>>> jobs = new ArrayList();
    private static NotificationManagerSingleThreaded singleton = null;

    public static NotificationManager get() {
        if (singleton == null) {
            singleton = new NotificationManagerSingleThreaded();
            singleton.start();
        }
        return singleton;
    }

    protected NotificationManagerSingleThreaded() {
    }
    
    @Override
    public void invokeNotificationManager(RemoteEchonetObject robject, List<EchonetProperty> properties) {
        synchronized (jobs) {
            jobs.add(new AbstractMap.SimpleEntry(robject, properties));
            jobs.notify();
        }
    }

    private void startProcessing() {
        Entry<RemoteEchonetObject, List<EchonetProperty>> job = null;
        while (true) {
            synchronized (jobs) {
                while (jobs.isEmpty()) {
                    try {
                        jobs.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NotificationManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                job = jobs.remove(0);
            }
            //invoke must be out of the synchronized block.
            invoke(job.getKey(), job.getValue());
        }
    }

    private void start() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                startProcessing();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
