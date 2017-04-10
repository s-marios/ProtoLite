/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author marios
 */
public abstract class PropertyViewByteArray extends PropertyView implements ViewByteArray{

    public PropertyViewByteArray(PropertyHandler handler, String title) {
        super(handler, title);
    }

    protected byte[] data;
    protected byte[] olddata;
    
    
    @Override
    byte[] getRawData() {
        return data;
    }

    @Override
    void setRawData(byte[] data) {
        this.setBytes(data);
    }

    @Override
    public void setBytes(byte[] data) {
        this.data = data;
        updateGUI();
    }

    @Override
    public byte[] getBytes() {
        return data;
    }
    
    @Override
    void backup(){
        olddata = data;
    }
    
    @Override
    void restore(){
        data = olddata;
        updateControls();
    }
    
}
