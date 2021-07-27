/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marios
 */
public class EmptyMapDictionary implements DropDownDictionary {

    String[] strings = new String[1];
    protected HashMap<String, Object> map = new HashMap<String, Object>();

    public EmptyMapDictionary() {
       // map.put("INVALID", 0x00);
    }
    
    

    @Override
    public byte getByteValue(String key) {
        Object retval = map.get(key);
        if (retval instanceof Byte) {
            return (Byte) retval;
        } else {
            return Byte.MIN_VALUE;
        }
    }

    @Override
    public String[] getKeys() {
        return map.keySet().toArray(strings);

    }

    public Object[] getValues() {
        return map.values().toArray();
    }

    @Override
    public String getKeyByValue(byte value) {
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (entry.getValue() == (Byte) value) {
                return entry.getKey();
            }
        }
        return "N/A";
    }
}
