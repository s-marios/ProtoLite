package jaist.echonet.gui.ns;

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
    private boolean needs_reload;

    public MyTreeModel() {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("nodes");
        model = new DefaultTreeModel(root);
    }

    public void addEchonetObject(final AbstractEchonetObject object) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                privateAddEchonetObject(object);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(MyTreeModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void privateAddEchonetObject(AbstractEchonetObject object) {

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        WrappedIp objectip = new WrappedIp(object.getQueryIp());
        WrappedObject wrapped_object = new WrappedObject(object);

        
        DefaultMutableTreeNode parent = getOrInsertTreeNode(root, objectip);
        getOrInsertTreeNode(parent, wrapped_object);

        //trigger a model reload
        if (needs_reload) {
            needs_reload = false;
            model.reload();
        }

    }

    private DefaultMutableTreeNode getOrInsertTreeNode(DefaultMutableTreeNode parent, Comparable node) {
        Enumeration children = parent.children();

        int index = 0;
        while (children.hasMoreElements()) {

            DefaultMutableTreeNode other = (DefaultMutableTreeNode) children.nextElement();
            int res = node.compareTo(other.getUserObject());
            if (res == 0) {
                //we already exist
                return other;
            }
            if (res < 0) {
                //found our position. just break
                break;
            }

            index++;
        }

        DefaultMutableTreeNode treenode = new DefaultMutableTreeNode(node);
        parent.insert(treenode, index);
        
        this.needs_reload = true;
        return treenode;
    }

    public DefaultTreeModel getModel() {
        return model;
    }

    public synchronized boolean nodeExists(AbstractEchonetObject object) {
        Enumeration children = ((TreeNode) model.getRoot()).children();
        while (children.hasMoreElements())  {
            var node = (DefaultMutableTreeNode) children.nextElement();
            if (node.getUserObject().toString().equals(object.getQueryIp().getHostAddress())) {
                return true;
            }
        }
        return false;
    }

}
