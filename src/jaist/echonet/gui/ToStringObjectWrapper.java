/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
public class ToStringObjectWrapper {

    public AbstractEchonetObject object;
    public ToStringObjectWrapper(AbstractEchonetObject object) {
        this.object = object;
    }
    
    @Override
    public String toString(){
        return ("Object: " + "0x" + object.getEOJ());
    }
    
    public AbstractEchonetObject getObject(){ return object;}
    
}
