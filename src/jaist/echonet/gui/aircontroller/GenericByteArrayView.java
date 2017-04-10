/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
public class GenericByteArrayView extends PropertyViewByteArray {

    JTextField asString;
    JTextField asHex;
    JTextField lastModified;

    public GenericByteArrayView(PropertyHandler handler, String title) {
        super(handler, title);
    }

    @Override
    protected JPanel initPanel() {
        JPanel apanel = new JPanel(new BorderLayout());
        asString = new JTextField(20);
        asString.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                lastModified = asString;
            }
        });
        asHex = new JTextField(20);
        asHex.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                lastModified = asHex;
            }
        });

        JButton button = new JButton("Set");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                backup();
                if (lastModified == null) {
                    return;
                }
                String text = lastModified.getText();

                if (lastModified == asHex) {
                    data = Utils.hexStringToByteArray(text);
                }
                if (lastModified == asString) {
                    data = text.getBytes();
                }
                handler.writeProperty();
            }
        });

        apanel.add(new JLabel("Property code: " + this.getTitle() + ", Data as String,       Data as hex"), BorderLayout.NORTH);
        apanel.add(asString, BorderLayout.WEST);
        apanel.add(asHex, BorderLayout.CENTER);
        apanel.add(button, BorderLayout.EAST);
        return apanel;
    }

    @Override
    protected void updateControls() {
        asHex.setText(Utils.toHexString(data));
        if (data != null && data.length > 0) {
            asString.setText(new String(data));
        }
        else {
            asString.setText("");
        }
    }
}
