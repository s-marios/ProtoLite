package jaist.echonet.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import jaist.echonet.EOJ;
import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.EchonetProperty;

/**
 * Represents configuration information for a device. A DeviceInfo object holds
 * a list of properties that will be initialized as character buffer properties
 * on the final, initialized echonet object.
 * <p>
 * Every configuration for a device MUST derive from this class and override the
 * two abstract methods, {@link DeviceInfo#setObjectClass() } and {@link 
 * DeviceInfo#addProperties() }
 * 
 * @author ymakino, Sioutis Marios
 * @see EchonetProperty
 * @see EchonetCharacterProperty
 */
public abstract class DeviceInfo {
    
    class PropertyInfoComparator implements Comparator<PropertyInfo> {
        @Override
        public int compare(PropertyInfo p1, PropertyInfo p2) {
            int c1 = (p1.epc & 0x000000ff);
            int c2 = (p2.epc & 0x000000ff);
            return c1 - c2;
        }
    }
    
    private EOJ classEOJ;
    private TreeSet<PropertyInfo> props = new TreeSet<PropertyInfo>(new PropertyInfoComparator());
    private ArrayList<PropertyInfo> propList = null;
    
    public DeviceInfo() {
        
        add((byte)0x80, true, false,  true, 1, new byte[]{(byte) 0x30});
        add((byte)0x88, true, false,  true, 1);
        add((byte)0x8A, true, false, false, 3, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff});
        add((byte)0x9d, true, false, false, 17); 
        add((byte)0x9e, true, false, false, 17);
        add((byte)0x9f, true, false, false, 17);
        
        construct();
    }
    
    
    private void construct() {
        setObjectClass();
        addProperties();
    }
    
    public EOJ getClassEOJ() {
        return classEOJ;
    }
    
    public void setClassEOJ(EOJ eoj) {
        classEOJ = eoj.getClassEOJ();
    }
    
    /**
     * Used to set the eoj class information for the device to be initialized
     * with this configuration. Methods that override this should just call the
     * {@link DeviceInfo#setClassEOJ(jaist.echonet.EOJ) } method.
     */
    protected abstract void setObjectClass();
    
    /**
     * Add properties that will be seen as EchonetCharacterProperty properties to
     * the final object by overriding this method. Any number of <code>add()
     * </code> is allowed. For implementation examples, see the "See Also"
     * section below.
     * 
     * @see TemperatureSensorInfo
     */
    protected abstract void addProperties();
    
    @Deprecated
    public final boolean add(byte epc, boolean readable, boolean writable, boolean notifies, int size) {
        propList = null;
        return add(new PropertyInfo(epc, readable, writable, notifies, size));
    }
    
    @Deprecated
    public final boolean add(byte epc, boolean readable, boolean writable, boolean notifies, byte[] data) {
        propList = null;
        return add(new PropertyInfo(epc, readable, writable, notifies, data));
    }
    
    public final boolean add(byte epc, boolean readable, boolean writable, boolean notifies, int size, byte[] data) {
        propList = null;
        return add(new PropertyInfo(epc, readable, writable, notifies, size, data));
    }
    
    public final boolean add(byte epc, boolean readable, boolean writable, boolean notifies, int size, int capacity, byte[] data) {
        propList = null;
        return add(new PropertyInfo(epc, readable, writable, notifies, size, capacity, data));
    }
    
    public final boolean add(PropertyInfo prop) {
        makePropListOutdated();
        props.remove(prop);
        return props.add(prop);
    }
    
    public void makePropListOutdated() {
        propList = null;
    }
    
    public ArrayList<PropertyInfo> getPropList() {
        if (propList == null) {
            propList = new ArrayList<PropertyInfo>(props);
        }
        return propList;
    }
    
    public PropertyInfo getAtIndex(int i) {
        return getPropList().get(i);
    }
    
    public PropertyInfo getAtEPC(byte epc) {
        for (PropertyInfo prop : props) {
            if (prop.epc == epc) {
                return prop;
            }
        }
        return null;
    }
    
    public final int size() {
        return props.size();
    }
}
