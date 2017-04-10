/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import javax.swing.JCheckBox;

/**
 *
 * @author marios
 */
public class CheckBoxPropertyPanel extends PropertyPanel{

    private JCheckBox checkBox = new JCheckBox("Switched on?");

    public CheckBoxPropertyPanel() {
        this.add(checkBox);
    }
    
    @Override
    public Object getInput() {
        return checkBox.isSelected();
    }
    
}
