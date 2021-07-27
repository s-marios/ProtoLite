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
public class AirSwingDictionary extends EmptyMapDictionary {

    public AirSwingDictionary() {
        map.put("OFF", 0x31);
        map.put("VERTICAL", 0x41);
        map.put("HORIZONTAL", 0x42);
        map.put("BOTH",0x43);
    }
}
