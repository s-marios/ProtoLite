/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller.dict;

import jaist.echonet.gui.aircontroller.DropDownDictionary;
import jaist.echonet.gui.aircontroller.EmptyMapDictionary;

/**
 *
 * @author marios
 */
public class OnOffDictionary extends EmptyMapDictionary{

    public OnOffDictionary() {
        map.put("ON", (byte) 0x30);
        map.put("OFF",(byte) 0x31);
    }
    
}
