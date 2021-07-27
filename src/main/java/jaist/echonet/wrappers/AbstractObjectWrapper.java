package jaist.echonet.wrappers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jaist.echonet.EchoEventListener;
import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.config.DeviceInfo;

/**
 * This is the top wrapper class. A wrapper class is used to manipulate echonet
 * objects using simpler to use methods. Most methods that read or write a 
 * property will result into one query being made.
 * 
 * @author Sioutis Marios
 */
public abstract class AbstractObjectWrapper {
    /**
     * The underlying echonet object (remote or local)
     */
    protected AbstractEchonetObject rawobject = null;
    
    /**
     * The originator echonet object. It will be used as the sender for any
     * queries
     */
    protected AbstractEchonetObject whoasks;
    
    /**
     * this buffer is used for reading/writing the underlying properties and 
     * help with binary conversions
     */
    protected ByteBuffer bb = ByteBuffer.allocate(16);
    
    /**
     * The writer which will carry out the write operations/queries
     */
    protected Writer writer = null;
    
    /**
     * Do not use directly. Use <code>createAndRegisterLocalinstance</code>
     * @param <T>
     * @param type
     * @param config
     * @return
     */
    private static <T extends AbstractObjectWrapper> T createLocalInstance(Class<T> type, DeviceInfo config){
        //init echonet object
        LocalEchonetObject lobject = new LocalEchonetObject(config);
        return getLocalInstance(type, lobject);
    }
    
    /**
     * Creates a wrapper of the given class type while also initializing the 
     * underlying local echonet object and registering it at the same time
     * 
     * @param <T> 
     * @param type The type of the wrapper requested
     * @param config configuration with which to initialize the local echonet
     * object
     * @param node the EchonetNode with which the local object will be registered
     * @return an instance of the requested wrapper class
     */
    public static <T extends AbstractObjectWrapper> T createAndRegisterLocalInstance(Class<T> type, DeviceInfo config, EchonetNode node){
        T t = createLocalInstance(type, config);
        t.registerSelfWithNode(node);
        return t;
    }  
    
    /**
     * Creates a wrapper of the given class type for an already initialized local
     * echonet object.
     * 
     * @param <T>
     * @param type The type of the wrapper class requested
     * @param lobject the local object
     * @return an instance of the requested wrapper class
     */
    public static <T extends AbstractObjectWrapper> T getLocalInstance(Class<T> type,
                LocalEchonetObject lobject){
        LocalWriter writer = new LocalWriter(lobject);
        return getInstance(type, lobject, null, writer);
    }
    
    /**
     * Creates a wrapper of the specified class type for a remote echonet object
     * 
     * @param <T>
     * @param type The type of the wrapper class requested
     * @param robject the remote object to be wrapped up
     * @param whoasks the echonet object that will be used as source for any
     * necessary queries
     * @return an instance of the requested wrapper class
     */
    public static <T extends AbstractObjectWrapper> T getRemoteInstance(Class<T> type, 
            RemoteEchonetObject robject, 
            AbstractEchonetObject whoasks){
        if(robject == null || whoasks == null )
            return null;
        RemoteWriter writer = new RemoteWriter(robject);
        return getInstance(type, robject, whoasks, writer);
    }
    
    private static <T extends AbstractObjectWrapper> T getInstance(Class<T> type, 
            AbstractEchonetObject object, 
            AbstractEchonetObject whoasks,
            Writer writer){
        try {
            T t = type.newInstance();
            t.rawobject = object;
            t.whoasks = whoasks;
            t.writer = writer;
            return t;
        } catch (InstantiationException ex) {
            Logger.getLogger(AbstractObjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AbstractObjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Registers this wrapper (its underlying local object) to the given 
     * echonet node
     * 
     * @param context the echonet node to register to
     */
    public void registerSelfWithNode(EchonetNode context){
        context.registerEchonetObject(this.getLocalEchonetObject());
    }

    /**
     * Interprets a raw read of a property map. This call should be used to
     * decipher the binary results of a read of a property map.
     * 
     * @param compiled the raw read of a "property map" property 
     * @return a byte array whose each entry corresponds to a single property code
     */
    public static byte[] propertyMap(byte[] compiled) {
        if (compiled == null || compiled.length == 0) {
            return null;
        }
        byte[] properties = new byte[compiled[0]];
        int howmany = compiled[0];
        if (howmany < 16) {
            //malformed property map packet?
            if (compiled.length != howmany + 1) {
                return null;
            }
            //no, continue processing
            for (int i = 0; i < howmany; i++) {
                properties[i] = compiled[i + 1];
            }
        } else {
            int mask = 1;
            int where = 0;
            for (int j = 0; j < 8; j++) {
                for (int i = 1; i <= 16; i++) {
                    if ((compiled[i] & mask) != 0) {
                        int howmuch = 0;
                        int tempmask = mask;
                        while ((tempmask >>= 1) != 0) {
                            howmuch++;
                        }
                        howmuch |= 8;
                        try {
                            properties[where++] = (byte) ((howmuch << 4) + (i - 1));
                        } catch (IndexOutOfBoundsException e) {
                            return null;
                        }
                    }
                }
                mask <<= 1;
            }
        }
        return properties;
    }
            
    /**
     * Gets the underlying object
     * 
     * @return the underlying object
     */
    public AbstractEchonetObject getEchonetObject(){
        return rawobject;
    }
    
    /**
     * Gets the underlying local object, if available
     * 
     * @return the underlying local echonet object, or null if the underlying 
     * object is a remote echonet object
     */
    public LocalEchonetObject getLocalEchonetObject(){
        AbstractEchonetObject answer = getEchonetObject();
        if (answer instanceof LocalEchonetObject)
            return (LocalEchonetObject) answer;
        else return null;
    }
    

    
    /**
     * Helper function that converts a byte array of length 4 to an integer
     * 
     * @param bytes the source byte array
     * @return the converted integer
     */
    public int getInt(byte[] bytes){
        assert(bytes.length == 4);
        bb.rewind();
        bb.put(bytes);
        bb.rewind();
        return bb.getInt();
    }
    
    /**
     * Helper function that converts a byte array of length 2 to a short
     * 
     * @param bytes the source byte array
     * @return the converted short
     */
    public short getShort(byte[] bytes){
        assert(bytes.length == 2);
        bb.rewind();
        bb.put(bytes);
        bb.rewind();
        return bb.getShort();
    }
    
    /**
     * Helper function that converts a short number to a byte array of length 2
     * 
     * @param number the number to convert
     * @return the resulting byte array
     */
    public byte[] ShortToBytes(short number){
        bb.rewind();
        bb.putShort(number);
        bb.rewind();
        byte [] bytes = new byte [2];
        bb.get(bytes);
        return bytes;
    }
    
    /**
     * Helper function that converts an integer to a byte array of length 4
     * 
     * @param number the number to convert
     * @return the resulting byte array
     */
    public byte[] IntToBytes(int number){
        bb.rewind();
        bb.putInt(number);
        bb.rewind();
        byte [] bytes = new byte [4];
        bb.get(bytes);
        return bytes;
    }
    //TODO have some functions for getInt getShort stuff.
    //TODO call rawobject's appropriate read.

    /**
     * Gets the raw "GET" property map of the underlying object. Interpret the 
     * result using the static function <code>propertyMap</code>
     * 
     * @return the raw property map
     */
    public byte[] GetPropertyMap() {
        return rawobject.readProperty(whoasks, (byte) 0x9f);
    }

    /**
     * Gets the raw "Notify" property map of the underlying object. Interpret the 
     * result using the static function <code>propertyMap</code>
     * 
     * @return the raw property map
     */
    public byte[] NotifyPropertyMap() {
        return rawobject.readProperty(whoasks, (byte) 0x9d);
    }

    /**
     * Gets the raw "SET" property map of the underlying object. Interpret the 
     * result using the static function <code>propertyMap</code>
     * 
     * @return the raw property map
     */
    public byte[] SetPropertyMap() {
        return rawobject.readProperty(whoasks, (byte) 0x9e);
    }

    /**
     * Used internally, to compute the property map properties
     * 
     * @param properties
     * @return the compiled property map, as per the appendix
     */
    protected byte[] compilePropertyMap(List<EchonetProperty> properties) {
        byte[] map = new byte[properties.size() < 16 ? properties.size() + 1 : 16];
        map[0] = (byte) properties.size();
        if (properties.size() < 16) {
            //just enummerate the property codes.
            int i = 1;
            for (EchonetProperty property : properties) {
                map[i++] = property.getPropertyCode();
            }
        } else {
            //binary representation.
            for (EchonetProperty property : properties) {
                //1) extract witch byte we're interested in.
                int byteno = property.getPropertyCode() & (byte) 0xf;
                byteno++;
                //2) extract witch bit we must flip
                int bittoflip = property.getPropertyCode() & (byte) 0x70;
                bittoflip >>= 4;
                //3) to get the bit mask, we rol by the bit number
                byte mask = 1;
                mask <<= bittoflip;
                //4)or the mask with whatever results we have there.
                map[byteno] |= mask;
//                System.out.printf("Property: 0x%2X, byteno : %d, bitflip: %d, mask: %d, contents: 0x%2X%n", property.getPropertyCode(), byteno, bittoflip, mask, map[byteno]);
            }
        }
        return map;
    }

    /**
     * Computes the property map properties
     */
    protected void computeMapProperties() {
        Collection<EchonetProperty> properties = rawobject.getPropertyList();
        List<EchonetProperty> readable = new ArrayList<EchonetProperty>();
        List<EchonetProperty> writeable = new ArrayList<EchonetProperty>();
        List<EchonetProperty> notifies = new ArrayList<EchonetProperty>();
        for (EchonetProperty property : properties) {
            if (property.isReadable()) {
                readable.add(property);
            }
            if (property.isWriteable()) {
                writeable.add(property);
            }
            if (property.doesNotify()) {
                notifies.add(property);
            }
            rawobject.addProperty(new EchonetCharacterProperty((byte) 0x9d, false, false, compilePropertyMap(notifies)));
            rawobject.addProperty(new EchonetCharacterProperty((byte) 0x9e, false, false, compilePropertyMap(writeable)));
            rawobject.addProperty(new EchonetCharacterProperty((byte) 0x9f, false, false, compilePropertyMap(readable)));
        }
    }
    
    //this makes the wrapper behave as an echonet object. It's mostly glue code
    //should use these.
    /**
     * 
     * Makes a read attempt at the requested property of the underlying object
     * 
     * @param property the code of the property to be read
     * @return a byte array, containing the data read
     */
    public byte[] readProperty(byte property){
        return this.rawobject.readProperty(whoasks, property);
    }
    
    /**
     * 
     * Makes an unprivileged write attempt at the requested property of the
     * underlying object
     * 
     * @param property The property to be used as source; (property code and 
     * source of data for the property to be written)
     * @return true if an error occured, false otherwise 
     */
    public boolean writeProperty(EchonetProperty property){
        return this.writer.writeProperty(whoasks,property);
    }
    
    /**
     *
     * * Makes an unprivileged write attempt at the requested property of the
     * underlying object
     * 
     * @param property the code of the property to be written
     * @param data the raw data to be written
     * @return true if an error occurred, false otherwise
     */
    public boolean writeProperty(byte property, byte[] data){
        return this.writer.writeProperty(whoasks, property, data);
    }
    
    /**
     * Adds a property to the underlying echonet object. Use with care.
     * 
     * @param property the property to add
     */
    protected void addProperty(EchonetProperty property){
        this.rawobject.addProperty(property);
    }
    
    /**
     * 
     * Registers a write event listener with the underlying echonet object
     * 
     * @param listener the listener to be registered
     */
    protected void registerListener(EchoEventListener listener){
        this.rawobject.registerListener(listener);
    }
    
    /**
     * For internal use, checks if a given argument is null or exceeds a given
     * length
     * 
     * @param arg the raw argument
     * @param length the expected lenght (must be exactly this long)
     * @return true if the argument is not valid, false otherwise
     */
    protected boolean checkArg(byte[] arg, int length){
        if(arg==null || arg.length != length)
            return true;
        return false;
    }
    
    //wrapper helper functions.
    /**
     * Returns the operation status of the object (property 0x80)
     * 
     * @return true if the object is in operation, false otherwise
     */
    public boolean getStatus(){
        byte[] status = readProperty((byte) 0x80);
        if(status == null || status.length == 0 || status[0] != 0x30)
            return false;
        return true;
    }
    
    /**
     * Sets the operation status of the object (property 0x80)
     * 
     * @param status true if the object is operational false for not operational
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setStatus(boolean status){
        byte[] write = new byte[1];
        write[0] = (byte) (status? 0x30 : 0x31);
        return writeProperty((byte) 0x80, write );
    }
    
    /**
     * Gets the ECHONET Lite version of the given object
     * 
     * @return a byte array containing the version data
     */
    public byte[] getVersion(){
        return readProperty((byte)0x82);
    }
    
    /**
     * Sets the ECHONET Lite version of the given object (property 0x82)
     * 
     * @param version a byte array containing the version data, as specified
     * in the appendix
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setVersion(byte [] version){
        return writeProperty((byte)0x82, version);
    }
    
    /**
     * Gets the ID number of the object (property 0x83)
     * 
     * @return the property in the format as specified in the appendix
     */
    public byte[] getIdNumber(){
        return readProperty((byte) 0x83);
    }
    
    /**
     * Set the ID number of the object 
     * 
     * @param idnumber 
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setIdNumber(byte[] idnumber){
        return writeProperty((byte)0x83, idnumber);
    }
    
    /**
     * Gets the error content (property 0x89)
     * 
     * @return the error type as specified in the appendix
     */
    public byte[] getErrorContent(){
        return readProperty((byte) 0x89);
    }
    
    /**
     * 
     * Sets the error content (property 0x89)
     * 
     * @param errorcontent the error type as specified in the appendix
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setErrorContent(byte [] errorcontent){
        if(checkArg(errorcontent, 2)) return true;
        return writeProperty((byte) 0x89, errorcontent);
    }
    
    /**
     * Gets the vendor code (property 0x8A)
     * 
     * @return the vendor code
     */
    public byte[] getVendorCode(){
        return readProperty((byte) 0x8A);
    }
    
    /**
     * Sets the vendor code (property 0x8A)
     * 
     * @param vendorcode the vendor code
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setVendorCode(byte[] vendorcode){
        return writeProperty((byte) 0x8A, vendorcode);
    }
    
    //TODO bad name, change it
    /**
     * Gets the manufacturing place (property 0x8B)
     * 
     * @return the manufacturing place
     */
    public byte[] getVendorPlace(){
        return readProperty((byte) 0x8b);
    }
    
    /**
     * Sets the manufacturing place
     * 
     * @param vendorplace the manufacturing place (property 0x8B)
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setVendorPlace(byte[] vendorplace){
        if(checkArg(vendorplace, 3)) return true;
        return writeProperty((byte)0x8B, vendorplace);
    }
    
    /**
     * Gets the product code (property 0x8C)
     * 
     * @return the product code
     */
    public byte[] getProductCode(){
        return readProperty((byte) 0x8C);
    }
    
    /**
     * Sets the product code (property 0x8C)
     * 
     * @param productcode the product code
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setProductCode(byte[] productcode){
        return writeProperty((byte) 0x8C, productcode);
    }
    
    /**
     * Gets product serial number (property 0x8D)
     * 
     * @return the product serial number
     */
    public byte[] getProductionSerialNumber(){
        return readProperty((byte) 0x8D);
    }
    
    /**
     * Sets product serial number (property 0x8D)
     * 
     * @param serial the product serial number
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setProductionSerialNumber(byte [] serial){
        return writeProperty((byte) 0x8D, serial);
    }
    
    /**
     * Gets production date (property 0x8E)
     * @return production date
     */
    public byte[] getProductionDate(){
        return readProperty((byte) 0x8E);
    }
    
    /**
     * Sets production date (property 0x8E)
     * 
     * @param productiondate production date
     * @return true if an error occurred during the setting of the property, 
     * false otherwise
     */
    public boolean setProductionDate(byte [] productiondate){
        if(checkArg(productiondate, 4)) return true;
        return writeProperty((byte) 0x8e, productiondate);
    }
    
}
