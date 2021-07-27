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

/**
 *
 * @author marios
 */
public class NumericByteView extends PropertyViewByte{

    protected JLabel datalabel;

    public NumericByteView(PropertyHandler handler, String title) {
        super(handler, title);
    }
    
    @Override
    protected void updateControls() {
        datalabel.setText(new Integer(data & 0x000000ff).toString());
    }

    @Override
    protected JPanel initPanel() {
        JPanel aPanel = new JPanel();
        
        
        JLabel namelabel = new JLabel(this.getTitle());
        
        datalabel = new JLabel();
        datalabel.setText(new Integer(data & 0x000000ff).toString());
        JButton button = new JButton("Refresh");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                backup();
                handler.readProperty();
            }
        });
        
        aPanel.add(namelabel, BorderLayout.WEST);
        aPanel.add(datalabel, BorderLayout.CENTER);
        aPanel.add(button,BorderLayout.EAST);
        
        return aPanel;
    }
    
}
