/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import jaist.echonet.wrappers.AbstractObjectWrapper;

/**
 *
 * @author marios
 */
public class StringHandler extends AbstractPropertyHandler implements PropertyHandler {

    PropertyViewString stringview;
    String data;
/*
    public StringHandler(AbstractObjectWrapper wrapper, byte opcode) {
        super(wrapper, opcode);
        this.opcode = opcode;
        stringview = new PropertyViewString(this);
    }
*/
    public StringHandler(AbstractObjectWrapper wrapper, byte opcode, String title) {
        super(wrapper, opcode);
        this.opcode = opcode;
        stringview = new PropertyViewString(this,title);
    }

    @Override
    public boolean readProperty() {
        byte [] raw = object.readProperty(opcode);
        if(raw == null || raw.length == 0)
            return false;
        data = new String(raw);
        stringview.setString(data);
        return true;
    }

    @Override
    public boolean writeProperty() {
        return object.writeProperty(opcode, stringview.getString().getBytes());
    }

    @Override
    public PropertyView getView() {
        return stringview;
    }

    @Override
    public byte[] readData(ViewByte aView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeData(ViewByte aView, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] readData(ViewByteArray aView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] readData(ViewInt aView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] readData(ViewShort aView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] readData(ViewString aView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeData(ViewByteArray aView, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeData(ViewInt aView, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeData(ViewShort aView, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeData(ViewString aView, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHandlerData(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
