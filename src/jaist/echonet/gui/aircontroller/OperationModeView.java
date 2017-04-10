/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jaist.echonet.gui.aircontroller.OperationModeView.OpModes;

/**
 *
 * @author marios
 */
public class OperationModeView extends PropertyViewByte{

    enum OpModes{

        AUTO((byte) 0x41),
        COOLING((byte) 0x42),
        HEATING((byte) 0x43),
        DEHUMIDIFY((byte) 0x44),
        FAN((byte)(0x45)),
        OTHER((byte)(0x40)),
        UNSPECIFIED((byte) 0x00);
        
        byte opcode; //percentage

        OpModes(byte opcode) {
            this.opcode = opcode;
        }

        byte getOpCode() {
            return opcode;
        }
        
        static OpModes getOpMode(byte opcode){
            for(OpModes mode: OpModes.values())
                if(mode.getOpCode() == opcode)
                    return mode;
            return OpModes.UNSPECIFIED;
        }
    }
    
    JComboBox comboBox;
    
    public OperationModeView(PropertyHandler handler, String title) {
        super(handler,title);
    }

    @Override
    protected void updateControls() {
        
        comboBox.setSelectedItem(OpModes.getOpMode(this.data));
    }

    @Override
    protected JPanel initPanel() {
        JPanel aPanel = new JPanel(new BorderLayout());
        JLabel aLabel = new JLabel(this.title);
        comboBox = new JComboBox(OpModes.values());
        comboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                backup();
                OpModes mode = (OpModes) comboBox.getSelectedItem();
                data = mode.getOpCode();
                handler.writeProperty();
                        
            }
        });
 
        aPanel.add(aLabel, BorderLayout.WEST);
        aPanel.add(comboBox, BorderLayout.CENTER);
        
        return aPanel;
    }
    
    
}
