package jaist.echonet.wrappers;

/**
 * Device super object wrapper. All device object wrappers inherit from this
 * class. Has methods to read/write the common properties of all objects
 * 
 * @author Sioutis Marios
 */
public abstract class DeviceSuperObject extends AbstractObjectWrapper {
    
    //this constructor is for local objects
    public DeviceSuperObject(){
    }
    
        /*
    public void setupDeviceObject(){
        addProperty(new EchonetCharacterProperty((byte)0x80, false, true, 1));
        addProperty(new EchonetCharacterProperty((byte)0x81, true, true, 1));
        addProperty(new EchonetCharacterProperty((byte)0x82, false, false, 4));
        addProperty(new EchonetCharacterProperty((byte)0x83, false, false, 19));
//for testing purposes! remove later.
        addProperty(new EchonetCharacterProperty((byte)0x84, false, false, 2));
        addProperty(new EchonetCharacterProperty((byte)0x85, false, false, 4));
        addProperty(new EchonetCharacterProperty((byte)0x86, false, false, 255));
        addProperty(new EchonetCharacterProperty((byte)0x87, false, false, 1));
        addProperty(new EchonetCharacterProperty((byte)0x88, false, true, 1));
        addProperty(new EchonetCharacterProperty((byte)0x89, false, false, 2));
        addProperty(new EchonetCharacterProperty((byte)0x8A, false, false, 3));
        addProperty(new EchonetCharacterProperty((byte)0x8B, false, false, 3));
        addProperty(new EchonetCharacterProperty((byte)0x8C, false, false, 12));
        addProperty(new EchonetCharacterProperty((byte)0x8D, false, false, 12));
        addProperty(new EchonetCharacterProperty((byte)0x8E, false, false, 4));
        addProperty(new EchonetCharacterProperty((byte)0x8F, true, false, 1));
        addProperty(new EchonetCharacterProperty((byte)0x93, true, false, 16));
        addProperty(new EchonetDateProperty());
        addProperty(new EchonetTimeProperty());
        addProperty(new EchonetCharacterProperty((byte)0x99, true, false, 2));
        addProperty(new EchonetCharacterProperty((byte)0x9A, false, false, 5));
        addProperty(new EchonetCharacterProperty((byte)0x9D, false, false, 17));
        addProperty(new EchonetCharacterProperty((byte)0x9E, false, false, 17));
        addProperty(new EchonetCharacterProperty((byte)0x9F, false, false, 17));
        
    }*/
    
    
    public byte getPosition(){
        try{
            return readProperty((byte)0x81)[0];
        }
        catch (RuntimeException e) {return 0x00;}
    }
    
    public boolean setPosition(byte position){
        return writeProperty((byte)0x81, new byte[]{position});
    }
    
    public short getWattConsumptionInstantaneous(){
        try{
            return this.getShort(readProperty((byte) 0x84));
        } catch (Exception e){   }
        return -1;
    }
    
    public boolean setWattConsumptionInstantaneous(short consumption){
        return writeProperty((byte) 0x84, ShortToBytes(consumption));
    }
    
    public int getWattConsumptionCumulative(){
        try{
            return this.getInt(readProperty((byte) 0x85));
        }catch (Exception e) {  }
         return -1;
    }
    
    public boolean setWattConsumptionCumulative(int consumption){
        return writeProperty((byte) 0x85, IntToBytes(consumption));
    }
    
    public byte[] getVendorSpecificErrorCode(){
        return readProperty((byte)0x86);
    }
    
    public boolean setVendorSpecificErrorCode(byte[] propertycode){
        return writeProperty((byte) 0x86, propertycode);
    }
    
    public byte getEnergyConsumptionLimitPercentage()
    {
        try{
            return readProperty((byte) 0x87)[0];
        }
        catch (RuntimeException e){
        }
        return (byte) 0xFF; // as an error code?
    }
    
    public boolean setEnergyConsumptionLimitPercentage(byte limit){
        if(limit > 0x64)
            return true;
        else return writeProperty((byte)0x87, new byte[]{limit});
    }
    
    public byte getError(){
        try{
            return readProperty((byte)0x88)[0];
        }finally {
            return (byte)0xFF;
        }
    }
    
    public boolean setError(boolean error){
        return writeProperty((byte)0x88, new byte[]{ (byte) (error? 0x41 : 0x42) });
    }
    
    public byte getEnergyConservationMode(){
        try{
            return (readProperty((byte)0x8F)[0]); 
        } catch (RuntimeException e) { }
         return (byte) 0xFF;
    }
    
    public boolean setEnergyConservationMode(boolean onoff){
        return writeProperty((byte) 0x8F, new byte[]{ (byte) (onoff? 0x41 : 0x42) } );
    }
    
    public byte[] getPositionInformation(){
        return readProperty((byte) 0x93);
    }
    
    public boolean setPositionInformation(byte[] positioninfo){
        return writeProperty((byte) 0x93, positioninfo);
    }
    
    public byte[] getCurrentTime(){
        return readProperty((byte) 0x97);
    }
    
    public boolean setCurrentTime(byte [] time){
        if(checkArg(time,2)) return true;
        else return writeProperty((byte)0x97, time);
    }
    
    public byte[] getCurrentDate(){
        return readProperty((byte) 0x98);
    }
    
    public boolean setCurrentDate(byte [] date){
        if (checkArg(date,4)) return true;
        else return writeProperty((byte)0x98, date);
    }
    
    public short getEnergyConsumptionLimitWatt(){
        try{
            return getShort(readProperty((byte) 0x99));
        } catch (Exception e){ }
        return 0x00;
    }
    
    public boolean setEnergyConsumptionLimitWatt(short wattlimit){
        return writeProperty((byte) 0x99, ShortToBytes(wattlimit));
    }
    
    public byte[] getCumulativeWorkTime(){
        byte [] result = readProperty((byte) 0x9A);
            if(checkArg(result,5))
                return null;
        else return result;
    }
    
    public boolean setCumulativeWorkTime(byte[] totaltime){
        if(checkArg(totaltime, 5))
            return true;
        return writeProperty((byte) 0x9A, totaltime);
    }
        
}
