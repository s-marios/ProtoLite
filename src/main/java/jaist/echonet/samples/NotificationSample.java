package jaist.echonet.samples;

import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.util.Utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class NotificationSample implements EchoEventListener {

    public static void main(String args[]) {
        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(NotificationSample.class.getName()).log(Level.SEVERE, null, ex);
        }
        new NotificationSample(address);
    }

    public NotificationSample(InetAddress address) {
        context = new EchonetNode(address);
        context.registerForNotifications(null, null, null, null, null, this);
        Thread thread = context.start();

        while (true) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                //do nothing for the spurious wake up.
            }
        }
    }

    EchonetNode context;

    @Override
    public boolean processWriteEvent(EchonetProperty property) {
        throw new UnsupportedOperationException("This will never be used.");
    }

    @Override
    public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
        System.out.printf(String.format("Notification: %s:%s:%s:%s\n",
                robject.getQueryIp(),
                robject.getEOJ().toString(),
                Utils.toHexString(property.getPropertyCode()),
                Utils.toHexString(property.read()))
        );
        return true;
    }

    @Override
    public void processAnswer(EchonetAnswer answer) {
        throw new UnsupportedOperationException("This will never be used.");
    }

}
