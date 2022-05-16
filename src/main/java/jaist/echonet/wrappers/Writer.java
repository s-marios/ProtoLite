/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchonetProperty;

/**
 * Interface for the writers
 * 
 * @author Sioutis Marios
 */
interface Writer {
    /**
     * Attempts a write 
     * 
     * @return If there was an error (true). If everything is ok it 
     * should return false;
     * 
     */
     boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty property);
    /**
     * Attempts a write 
     * 
     * @return If there was an error (true). If everything is ok it 
     * should return false;
     * 
     */
     boolean writeProperty(AbstractEchonetObject whoasks, byte propertycode, byte[] data);
    
}
