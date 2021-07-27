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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author marios
 */
class TemperatureView extends PropertyViewByte implements ViewByte, View{

    JSpinner spinner;
    
    public TemperatureView(AirconPropertyHandler handler, String title) {
        super(handler,title);
        this.handler = handler;
    }
    
    @Override
    protected void updateControls() {
        spinner.setValue(new Integer(this.data & 0x000000ff));
    }

    @Override
    protected JPanel initPanel() {
        SpinnerModel model =
        new SpinnerNumberModel(25, //initial value
                               0, //min
                               50, //max
                               1);   
        final JSpinner aspinner = new JSpinner(model);
        spinner = aspinner;
        
        JPanel aPanel = new JPanel(new BorderLayout());
        JLabel aLabel = new JLabel(this.title);
        JButton button = new JButton("Set temperature");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                backup();
                int temperature = (Integer) spinner.getValue();
                data = (byte) (temperature & 0x000000ff);
                handler.writeProperty();
            }
        });
        
        aPanel.add(aLabel, BorderLayout.WEST);
        aPanel.add(spinner, BorderLayout.CENTER);
        aPanel.add(button, BorderLayout.EAST);
        
        return aPanel;
    
    }
    
}
