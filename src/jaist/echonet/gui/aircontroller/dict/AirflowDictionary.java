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
public class AirflowDictionary extends EmptyMapDictionary {
    
    public AirflowDictionary() {
        map.put("(0x31) LEAST", (byte) 0x31);
        map.put("(0x32) LESS", (byte) 0x32);
        map.put("(0x33) LESS", (byte) 0x33);
        map.put("(0x34) MEDIUM", (byte) 0x34);
        map.put("(0x35) MEDIUM", (byte) 0x35);
        map.put("(0x36) HIGH", (byte) 0x36);
        map.put("(0x37) HIGH", (byte) 0x37);
        map.put("(0x38) HIGHEST", (byte) 0x38);
        map.put("(0x41) AUTO", (byte) 0x41);
        map.put("(0x00) INVALID", (byte) 0x00);
    }
    
    
}
