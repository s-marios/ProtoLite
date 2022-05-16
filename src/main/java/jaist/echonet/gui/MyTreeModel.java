/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import jaist.echonet.AbstractEchonetObject;

/**
 *
 * @author marios
 */
public class MyTreeModel {
    protected DefaultTreeModel model;
    
    public MyTreeModel(){
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("nodes");
        model = new DefaultTreeModel(root);
    }
    
    public void addEchonetObject(final AbstractEchonetObject object){
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    privateAddEchonetObject(object);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(MyTreeModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MyTreeModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private synchronized void privateAddEchonetObject(AbstractEchonetObject object){
        
        String objectip = object.getQueryIp().getHostAddress();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        ToStringObjectWrapper wrapped = new ToStringObjectWrapper(object);
        //search for the " echonet node" in the tree
        DefaultMutableTreeNode parent = searchForNode((TreeNode) model.getRoot(),objectip);
        if(parent == null) {
            parent = new DefaultMutableTreeNode(objectip);
            root.add(parent);
            parent.add(new DefaultMutableTreeNode(wrapped));
            model.reload();
        } 
        else {
        //parent existed. see if node is already in the tree
            if(!isAlreadyAdded(parent, wrapped)){
                parent.add(new DefaultMutableTreeNode(wrapped));
                model.reload();
            }
        }
        
    }
    
    public DefaultTreeModel getModel(){
        return model;
    }
    
    private DefaultMutableTreeNode searchForNode(TreeNode root, String ip){
        
        Enumeration breadth = root.children();
        while(breadth.hasMoreElements())
        {
            DefaultMutableTreeNode  anode = (DefaultMutableTreeNode) breadth.nextElement();
            if(ip.equals((String) anode.getUserObject().toString()))
                    return anode;
        }
        return null;
    }
    
    private boolean isAlreadyAdded(DefaultMutableTreeNode parent, ToStringObjectWrapper wrapped){
        if(searchForNode(parent, wrapped.toString()) != null)
            return true;
        return false;
    
    }
    
    public synchronized boolean nodeExists(AbstractEchonetObject object){
        if(searchForNode((TreeNode) model.getRoot(),object.getQueryIp().getHostAddress()) !=null)
            return true;
        return false;
        
    }

    
}
