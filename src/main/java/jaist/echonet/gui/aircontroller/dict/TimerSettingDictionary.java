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
public class TimerSettingDictionary extends EmptyMapDictionary {

    public TimerSettingDictionary() {
        map.put("Relative and Absolute", (byte) 0x41);
        map.put("Disabled", (byte) 0x42);
        map.put("Absolute", (byte) 0x43);
        map.put("Relative", (byte) 0x44);
    }
    
}
