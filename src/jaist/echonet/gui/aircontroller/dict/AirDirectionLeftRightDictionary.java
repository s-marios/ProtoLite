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
public class AirDirectionLeftRightDictionary extends EmptyMapDictionary{

    public AirDirectionLeftRightDictionary() {
        map.put("LEFT", 0x41);
        map.put("RIGHT", 0x42);
        map.put("CENTER", 0x43);
        map.put("LEFTRIGHT",0x44);
    }
    
    
}
