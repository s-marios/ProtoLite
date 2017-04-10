/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import jaist.echonet.util.Utils;


/**
 *
 * @author marios
 */
public class DevicePropertiesTableModel extends AbstractTableModel{
    
    public static final int COLCOUNT = 5;
    static final byte convertbase = 65;
        
    private List<TableProperty> data = new ArrayList<TableProperty>();
    private NetworkScanner callme;

    DevicePropertiesTableModel(NetworkScanner callme) {
        this.callme = callme;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLCOUNT;
    }

    @Override
    public Object getValueAt(int row, int column) {
        TableProperty rowdata = data.get(row);
        switch(column){
            case 0: return rowdata.getCodeString();
            case 1: return rowdata.getAccessString();
            case 2: return rowdata.getDataString();
            case 3: try {
                    return new String(rowdata.getRawdata());
                } catch (Exception e) {
                    return "null";
                }
            default: return null;
        }
    }
    
    public int getOpcode(int row){
        return data.get(row).getCode();
    }
    
    @Override
    public void setValueAt(Object value, int row, int col){
        boolean error = true;
        callme.setCurrentOpcode(data.get(row).getCode());
        if(col == 3){
            error = callme.requestWrite(((String) value).getBytes()); 
            if(!error) {
                data.get(row).setRawdata(((String) value).getBytes());
            }
        }
        
        if(col == 2)
        {
            byte [] datatoset = Utils.hexStringToByteArray((String) value);
            if(datatoset != null){
                error = callme.requestWrite(datatoset);
                if(!error){
                    data.get(row).setRawdata(datatoset);
                }
            }
        }
        fireTableDataChanged();
    }
    
    private String charToHex(byte letter){
        if(letter > 0x0f || letter <0)
            return null;
        if(letter < 10)
            return new String(new char[]{(char)(letter + 48)});
        else return new String(new char[]{(char)(letter + convertbase - 10)});
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
        //TODO fix editable here
            if (col < 2) {
                return false;
            } else {
                TableProperty rowdata = this.data.get(row);
                return ((rowdata.getAccess() & 0x02) != 0) ;//hard coding the writeable is not good
                
            }
        }
     
    private TableProperty findProperty(int code){
        for(TableProperty property : data)
            if (property.getCode() == code)
                return property;
        return null;
    }
    
    private void addTableProperty(TableProperty property){
        //sanity check
        if(property == null)
            return;
        //
        TableProperty old = findProperty(property.getCode());
        if(old != null){
            //there was a previous entry in the list
            // logically and their access rules.
            old.setAccess(old.getAccess() | property.getAccess());
            old.setRawdata(property.getRawdata());
        } else {
        //no previous data. add it to the list
            this.data.add(property);
        }
        
        fireTableDataChanged();
    }
    
    public boolean removeProperty(int i){
        if (i < 0 || i>= data.size() )
            return false;
        this.data.remove(i);
        fireTableDataChanged();
        return true;
    }
    
    public boolean removeProperty(TableProperty property){
        return data.remove(property);
    }
    
    public void addTableProperties(final List<TableProperty> properties){
        SwingUtilities.invokeLater(new Runnable(){
            
            @Override
            public void run() {
                for(TableProperty property: properties)
                    addTableProperty(property);
            }
        }
        );
    }
    
}

