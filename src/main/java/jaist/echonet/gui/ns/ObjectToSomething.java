package jaist.echonet.gui.ns;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import jaist.echonet.AbstractEchonetObject;

/**
 *
 * @author marios
 */
public class ObjectToSomething {

    Map<AbstractEchonetObject, JPanel> panelmap = new ConcurrentHashMap<>();
    Map<AbstractEchonetObject, DevicePropertiesTableModel> datamap = new ConcurrentHashMap<>();

    public JPanel getTableComponent(AbstractEchonetObject key, DevicePropertiesTableModel datamodel, MouseListener ml) {

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(datamodel, null);
        table.addMouseListener(ml);

        //datamodel register
        this.datamap.put(key, datamodel);

        //table setup
        TableColumnModel columns = new DefaultTableColumnModel();
        String[] headers = {"Opcode", "Access", "RawData", "asString"};
        for (int i = 0; i < 4; i++) {
            TableColumn col = new TableColumn(i);
            col.setHeaderValue(headers[i]);
            columns.addColumn(col);

        }
        columns.setColumnSelectionAllowed(false);
        columns.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setColumnModel(columns);
        table.setShowGrid(true);
        table.setCellSelectionEnabled(true);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panelmap.put(key, panel);
        System.out.println("number of cards: " + panelmap.size() + " adding: " + "0x" + key.getEOJ());

        return panel;
    }

    public DevicePropertiesTableModel getDataModel(AbstractEchonetObject key) {
        return datamap.get(key);
    }

    JPanel getPanelComponent(AbstractEchonetObject key) {
        return panelmap.get(key);
    }
}
