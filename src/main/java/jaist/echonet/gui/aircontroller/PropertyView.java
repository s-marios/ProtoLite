/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author marios
 */
public abstract class PropertyView implements View, Runnable{
    protected PropertyHandler handler;
    protected JPanel panel;
    protected String title;
    protected AcceptsQueryResults output;
    
    PropertyView(PropertyHandler handler, String title){
    //    this.aircon = aircon;
        this.handler = handler;
        setTitle(title);
    }
    
    public JPanel getPanel(){
        if(panel == null)
            panel = initPanel();
        return panel;
    }

    protected abstract JPanel initPanel();
    protected abstract void updateControls();

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public void run(){
        updateControls();
    }
    
    public void updateGUI(){
        SwingUtilities.invokeLater(this);
    }
    abstract byte[] getRawData();
    abstract void setRawData(byte []data );
    abstract void backup();
    abstract void restore();
}
