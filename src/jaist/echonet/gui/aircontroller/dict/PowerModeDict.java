/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller.dict;

import jaist.echonet.gui.aircontroller.EmptyMapDictionary;

/**
 *
 * @author marios
 */
public class PowerModeDict extends EmptyMapDictionary{

    public PowerModeDict() {
        map.put("Normal Mode", (byte) 0x41);
        map.put("Power Mode", (byte) 0x42);
        map.put("Silent Mode",(byte) 0x43);
    }
    
    
}
