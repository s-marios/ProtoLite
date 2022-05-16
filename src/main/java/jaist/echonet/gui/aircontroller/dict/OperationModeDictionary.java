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
public class OperationModeDictionary extends EmptyMapDictionary{

    public OperationModeDictionary() {
        map.put("AUTO", (byte) 0x41);
        map.put("COOLING", (byte) 0x42);
        map.put("HEATING", (byte) 0x43);
        map.put("DEHUMIDIFY", (byte) 0x44);
        map.put("FAN", (byte) 0x45);
        map.put("OTHER", (byte) 0x40);
    }
    
}
