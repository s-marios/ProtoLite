/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

/**
 *
 * @author marios
 */
public interface DropDownDictionary {
    
    public byte getByteValue(String key);
    public String[] getKeys();
    public String getKeyByValue(byte value);
}
