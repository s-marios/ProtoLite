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
public abstract class PropertyViewShort extends PropertyView implements ViewShort {

    public PropertyViewShort(PropertyHandler handler, String title) {
        super(handler, title);
    }
    
    protected short data;
    protected short olddata;

    @Override
    public short getShort() {
        return data;
    }

    @Override
    public void setShort(short num) {
        data = num;
        updateGUI();
    }

    @Override
    byte[] getRawData() {
        return handler.readData(this);
    }

    @Override
    void setRawData(byte[] data) {
        handler.writeData(this, data);
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
