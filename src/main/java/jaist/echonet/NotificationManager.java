package jaist.echonet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.net.InetAddress;

/**
 * Part of {@link EchonetNode}, it is responsible for the management and
 * execution of notification event listeners. Multi-threaded manager.
 *
 * @author Sioutis Marios
 */
class NotificationManager {

    private final List<Map.Entry<int[], EchoEventListener>> runagainst = new ArrayList<>();

    public NotificationManager() {
    }

    private synchronized void checkAgainst(int[] matchme, RemoteEchonetObject robject, EchonetProperty property) {
        for (Map.Entry<int[], EchoEventListener> entry : runagainst) {
            if (matches(matchme, entry.getKey())) {
                entry.getValue().processNotificationEvent(robject, property);
            }
        }
    }

    private boolean matches(int[] source, int[] regex) {
        if (source.length != regex.length) {
            return false;
        }
        for (int i = 0; i < source.length; i++) {
            if (regex[i] == -1) {
                continue;
            }
            if (regex[i] != source[i]) {
                return false;
            }
        }
        return true;
    }

    public void invokeNotificationManager(final RemoteEchonetObject robject, final List<EchonetProperty> properties) {
        final NotificationManager nm = this;
        Thread t = new Thread() {
            @Override
            public void run() {
                nm.invoke(robject, properties);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void invoke(RemoteEchonetObject robject, List<EchonetProperty> properties) {

        for (EchonetProperty property : properties) {
            if (property.isEmpty()) {
                continue;
            }
            InetAddress ip = robject.getQueryIp();
            EOJ eoj = robject.getEOJ();
            byte classGroupCode = eoj.getClassGroupCode();
            byte classCode = eoj.getClassCode();
            byte instanceCode = eoj.getInstanceCode();
            byte propertyCode = property.getPropertyCode();
            int[] matchme = makeMatch(ip, classGroupCode, classCode, instanceCode, propertyCode);
            //check against registered stuff.
            this.checkAgainst(matchme, robject, property);
        }
    }

    public synchronized void register(InetAddress ip, Byte classGroupCode, Byte classCode, Byte instanceCode, Byte property, EchoEventListener listener) {
        int[] match = makeMatch(ip, classGroupCode, classCode, instanceCode, property);
        runagainst.add(new AbstractMap.SimpleEntry(match, listener));
    }

    protected int byteToIntMatch(Byte b) {
        if (b != null) {
            return (255 & b.intValue());
        } else {
            return -1;
        }
    }

    protected int[] makeMatch(InetAddress ip, Byte classGroupCode, Byte classCode, Byte instanceCode, Byte property) {
        //match for up to IPv6 length addresses
        //IPv6 (16) + EOJ(3) + property code (1)
        int[] match = new int[20];
        int i = 0;

        for (int j = 0; j < match.length; j++) {
            match[j] = -1;
        }

        if (ip != null) {
            for (byte b : ip.getAddress()) {
                match[i++] = byteToIntMatch(b);
            }
        }

        //jump to final four bytes, eoj/property at the end
        i = 16;
        match[i++] = byteToIntMatch(classGroupCode);
        match[i++] = byteToIntMatch(classCode);
        match[i++] = byteToIntMatch(instanceCode);
        match[i++] = byteToIntMatch(property);
        return match;
    }
}
