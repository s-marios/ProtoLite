package jaist.echonet.gui.ns;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
public class DevicePropertiesTableModel extends AbstractTableModel {

    public static final int COLCOUNT = 5;

    private final List<TableProperty> data = new ArrayList<>();
    private final NetworkScanner callme;

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
        switch (column) {
            case 0:
                return rowdata.getCodeString();
            case 1:
                return rowdata.getAccessString();
            case 2:
                return rowdata.getDataString();
            case 3: try {
                return new String(rowdata.getRawdata());
            } catch (Exception e) {
                return "null";
            }
            default:
                return null;
        }
    }

    public int getOpcode(int row) {
        return data.get(row).getCode();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        boolean error;
        callme.setCurrentOpcode(data.get(row).getCode());
        if (col == 3) {
            error = callme.requestWrite(((String) value).getBytes());
            if (!error) {
                data.get(row).setRawdata(((String) value).getBytes());
            }
        }

        if (col == 2) {
            byte[] datatoset = Utils.hexStringToByteArray((String) value);
            if (datatoset != null) {
                error = callme.requestWrite(datatoset);
                if (!error) {
                    data.get(row).setRawdata(datatoset);
                }
            }
        }
        fireTableDataChanged();
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
            return ((rowdata.getAccess() & 0x02) != 0);//hard coding the writeable is not good

        }
    }

    private TableProperty findProperty(int code) {
        for (TableProperty property : data) {
            if (property.getCode() == code) {
                return property;
            }
        }
        return null;
    }

    private void addTableProperty(TableProperty property) {
        //sanity check
        if (property == null) {
            return;
        }
        //
        TableProperty old = findProperty(property.getCode());
        if (old != null) {
            //there was a previous entry in the list
            // logically and their access rules.
            old.setAccess(old.getAccess() | property.getAccess());
            old.setRawdata(property.getRawdata());
        } else {
            //no previous data. add it to the list in its sorted position
            insertPropertySorted(property);
        }

        fireTableDataChanged();
    }

    private void insertPropertySorted(TableProperty property) {
        var it = this.data.iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getCode() > property.getCode()) {
                break;
            }
            index ++;
        }
        this.data.add(index, property);
    }

    boolean removeProperty(int i) {
        if (i < 0 || i >= data.size()) {
            return false;
        }
        this.data.remove(i);
        fireTableDataChanged();
        return true;
    }

    boolean removeProperty(TableProperty property) {
        return data.remove(property);
    }

    void addTableProperties(final List<TableProperty> properties) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (TableProperty property : properties) {
                    addTableProperty(property);
                }
            }
        }
        );
    }

}
