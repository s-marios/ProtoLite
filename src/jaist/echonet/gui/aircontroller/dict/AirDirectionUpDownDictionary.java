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
public class AirDirectionUpDownDictionary extends EmptyMapDictionary{

    public AirDirectionUpDownDictionary() {
        map.put("UP", (byte) 0x41);
        map.put("DOWN", (byte) 0x42);
        map.put("MIDDLE", (byte) 0x43);
        map.put("UPPER MIDDLE", (byte) 0x44);
        map.put("LOWER MIDDLE", (byte) 0x45);
    }
    
    
}
