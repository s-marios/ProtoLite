/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jaist.echonet.adapter;

import jaist.echonet.EOJ;
import jaist.echonet.EchonetProperty;
import jaist.echonet.LocalEchonetObject;
import java.util.Collection;

/**
 *
 * @author haha
 */
class MiddlewareObject extends LocalEchonetObject{
    private final MiddlewareCommunicator comm;

    public MiddlewareObject(EOJ eoj, Collection<MiddlewareProperty> properties, MiddlewareCommunicator comm) {
        super(eoj, properties);
        this.comm = comm;
        for (MiddlewareProperty property : properties){
            property.registerParent(this);
        }
    }
    
    public MiddlewareCommunicator getCommunicator(){
        return comm;
    }
    
}
