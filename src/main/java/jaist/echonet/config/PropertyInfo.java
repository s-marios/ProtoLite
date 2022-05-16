package jaist.echonet.config;

import jaist.echonet.EchonetProperty;

/**
 * The basic representation of a property configuration. Recaptures data to be
 * used during the initialization of echonet properties
 * 
 * @author ymakino
 * @see EchonetProperty
 */
public class PropertyInfo {
    public byte epc;
    public boolean readable;
    public boolean writable;
    public boolean notifies;
    public int size;
    public int capacitypolicy = 0;
    public byte[] data;
    
    @Deprecated
    public PropertyInfo(byte epc, boolean readable, boolean writable, boolean notifies, int size) {
        this.epc = epc;
        this.readable = readable;
        this.writable = writable;
        this.notifies = notifies;
        this.size = size;
        this.data = new byte[size];
    }
    
    public PropertyInfo(byte epc, boolean readable, boolean writable, boolean notifies, int size, int capacitypolicy) {
        this.epc = epc;
        this.readable = readable;
        this.writable = writable;
        this.notifies = notifies;
        this.size = size;
        this.data = new byte[size];
        this.capacitypolicy = capacitypolicy;
    }
    
    @Deprecated
    public PropertyInfo(byte epc, boolean readable, boolean writable, boolean notifies, byte[] data) {
        this.epc = epc;
        this.readable = readable;
        this.writable = writable;
        this.notifies = notifies;
        this.size = data.length;
        this.data = data;
    }
    
    /**
     * 
     * This constructor has capacity policy EXACT
     * 
     * @param epc
     * @param readable
     * @param writable
     * @param notifies
     * @param size
     * @param data 
     */
    public PropertyInfo(byte epc, boolean readable, boolean writable, boolean notifies, int size, byte[] data) {
        this.epc = epc;
        this.readable = readable;
        this.writable = writable;
        this.notifies = notifies;
        this.size = size;
        this.data = data;
    }
    
    public PropertyInfo(byte epc, boolean readable, boolean writable, boolean notifies, int size, int capacitypolicy, byte[] data) {
        this.epc = epc;
        this.readable = readable;
        this.writable = writable;
        this.notifies = notifies;
        this.size = size;
        this.data = data;
        this.capacitypolicy = capacitypolicy;
    }
    
    @Override
    public boolean equals(Object prop) {
        if (! (prop instanceof PropertyInfo)) {
            return false;
        }
        
        return this.epc == ((PropertyInfo)prop).epc;
    }

    @Override
    public int hashCode() {
        return this.epc;
    }
}
