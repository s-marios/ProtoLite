package jaist.echonet;

import java.util.ArrayList;
import java.util.List;

/**
 * A dummy property that is used only to convey the code of a property. Read and
 * write operations will fail
 * 
 * @author Sioutis Marios
 */
public class EchonetDummyProperty extends EchonetProperty{
    
    /**
     * Constructor. The only argument is the property code
     * 
     * @param propcode The property code as a byte for this property
     */
    public EchonetDummyProperty(byte propcode){
        super(propcode, true, true);
    }

    @Override
    public byte[] read() {
        return null;
    }

    @Override
    public boolean write(byte[] data) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
    
    public static List<EchonetProperty> getDummies(byte[] codelist){
        if(codelist == null || codelist.length == 0)
            return null;
        List<EchonetProperty> alist =new ArrayList<EchonetProperty>();
        for (byte propcode : codelist){
            alist.add(new EchonetDummyProperty(propcode));
        }
        return alist;
    }
    
}
