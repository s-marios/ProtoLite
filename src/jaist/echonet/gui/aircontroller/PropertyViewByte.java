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
public abstract class PropertyViewByte extends PropertyView implements ViewByte {

    protected byte data;
    protected byte olddata;

    public PropertyViewByte(PropertyHandler handler,String title) {
        super(handler,title);
    }

    @Override
    public byte getData() {
        return data;
    }

    @Override
    public void setData(byte data) {
        this.data = data;
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
