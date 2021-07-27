/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import jaist.echonet.gui.aircontroller.dict.*;
import jaist.echonet.wrappers.AbstractObjectWrapper;
import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
public class AirconPropertyHandler extends AbstractPropertyHandler {

    protected PropertyView view;

    public AirconPropertyHandler(AbstractObjectWrapper wrapper, byte opcode, String title) {
        super(wrapper, opcode);
        switch (opcode) {
            case (byte) 0x80:
                view = new ComboBoxByteView(this, title, new OnOffDictionary());
                break;
            case (byte) 0xB0:
                view = new ComboBoxByteView(this, title, new OperationModeDictionary());
                break;
            case (byte) 0xB2:
                view = new ComboBoxByteView(this, title, new PowerModeDict());
                break;
            case (byte) 0xB1:
                view = new ComboBoxByteView(this, title, new AutoManualDictionary());
                break;
            case (byte) 0xB3:
            case (byte) 0xB5:
            case (byte) 0xB6:
            case (byte) 0xB7:
                view = new TemperatureView(this, title);
                break;
            case (byte) 0xBA:
            case (byte) 0xBC:
                view = new NumericByteView(this, title);
                break;
            case (byte) 0xBB:
            case (byte) 0xBD:
            case (byte) 0xBE:
                view = new NegativeNumericByteView(this, title);
                break;
            case (byte) 0xBF:
                view = new TemperatureView(this, title);
            case (byte) 0xA0:
                view = new ComboBoxByteView(this, title, new AirflowDictionary());
                break;
            case (byte) 0xA1:
                view = new ComboBoxByteView(this, title, new AirDirectionAutoDictionary());
                break;
            case (byte) 0xA3:
                view = new ComboBoxByteView(this, title, new AirSwingDictionary() );
                break;
            case (byte) 0xA4:
                view = new ComboBoxByteView(this, title, new AirDirectionUpDownDictionary());
                break;
            case (byte) 0xA5:
                view = new ComboBoxByteView(this, title, new AirDirectionLeftRightDictionary());
                break;
            
            case (byte) 0x92:
            case (byte) 0x96:
                view = new HourMinuteTimer(this, title);
                break;
            case (byte) 0x90:
            case (byte) 0x94:
                view = new ComboBoxByteView(this, title, new TimerSettingDictionary());
                break;
            default:
                view = new GenericByteArrayView(this, title);
        }

        view.setTitle(title);
    }

    @Override
    public boolean readProperty() {
        byte[] raw = object.readProperty(opcode);
        setHandlerData(raw);
        return true;
    }

    @Override
    public void setHandlerData(byte[] data) {
        this.view.setRawData(data);
    }

    @Override
    public boolean writeProperty() {
        byte [] rawData = getRawDataFromView();
        
        boolean error = object.writeProperty(opcode, rawData);
        if (error) {
            view.restore();
        }
        if (output != null) {
            if (!error) {
                output.setLastResult("OK", error);
            } else {
                output.setLastResult("write fail. OPCODE: " + Utils.toHexString(opcode) + ", data: " + Utils.toHexString(rawData) , error);
            }
        }
        return error;
    }

    @Override
    public byte[] readData(ViewByte aView) {
        return new byte[]{aView.getData()};
    }

    @Override
    public byte[] readData(ViewString aView) {
        return aView.getString().getBytes();
    }

    @Override
    public byte[] readData(ViewShort aView) {
        return this.object.ShortToBytes(aView.getShort());
    }

    @Override
    public byte[] readData(ViewInt aView) {
        return this.object.IntToBytes(aView.getInt());
    }

    @Override
    public byte[] readData(ViewByteArray aView) {
        return aView.getBytes();
    }

    @Override
    public void writeData(ViewByte aView, byte[] data) {
        if (data != null && data.length > 0) {
            aView.setData(data[0]);
        }
    }

    @Override
    public void writeData(ViewShort aView, byte[] data) {
        aView.setShort(this.object.getShort(data));
    }

    @Override
    public void writeData(ViewInt aView, byte[] data) {
        aView.setInt(this.object.getInt(data));
    }

    @Override
    public void writeData(ViewString aView, byte[] data) {
        String str = "";
        if (data != null && data.length > 0) {
            str = new String(data);
        }
        aView.setString(str);
    }

    @Override
    public void writeData(ViewByteArray aView, byte[] data) {
        aView.setBytes(data);
    }

    byte[] getRawDataFromView() {
        return view.getRawData();
    }

    private void setRawDataToView(byte[] data) {
        setRawDataToView(data);
    }

    @Override
    public PropertyView getView() {
        return view;
    }
}
