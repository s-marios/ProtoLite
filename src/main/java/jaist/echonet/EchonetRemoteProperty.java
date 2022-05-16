package jaist.echonet;

/**
 *
 * @author Sioutis Marios
 */
public class EchonetRemoteProperty extends EchonetProperty {

    RemoteEchonetObject remoteObject;
    
    public EchonetRemoteProperty(RemoteEchonetObject remoteObject, byte propcode, boolean isreadable, boolean iswriteable, boolean notifies) {
        super(propcode, isreadable, iswriteable, notifies);
        this.remoteObject = remoteObject;
    }

    @Override
    public byte[] read() {
        if (!this.isReadable() ){
            return null;
        }
        return this.remoteObject.readProperty(this.getPropertyCode());
    }

    @Override
    public boolean write(byte[] data) {
        if (!this.isWriteable()){
            //return true is the error condition
            return true;
        }
        return this.remoteObject.writeProperty(this.getPropertyCode(), data);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
    //package private stuff, should not use directly
    void setReadable(boolean readable){
        this.readable = readable;
    }
    
    void setWritable(boolean writable){
        this.writable = writable;
    }
    
    void setNotifies(boolean notifies){
        this.notifies = notifies;
    }
    
}
