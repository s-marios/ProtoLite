/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.adapter;

import jaist.echonet.EchonetNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class MiddlewareAdapter {

    private MiddlewareCommunicator comm;

    public static void main(String[] args) {
        InetAddress address = null;
        int port = 33333;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            } else {
                address = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(MiddlewareAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        new MiddlewareAdapter(address, port);
    }

    public MiddlewareAdapter(InetAddress address, int port) {
        comm = new MiddlewareCommunicator(address, port);
        comm.start();
        //this.append("Node Using address: " + context.getIP().getHostAddress() + "\n");
    }

}
