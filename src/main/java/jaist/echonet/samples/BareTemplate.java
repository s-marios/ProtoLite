/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.samples;

import jaist.echonet.EchonetNode;
import jaist.echonet.gui.NetworkScanner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class BareTemplate {

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
            Logger.getLogger(NetworkScanner.class.getName()).log(Level.SEVERE, null, ex);
        }

        BareTemplate baseTemplate = new BareTemplate(address);
    }

    public BareTemplate(InetAddress address) {
        /* Initialize the echonet lite context with the desired address */
        context = new EchonetNode(address);
        
        /* start up the internal processing threads */
        Thread thread = context.start();
        
        /* Main processing thread is a daemon, call join in a loop to prevent exit */
        while (true) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(BareTemplate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
