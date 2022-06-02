package jaist.echonet.gui.aircon;

import jaist.echonet.gui.aircon.SupportsAppend;
import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import jaist.echonet.EchonetNode;
import jaist.echonet.sampledevices.VirtualAirCondition;

/**
 * The main class for the virtual air condition application. 
 * 
 * @author Sioutis Marios
 */
public class AirconApp implements SupportsAppend{
    EchonetNode context;
    JTextField textField;
    JTextArea textArea;
    JPanel panel;
    JFrame frame;
    
    public AirconApp(InetAddress address){
        context = new EchonetNode(address);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    GUIStartup();
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(AirconApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(AirconApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //VirtualAirCondition aircon = AbstractObjectWrapper.createAndRegisterLocalInstance(VirtualAirCondition.class, new AirconditionFullInfo(), context);
        //aircon.setAppendable(this);
        VirtualAirCondition aircon = new VirtualAirCondition(context, this);
        Thread t = context.start();
        this.append("Node Using address: " + context.getIP().getHostAddress() + "\n");
    }
    
    private void GUIStartup(){
        
        textField = new JTextField(20);

        textArea = new JTextArea(24, 80);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame = new JFrame("Virtual Air condition Unit");
        
        panel = new JPanel(new BorderLayout());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
       }
    
    /** 
     * The main method for the virtual air condition program. 
     * 
     * @param args A single argument (if supplied) is an IP address that will be
     * used as suggestion/hint during the network setup. The IP address may be
     * either IPv4 or IPv6
     */
    public static void main(String args []){
        InetAddress address = null;
        try {
            if(args.length > 0)
                address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AirconApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        AirconApp airconApp = new AirconApp(address);
    }

    @Override
    public void append(final String toappend) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                textArea.append(toappend);
            }
        });
    }

    @Override
    public void appendln(String toappend) {
        append(toappend + '\n');
    }
}
