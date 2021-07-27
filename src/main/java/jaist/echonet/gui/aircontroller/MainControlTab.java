/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 *
 * @author marios
 */
public class MainControlTab implements AcceptsQueryResults{

    private AircondionControlData airdata;
    private JPanel aPanel;
    private JTextField textField;

    public MainControlTab(AircondionControlData airdata) {
        this.airdata = airdata;
    }

    public JPanel getPanel() {
        if (aPanel == null) {
            initPanel();
        }
        return aPanel;
    }

    private void initPanel() {
        aPanel = new JPanel(new BorderLayout());
        JButton aButton = new JButton("Refresh All Properties");
        aButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                airdata.refreshAll();
            }
        });
        //String content = "";
        //JLabel aLabel = new JLabel(content);

        textField = new JTextField();
        textField.setEditable(false);

        aPanel.add(new JLabel("Last output:"), BorderLayout.NORTH);
        aPanel.add(textField, BorderLayout.CENTER);
        //aPanel.add(aLabel, BorderLayout.NORTH);
        aPanel.add(aButton, BorderLayout.EAST);
        aPanel.add(new JSeparator(), BorderLayout.SOUTH);
    }

    @Override
    public void setLastResult(String result, boolean error) {
        textField.setText(result);
        if (error) {
            textField.setBackground(Color.RED);
        } else {
            textField.setBackground(Color.GREEN);
        }
    }
}
