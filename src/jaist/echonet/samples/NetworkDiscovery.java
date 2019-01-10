/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.samples;

import jaist.echonet.EchonetNode;
import jaist.echonet.NodeDiscovery;
import jaist.echonet.RemoteEchonetObject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class NetworkDiscovery {
    /**
     * The main ECHONET Lite context for this application
     */
    EchonetNode context;

    public static void main(String[] args) {
        /*
         * Look up the invocation arguments for the desired IP address, use 
         * whatever is the default otherwise.
         */
        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }

        NetworkDiscovery baseTemplate = new NetworkDiscovery(address);
    }

    public NetworkDiscovery(InetAddress address) {
        /* Initialize the echonet lite context with the desired address */
        context = new EchonetNode(address);
        
        /* start up the internal processing threads */
        Thread thread = context.start();
        
        NodeDiscovery nodeDiscovery = context.getNodeDiscovery();

        long start = System.currentTimeMillis();
        Date date = new Date(start);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss: ");
        System.out.println(sdf.format(date) + "Starting discovery...");
        
        //perform discovery.
        for (RemoteEchonetObject robject : nodeDiscovery.discoverAllObjectsBlocking()){
            System.out.printf("IP: %-15s, EOJ: %s \n", robject.getQueryIp(), robject.getEOJ().toString());
        }
        
        long end = System.currentTimeMillis();
        date = new Date(end);
        System.out.println(sdf.format(date) + "Discovery end.");
        System.out.printf("Total time (ms): %d\n", end-start);
        
    }
}
