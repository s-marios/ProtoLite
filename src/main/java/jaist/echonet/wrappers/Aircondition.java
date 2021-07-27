package jaist.echonet.wrappers;

import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.RemoteEchonetObject;

/**
 * The wrapper class for an air condition unit.
 * 
 * @author Sioutis Marios
 */
public class Aircondition extends ClimateControlObject{
    public static Aircondition createLocalInstance(LocalEchonetObject obj) {
        return getLocalInstance(Aircondition.class, obj);
    }
    
    public static Aircondition createRemoteInstance(RemoteEchonetObject obj, AbstractEchonetObject whoasks) {
        return getRemoteInstance(Aircondition.class, obj, whoasks);
    }
    
    public byte getOperationMode(){
        byte [] result = readProperty((byte) 0xB0);
        if(result == null || result.length != 1)
            return 0;
        return result[0];
    }
    
    //TODO who will do the validation checking?
    public boolean setOperationMode(byte mode){
        return writeProperty((byte) 0xB0, new byte[]{mode});
    }
    
    public boolean getAutoTemperatureControl(){
        try {
            if (readProperty((byte) 0xB1)[0] == (byte) 0x41) {
                return true;
            }
        } catch (RuntimeException e) {/*do nothing*/}
        return false;
    }
    
    public boolean setAutoTemperatureControl(boolean auto){
        return writeProperty((byte) 0xB1, new byte[]{ (byte) (auto? 0x41 : 0x42)});
    }
    
    public byte getIntensiveMode(){
        try{
            return readProperty((byte) 0xB2)[0];
        }catch(RuntimeException e){
            return (byte) 0xFF;
        }
    }
    
    public boolean setIntensiveMode(byte mode){
        switch(mode){
            case (byte) 0x41:
            case (byte) 0x42:
            case (byte) 0x43:
                return writeProperty((byte)0xB2, new byte[]{mode});
            default:return true;
        }
    }
    
    public int getTemperature(){
        try{
            return 0x000000ff & readProperty((byte) 0xB3)[0];
        } catch (Exception e){
            return 0xfd;
        }
    }
    
    public boolean setTemperature(int temperature){
        if(temperature < 0 || temperature >  50)
            return true;
        return writeProperty((byte)0xB3, new byte[]{ (byte)(0x000000ff & temperature)});
    }
    
    public int getRelativeHumidityTarget(){
        try{
            return 0x000000ff & readProperty((byte) 0xB4)[0];
        } catch (Exception e){
            return 0xfd;
        }
    }
    
    public boolean setRelativeHumidityTarget(int humiditypercentage){
        if(humiditypercentage< 0 || humiditypercentage>100)
            return true;
        return writeProperty((byte) 0xB4, new byte[]{(byte) (0x000000ff & humiditypercentage)});
    }
    
    public int getTemperatureCoolingMode(){
        try{
            return 0x000000ff & readProperty((byte) 0xB5)[0];
        } catch (Exception e){
            return -1;
        }
    }
    
    public boolean setTemperatureCoolingMode(int temperature){
        if(temperature < 0 || temperature >  50)
            return true;
        return writeProperty((byte)0xB5, new byte[]{ (byte)(0x000000ff & temperature)});
    }
    
    public int getTemperatureHeatingMode(){
        try{
            return 0x000000ff & readProperty((byte) 0xB6)[0];
        } catch (Exception e){
            return -1;
        }
    }
    
    public boolean setTemperatureHeatingMode(int temperature){
        if(temperature < 0 || temperature >  50)
            return true;
        return writeProperty((byte)0xB6, new byte[]{ (byte)(0x000000ff & temperature)});
    }
    
    public int getTemperatureDehumidifyingMode(){
        try{
            return 0x000000ff & readProperty((byte) 0xB7)[0];
        } catch (Exception e){
            return -1;
        }
    }
    
    public boolean setTemperatureDehumidifyingMode(int temperature){
        if(temperature < 0 || temperature >  50)
            return true;
        return writeProperty((byte)0xB7, new byte[]{ (byte)(0x000000ff & temperature)});
    } 
    
    public int[] getEstimatedWattConsumption(){
        byte [] result = readProperty((byte) 0xB8);
        int [] consumptions = {-1, -1, -1, -1};
        if(result.length == 8){
            for(int i = 0; i<consumptions.length; i++)
                consumptions[i] = getShort( new byte[] {result[i*2], result[i*2 + 1]});
        }
        return consumptions;
    }
    
    public boolean setEstimatedWattConsumption(int[] consumptions){
        for(int i = 0; i< 4; i++){
            if ( consumptions[i] < 0 || consumptions[i] > 0x0000ffff)
                return true;
        }/*
        byte [] data = new byte[8];
        for(int i= 0; i<4; i++){
            Array.consumptions[i]
        }
        return writeProperty((byte) 0xB8, data);
         * 
         */
        return true;
        //TODO not supported yet
    }
    
    public int getMeasuredCurrent(){
        try{
            return  0x0000ffff & getShort(readProperty((byte) 0xB9));
        } catch(Exception e){
            return -1;
        }
    }
    
    public boolean setMeasuredCurrent(int current){
        short thecurrent =  (short)(0x0000ffff & current);
        if(thecurrent <0 || thecurrent > 0x0000FFFF)
            return true;
        return writeProperty((byte) 0xB9, ShortToBytes(thecurrent));
    }
    
    public int getRelativeHumidityObserved(){
        try{
            return (int) 0x000000ff & readProperty((byte) 0xBA)[0];
        } catch (RuntimeException e){
            return -1;
        }
    }
    
    public boolean setRelativeHumidityObserved(int humidity){
        return writeProperty((byte) 0xBA, new byte[]{(byte) (0x000000FF & humidity)});
    }
    
    public int getTemperatureObserved(){
        try{ return readProperty((byte) 0xBB)[0];}
        catch (RuntimeException e){ return 0x80000000;}
    }
    
    public boolean setTemeparutreObserved(int temperature){
        if(temperature < -127 || temperature > 125)
            return true;
        return writeProperty((byte) 0xBB, new byte[]{(byte)temperature});
    }
    
    public int getRemoteControlTemperature(){
        try{ return readProperty((byte) 0xBC)[0];}
        catch (RuntimeException e){ return -1;}
    }
    
    public boolean setRemoteControlTemperature(int temperature){
        if(temperature<0 || temperature > 50)
            return true;
        return writeProperty((byte) 0xBC, new byte[]{(byte) temperature});
    }
    
    //TODO find a better name for this
    public int getTemperatureExhaust(){
        try{ return readProperty((byte) 0xBD)[0];}
        catch (RuntimeException e){ return 0x80;}
    }
    
    public boolean setTemperatureExhaust(int temperature){
    if(temperature < -127 || temperature > 125)
            return true;
        return writeProperty((byte) 0xBD, new byte[]{(byte)temperature});       
    }
    
    public int getTemperatureOutside(){
        try{ return readProperty((byte) 0xBE)[0];}
        catch (RuntimeException e){ return 0x80;}
    }
    
    public boolean setTemperatureOutside(int temperature){
    if(temperature < -127 || temperature > 125)
            return true;
        return writeProperty((byte) 0xBE, new byte[]{(byte)temperature});       
    }
    
    public float getRelativeTemperatureSetting(){
        try{ return ((float)readProperty((byte) 0xBF)[0])/10;}
        catch (RuntimeException e){ return 0x80;}
    }
    
    public boolean setRelativeTemperatureSetting(float temperature){
    if(temperature < -12.7 || temperature > 12.5)
            return true;
        return writeProperty((byte) 0xBF, new byte[]{(byte)(temperature * 10)});       
    }
    
    public byte getAirFlowAmount(){
        try{ return readProperty((byte) 0xA0)[0];}
        catch (RuntimeException e){ return (byte)0x80;}
    }
    
    public boolean setAirFlowAmount(byte amount){
        switch(amount){
            case (byte) 0x41: //auto
            case (byte) 0x31: //more specific amounts
            case (byte) 0x32:
            case (byte) 0x33:
            case (byte) 0x34:
            case (byte) 0x35:
            case (byte) 0x36:
            case (byte) 0x37:
            case (byte) 0x38:    
                return writeProperty((byte)0xA0, new byte[]{amount});
            default:return true;
        }
    }
    
    public byte getAirFlowAuto(){
        try{ return readProperty((byte) 0xA1)[0];}
        catch (RuntimeException e){ return (byte)0x42;}
    }
    
    public boolean setAirFlowAuto(byte autosetting){
        if(autosetting < 0x41 || autosetting > 0x44)
            return true;
        return writeProperty((byte)0xA1, new byte[]{autosetting});
    }
    
    public byte getAirFlowSwing(){
        try{ return readProperty((byte) 0xA3)[0];}
        catch (RuntimeException e){ return (byte)0x31;}
    }
    
    public boolean setAirFlowSwing(byte swing){
        if(swing < 0x41 || swing > 0x44)
            if (swing != 0x31)
                return true;
        return writeProperty((byte)0xA3, new byte[]{swing});
    }
    
    public byte getAirFlowVerticalAngle(){
        try{ return readProperty((byte) 0xA4)[0];}
        catch (RuntimeException e){ return (byte)0x41;} //TODO really?
    }
    
    public boolean setAirFlowVerticalAngle(byte angle){
        if(angle < 0x41 || angle > 0x45)
            return true;
        return writeProperty((byte)0xA4, new byte[]{angle});
    }
    
    public byte getAirFlowHorizontalAngle(){
        try{ return readProperty((byte) 0xA5)[0];}
        catch (RuntimeException e){ return (byte)0xFF;} //TODO really?
    }
    
    public boolean setAirFlowHorizontalAngle(byte angle){
        //TODO error check?
        return writeProperty((byte) 0xA5, new byte[]{angle});
    }
    
    public byte getSpecialOperationState(){
        try{  return readProperty((byte) 0xAA)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;
    }
    
    public boolean setSpecialOperationState(byte mode){
        if (mode < 0x40 || mode > 0x43)
            return true;
        return writeProperty((byte) 0xAA, new byte[]{mode});
    }
    
    public byte getPriorityMode(){
        try{  return readProperty((byte) 0xAB)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;    
    }
    public boolean setPriorityMode(boolean mode){
        return writeProperty((byte) 0xAB, new byte[]{ (byte) (mode? 0x41 : 0x40 )});
    }
    
    public byte getVentilationMode(){
        try{  return readProperty((byte) 0xC0)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;
    }
    
    public boolean setVentilationMode(byte mode){
        if(mode < 0x41 || mode > 0x43)
            return true;
        return writeProperty((byte) 0xC0, new byte[]{mode});
    }
    
    public byte getDehumidifyMode(){
    try{  return readProperty((byte) 0xC1)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;
    }
    
    public boolean setDehumidifyMode(boolean mode){
        return writeProperty((byte) 0xC1, new byte[]{(byte) (mode? 0x41 : 0x42)});
    }
    
    public byte getVentilationAirFlowAmount(){
    try{  return readProperty((byte) 0xC2)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;
    }
    
    public boolean setVentilationAirFlowAmount(byte amount){
         switch(amount){
            case (byte) 0x41: //auto
            case (byte) 0x31: //more specific amounts
            case (byte) 0x32:
            case (byte) 0x33:
            case (byte) 0x34:
            case (byte) 0x35:
            case (byte) 0x36:
            case (byte) 0x37:
            case (byte) 0x38:    
                return writeProperty((byte)0xC2, new byte[]{amount});
            default:return true;
        }
    }
    
    public byte getDehumidifyAirFlowAmount(){
    try{  return readProperty((byte) 0xC4)[0];
        } catch(RuntimeException e){}
        return (byte)0xFF;
    }
    
    public boolean setDehumidifyAirFlowAmount(byte amount){
         switch(amount){
            case (byte) 0x41: //auto
            case (byte) 0x31: //more specific amounts
            case (byte) 0x32:
            case (byte) 0x33:
            case (byte) 0x34:
            case (byte) 0x35:
            case (byte) 0x36:
            case (byte) 0x37:
            case (byte) 0x38:    
                return writeProperty((byte)0xC4, new byte[]{amount});
            default:return true;
        }
    }
    
    public byte getAirCleaningAvailableMethods(){
        try{  return readProperty((byte) 0xC6)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;
    }
    
    public boolean setAirCleaningAvailableMethods(byte methods){
        return writeProperty((byte) 0xC6, new byte[]{methods});
    }
    
    //TODO do the C7 command
    /*
    public byte getAirCleaningSetting(){
        try{ return readProperty((byte) 0xC7)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;
    }
    
    public boolean setAirCleaningSetting(byte setting){
        return writeProperty((byte) 0xC7, new byte[]{setting});
    }
    */
    
    public byte getAirRefreshMethods(){
    try{  return readProperty((byte) 0xC8)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;
    }
    
    public boolean setAirRefreshMethods(byte methods){
        return writeProperty((byte) 0xC8 , new byte[]{methods});
    }
    
    //TODO do c9
    public byte getUnitCleaninghMethods(){
    try{  return readProperty((byte) 0xCA)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;
    }
    
    public boolean setUnitCleaningMethods(byte methods){
        return writeProperty((byte) 0xCA , new byte[]{methods});
    }
    
    //TODO do cb
    public byte getSpecialOperationMode(){
    try{  return readProperty((byte) 0xCC)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;    
    }
    
    public boolean setSpecialOperationMode(byte mode){
        if (mode <  0x45  )
            return true;
        return writeProperty((byte) 0xCC, new byte[]{mode});
    }
    
    public byte getSaamoMode(){
    try{  return readProperty((byte) 0xCE)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;    
    }
    
    public boolean setSaamoMode(byte mode){
        if (mode < 0x40 || mode > 0x42)
            return true;
        return writeProperty((byte) 0xCE, new byte[]{mode});
    }
    
    public byte getAirCleaningMode(){
    try{  return readProperty((byte) 0xCF)[0];
        } catch(RuntimeException e){}
        return (byte)0x00;    
    }
    
    public boolean setAirCleaningMode(boolean mode){
        return writeProperty((byte) 0xCF, new byte[]{(byte) (mode? 0x41 : 0x42)});
    }
    
    //timer functions. have lots in common, refactor them
    
    private byte getTimerStatus(byte property){
        try{  return readProperty(property)[0];
        } catch(RuntimeException e){}
        return (byte)0x42;        
    
    }
    
    public byte getTimerOnStatus(){
        return getTimerStatus((byte) 0x90);
    }
    public byte getTimerOffStatus(){
        return getTimerStatus((byte) 0x94);
    }
    
    private boolean setTimerStatus(byte timer, byte on){
    if(on < 0x41 || on > 0x44)
            return true;
        return writeProperty(timer, new byte[]{on});
    }
    
    public boolean setTimerOnStatus(byte on){
        return setTimerStatus((byte)0x90, on);
    }
    public boolean setTimerOffStatus(byte on){
        return setTimerStatus((byte)0x94, on);
    }
    
    public byte[] getTimerOnAbsolute(){
        return readProperty((byte)0x91);
    }
    
    public byte[] getTimerOffAbsolute(){
        return readProperty((byte)0x95);
    }
    
    private boolean setTimerAbsolute(byte timer, byte [] on){
         if (checkArg(on, 2))
            return true;
        if (on[0] < 0 || on[0] > 23)
            return true;
        if (on[1] < 0 || on[1] > 59)
            return true;
        return writeProperty(timer, on);
    }
    
    
    public boolean setTimerOnAbsolute(byte [] on){
        return setTimerAbsolute((byte) 0x91, on);
    }
    public boolean setTimerOffAbsolute(byte [] on){
        return setTimerAbsolute((byte) 0x95, on);
    }
    
    
    public byte[] getTimerOnRelative(){
        return readProperty((byte) 0x092);
    }
    public byte[] getTimerOffRelative(){
        return readProperty((byte) 0x096);
    }
    
    private boolean setTimerRelative(byte timer, byte [] on){
        if (checkArg(on, 2))
            return true;
        if (on[1] < 0 || on[1] > 59)
            return true;
        return writeProperty(timer, on);
    
    }
    
    public boolean setTimerOnRelative(byte [] on){
        return setTimerRelative((byte) 0x92, on);
    }
    
    public boolean setTimerOffRelative(byte [] on){
        return setTimerRelative((byte) 0x96, on);
    }
    
    public int convertRelativeTimeToMinutes(byte [] relativetime){
        if(relativetime == null || relativetime.length != 2)
            return -1;
        if(relativetime[1] > 0x3B)
            return -1;
        int minutes = 0x000000ff & relativetime[1];
        int hours = 0x000000ff & relativetime[0];
        minutes+= hours * 60;
        return minutes;
    }
    
    public byte[] convertMinutesToRelativeTime(int minutes){
        if (minutes < 0)
                return null;
        byte [] relative = {0 ,0};
        relative[1] = (byte)(minutes % 60);
        relative[0] = (byte)(minutes / 60);
        return relative;
    }
    
}
