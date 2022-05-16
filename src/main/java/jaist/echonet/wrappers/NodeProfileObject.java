package jaist.echonet.wrappers;

import jaist.echonet.*;

/**
 * The node profile object wrapper
 * 
 * @author Sioutis Marios
 */
public class NodeProfileObject extends ProfileObject{
        
    protected static byte instanceno = 0;
    protected static byte eojclass = (byte) 0xF0; 
    
    public static NodeProfileObject createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(NodeProfileObject.class, obj);
    }
    
    public static NodeProfileObject createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(NodeProfileObject.class, obj, whoasks);
    }
    
    //wrapper methods
    public short getNodeStaticId(){
        try{
            return getShort(readProperty((byte) 0xBF));
        }
        finally{
            return (short) 0xFFFF;
        }
    }
    
    //i'm not sure about this....
    public boolean setNodeStaticId(short id){
        return writeProperty((byte) 0xBF, ShortToBytes(id));
    }
    
    //public set Address is not provided. we can get this from the node context.
    public boolean getLockStatus(){
        try{
            return readProperty((byte) 0xEE)[0] == (byte) 30 ? true : false;
        } finally {
            return false;
        }
    }
    
    public boolean setLockStatus(boolean lock){
        return writeProperty((byte)0xEE, new byte[]{(byte) (lock? 0x30 : 0x31)});
    }
   
    public byte[] getLockInformation(){
        return readProperty((byte) 0xEF);
    }
    
    public boolean setLockInformation(byte[] lockaddress){
        return writeProperty((byte) 0xEF, lockaddress);
    }
    
    /**
     * Returns the number of echonet object instances this node is managing
     * 
     * @return the number of object instances on this node
     */
    public int getNumberOfInstances(){
        try{
        return readProperty((byte) 0xD3)[0] & 0x000000FF;
        } finally {
            return -1;
        }
    }
    
    /**
     * Gets the number of different classes on this node
     * 
     * @return an int representing the number of different classes
     */
    public int getNumberOfClasses(){
        try{
        return readProperty((byte) 0xD4)[0] & 0x000000FF;
        } finally {
            return -1;
        }
    }
    
    /**
     * Gets a list of all the instances on this node
     * 
     * @return a byte array, first byte is the number of instances, and
     * concecutive triplets of bytes represent an eoj of an object on the node
     */
    public byte[] getInstanceList(){
        return readProperty((byte) 0xD5);
    }
    
    //whatever this is....
    public byte[] getInstanceListS(){
        return readProperty((byte) 0xD6);
    }
    
    /**
     * Gets a list of all available class types on this node
     * 
     * @return a byte array, whose first byte is the nubmer of classes, and
     * consecutive two bytes represent the class code
     */
    public byte[] getClassList(){
        return readProperty((byte) 0xD7);
    }
}
