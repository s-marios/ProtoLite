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
public class AutoManualDictionary extends EmptyMapDictionary{

    public AutoManualDictionary() {
        map.put("AUTO", (byte) 0x41);
        map.put("MANUAL", (byte) 0x42);
    }
    
    
}
