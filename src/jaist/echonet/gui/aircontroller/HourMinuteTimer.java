/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author marios
 */
public class HourMinuteTimer extends PropertyViewShort{

    JSpinner hours = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
    JSpinner mins = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
    
    public HourMinuteTimer(PropertyHandler handler, String title) {
        super(handler, title);
    }
    
    @Override
    protected JPanel initPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(getTitle()), BorderLayout.NORTH);
        panel.add(hours, BorderLayout.WEST);
        panel.add(mins, BorderLayout.CENTER);
        JButton jButton = new JButton("Set");
        jButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //TODO: figure out what to do with old restores
                //read properties
                int h = (Integer) hours.getValue();
                int m = (Integer) mins.getValue();
                h <<=8;
                h |= m;
                data = (short)(0x0000ffff & h);
                boolean writeProperty = HourMinuteTimer.this.handler.writeProperty();
            }
        });
        panel.add(jButton,BorderLayout.EAST);
        return panel;
    }

    @Override
    protected void updateControls() {
        int h = ( data & 0x0000ffff ) >> 8;
        int m = data & 0x000000ff;
        hours.setValue(h);
        mins.setValue(m);
    }
    
}
