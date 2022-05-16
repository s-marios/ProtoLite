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
public class AirDirectionAutoDictionary extends EmptyMapDictionary{

    public AirDirectionAutoDictionary() {
        map.put("AUTO", (byte) 0x41);
        map.put("MANUAL", (byte) 0x42);
        map.put("AUTO: VERTICAL", (byte) 0x43);
        map.put("AUTO: SIDEWAYS", (byte) 0x44);
    }
    
    
}
