package jaist.echonet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import jaist.echonet.util.Utils;

/**
 * A memento used during serialization of properties. Each property can return
 * a memento that stores the following information:
 * <ul>
 * <li> the contents of the property</li>
 * <li> the property code</li>
 * </ul>
 * To restore the contents of a property just call the <code>write</code> method
 * of the property and passing as data the byte array returned from the <code>
 * read()</code> method of the memento
 * This class is serializable
 * 
 * @author Sioutis Marios
 */
public class PropertyMemento  implements Serializable{
    private byte[] data;
    private byte opcode;
    
    PropertyMemento(){
    }
    
    /**
     * Constructor
     * 
     * @param copyfrom The property from which this memento will be created
     */
    PropertyMemento(EchonetProperty copyfrom){
        opcode = copyfrom.getPropertyCode();
        data = copyfrom.read();
    }
    
    /**
     * Gets the contents of the memento. This data is the data as captured when
     * the memento was created
     * @return the captured data
     */
    public byte[] read() {
        return data;
    }
    
    /**
     * Gets the property code of the memento. It has the same property code
     * as the property that generated it.
     * @return the property code associated with this memento
     */
    public byte getPropertyCode(){
        return opcode;
    }

    public static void main(String args[]) throws Exception{
        EchonetCharacterProperty cb = new EchonetCharacterProperty((byte)0x90, true, true, "skata".getBytes());
        PropertyMemento id = new PropertyMemento(cb);
        System.out.println("id: " + Utils.toHexString(id.getPropertyCode()) );
        System.out.println("data: " + Utils.toHexString(id.read()));
        
        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("Test.out"));
        oout.writeObject(id);
        oout.close();
        
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream("Test.out"));
        PropertyMemento readObject = (PropertyMemento) oin.readObject();
        id = readObject;
        System.out.println("id: " + Utils.toHexString(id.getPropertyCode()) );
        System.out.println("data: " + Utils.toHexString(id.read()));
        
    }
}
