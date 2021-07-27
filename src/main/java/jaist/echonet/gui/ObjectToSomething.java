/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
public class ObjectToSomething {

    Map<AbstractEchonetObject, JPanel> panelmap = new ConcurrentHashMap<AbstractEchonetObject, JPanel>();
    Map<AbstractEchonetObject, DevicePropertiesTableModel> datamap = new ConcurrentHashMap<AbstractEchonetObject, DevicePropertiesTableModel>();
    private NetworkScanner caller;

    ObjectToSomething(NetworkScanner caller) {
        this.caller = caller;
    }
/*
    public JPanel getSimpleComponent(AbstractEchonetObject key) {
        JPanel ans = panelmap.get(key);
        if (ans == null) {
            //generate panel, with a stupid laabel for test
            JLabel label = new JLabel("0x" + key.getEOJ());
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.NORTH);
            //panel.add(new )
            panelmap.put(key, panel);
            ans = panel;
            System.out.println("number of cards: " + panelmap.size() + " adding: " + "0x" + key.getEOJ());
            System.out.println("Key: " + key.toString());
        }
        return ans;
    }
*/
    public JPanel getTableComponent(AbstractEchonetObject key, DevicePropertiesTableModel datamodel, MouseListener ml) {
        JPanel ans = panelmap.get(key);
        //   if(ans == null) {
        //generate panel, with a label for test and a JTable
        //JLabel label = new JLabel(Utils.toHexString(key.getEOJ()));
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(datamodel, null);
        table.addMouseListener(ml);
        //panel.add(label,BorderLayout.SOUTH);

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
        ans = panel;
        System.out.println("number of cards: " + panelmap.size() + " adding: " + "0x" + key.getEOJ());
        //} //else {

        //}
        return ans;
    }

    public DevicePropertiesTableModel getDataModel(AbstractEchonetObject key) {
        return datamap.get(key);
        /*
        DevicePropertiesTableModel ans = datamap.get(key);
        if (ans == null) {
            ans = new DevicePropertiesTableModel(caller);
            datamap.put(key, ans);
        }
        return ans;*/
    }

    JPanel getPanelComponent(AbstractEchonetObject key) {
        return panelmap.get(key);
    }
}
