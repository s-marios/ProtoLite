/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author marios
 */
public class ComboBoxByteView extends PropertyViewByte {

    DropDownDictionary dictionary;
    JComboBox comboBox;

    public ComboBoxByteView(PropertyHandler handler, String title, DropDownDictionary dict) {
        super(handler,title);
        dictionary = dict;
    }

    @Override
    protected void updateControls() {
        comboBox.setSelectedItem(dictionary.getKeyByValue(data));
    }

    @Override
    protected JPanel initPanel() {
        JPanel aPanel = new JPanel(new BorderLayout());
        JLabel aLabel = new JLabel(this.title);
        
        List sortedKeys = Arrays.asList(dictionary.getKeys());
        Collections.sort(sortedKeys);
        comboBox = new JComboBox(sortedKeys.toArray());
        JButton button = new JButton("Set");
        button.addActionListener(new ActionListener() {
            
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                backup();
                String key = (String) comboBox.getSelectedItem();
                data = dictionary.getByteValue(key);
                handler.writeProperty();

            }
        });

        aPanel.add(aLabel, BorderLayout.WEST);
        aPanel.add(comboBox, BorderLayout.CENTER);
        aPanel.add(button, BorderLayout.EAST);
        return aPanel;
    }
}
