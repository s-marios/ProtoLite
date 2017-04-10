/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

/**
 *
 * @author marios
 */
public interface PropertyHandler extends Comparable<PropertyHandler> {
    //public void receiveNotification();
    //for use by the panels..
    //wiill get the data form the panels and make its request, eventually 
    //updating the panels.
    public boolean readProperty();
    public boolean writeProperty();
    //this is used for notifications
    public void setHandlerData(byte [] data);

    public PropertyView getView();

    byte[] readData(ViewByte aView);

    void writeData(ViewByte aView, byte[] data);

    byte[] readData(ViewByteArray aView);

    byte[] readData(ViewInt aView);

    byte[] readData(ViewShort aView);

    byte[] readData(ViewString aView);

    void writeData(ViewByteArray aView, byte[] data);

    void writeData(ViewInt aView, byte[] data);

    void writeData(ViewShort aView, byte[] data);

    void writeData(ViewString aView, byte[] data);
    
    public AcceptsQueryResults getOutput();

    public void setOutput(AcceptsQueryResults output);

    byte getOpCode();
    
    
}
