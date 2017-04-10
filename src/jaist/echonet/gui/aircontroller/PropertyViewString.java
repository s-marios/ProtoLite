/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author marios
 */
public class PropertyViewString extends PropertyView implements ViewString {

    private String string;
    private JLabel label;
    private JTextField textField;
    
    public PropertyViewString(PropertyHandler handler, String title) {
        super(handler,title);
    }

    @Override
    public void setString(String string) {
        this.string = string;
        if (textField != null) {
            textField.setText(string);
        }
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Override
    protected JPanel initPanel() {
        label = new JLabel(getTitle());
        textField = new JTextField(30);
        textField.setText(string);
        textField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                string = ae.getActionCommand();
                handler.writeProperty();
            }
        });

        JPanel apanel = new JPanel(new BorderLayout());
        apanel.add(label, BorderLayout.WEST);
        apanel.add(textField, BorderLayout.CENTER);
        
        return apanel;
    }

    @Override
    byte[] getRawData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void setRawData(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateControls() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void backup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void restore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
