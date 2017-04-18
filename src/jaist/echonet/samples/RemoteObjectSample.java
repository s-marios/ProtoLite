/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.samples;

import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.Logging;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.gui.NetworkScanner;
import jaist.echonet.util.Utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class RemoteObjectSample {

    EchonetNode context;
    RemoteEchonetObject robject;
    List<Byte> propertyCodes;
    Thread t;

    public RemoteObjectSample(String[] args) {

        if (args.length < 4) {
            System.out.println("Uasege: REMOTEIPADDRESS EOJ PROPCODE [PROPCODE ...] ");
            System.exit(0);
        }

        String addressstring = args[0];
        String eojstring = args[1];
        EOJ eoj = new EOJ(eojstring);
        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(addressstring);
            }
        } catch (UnknownHostException ex) {
            Logging.getLogger().log(Level.SEVERE, "Failure to parse address", ex);
        }

        //node startup
        context = new EchonetNode();
        //get remote object
        robject = context.getRemoteObject(address, eoj);
        propertyCodes = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            propertyCodes.add(Integer.decode(args[i]).byteValue());
        }

        for (String arg : args) {
            System.out.println("arg: " + arg);
        }

        t = context.start();
    }

    public static void main(String args[]) throws InterruptedException {

        RemoteObjectSample ros = new RemoteObjectSample(args);
        ros.startPolling();
    }

    private void startPolling() throws InterruptedException {

        //Initial remote object list probing.
        System.out.println("Update remote object list");
        if (robject.updatePropertyList() == false) {
            System.out.println("Updating the list of properties for the remote object failed.");
            System.exit(1);
        }

        for (EchonetProperty property : robject.getPropertyList()) {
            System.out.println("Property: " + Utils.toHexString(property.getPropertyCode())
                    + property.isReadable()
                    + property.isWriteable()
                    + property.doesNotify()
            );
        }

        for (EchonetProperty property : robject.getPropertyList()) {
            System.out.println("Property (data): " + property.getPropertyCode()
                    + "  "
                    + Utils.toHexString(property.read())
            );
        }

        //polling session
        System.out.println("Entering polling session");
        while (true) {

            for (Byte propcode : propertyCodes) {
                robject.readProperty(propcode);
                byte[] data = robject.readProperty(propcode);
                System.out.println("propcode: " + Utils.toHexString(propcode) + " data: " + Utils.toHexString(data));
            }

//            for (EchonetProperty prop : robject.getPropertyList()) {
//                System.out.println("propcode: " + Utils.toHexString(prop.getPropertyCode()));
//            }
            Thread.currentThread().sleep(3000);
        }

    }

}
