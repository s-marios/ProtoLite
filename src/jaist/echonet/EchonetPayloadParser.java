package jaist.echonet;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * The echonet lite packet parser of this framework. Tries to parse a packet,
 * performing validation along the way. Information of the received packet can 
 * be obtained using the various get methods.
 * 
 * @author Sioutis Marios
 */
class EchonetPayloadParser extends EchonetPayload {
    
    public enum errorcode {
        SUCCESS,
        NO_PACKET,
        BAD_EHD1,
        BAD_EHD2,
        PACKET_TOO_BIG,
        PACKET_TOO_SMALL,
        BAD_ESV,
        PACKET_SHORT,
        PACKET_GARBAGE,
        MISC
    }
    
    private errorcode errno = errorcode.SUCCESS;
    
    public EchonetPayloadParser(byte [] payload){
        this.setPayload(payload, (payload==null)?0:payload.length);
    }
    
    public EchonetPayloadParser(){
    
    }

    /**
     * @return the errno
     */
    public errorcode getErrno() {
        return errno;
    }
   
    public boolean validate(){
        errno= errorcode.SUCCESS;
        bbuf.rewind();
        //let's validate stuff..
        //validate header
        if(bbuf.get() != ehd1)
        {   
            errno = errorcode.BAD_EHD1; 
            return false;
        }
        
        if(bbuf.get() != ehd2)
        {   
            errno = errorcode.BAD_EHD2; 
            return false;
        }
        
        bbuf.getShort(); //TODO do something for tid
        
        //validate data
        //do nothing with the SEOJ/DEOJ jump to ESV
        //esv opcode is valid? or not?
        bbuf.position(pos_esv);
        if(!ServiceCode.isIncluded(bbuf.get()))
        {
            errno =errorcode.BAD_ESV;
            return false;
        }
        
        //validate that the length,number and data of the operands
        if(!verifyPropertiesLength())
            return false;
        
        if(bbuf.position() < bbuf.limit()){
        //there's still data on the buffer, check for setget
            switch(ServiceCode.getOpcode(this.getESV())){
                case SetGet:
                case SetGetI_SNA:
                case SetGet_Res:
                    break;
                default: {
                    errno = errorcode.PACKET_GARBAGE;
                    return false;
                }
            }
            
            //we have SETGET packet
            pos_opc2 = bbuf.position();
            if(!verifyPropertiesLength())
                return false;          
        }
        
        //last check.
        if(bbuf.position() != bbuf.limit()){
            errno = errorcode.PACKET_GARBAGE;
            return false;
        }
        bbuf.rewind();
        return true;
    }
    
    private boolean verifyPropertiesLength(){
        int opcount = 0x000000FF & bbuf.get();
        try{
            for(int i=0; i<opcount; i++){
                bbuf.get();//do nothing with the property
                int pdc = 0x000000FF & bbuf.get();
                bbuf.position(bbuf.position()+pdc);
            }
        }catch(BufferUnderflowException e){
            //Not enough data on the payload.
            errno= errorcode.PACKET_SHORT;
            return false;
        }catch(IllegalArgumentException e){
            //illegal access of data further into the buffer
            errno= errorcode.PACKET_GARBAGE;
            return false;
        }
        return true;
    }
    
    public boolean setPayload(byte [] payload, int payloadlength){
        //payload size sanity checking.
        if(payload == null || payloadlength < 0 ){
            errno = errorcode.NO_PACKET;
            return false;
        }
        
        
        if(payloadlength > MAXLENGTH){
            errno = errorcode.PACKET_TOO_BIG;
            return false;
        }
        if(payloadlength < 12){
            errno = errorcode.PACKET_TOO_SMALL;
            return false;
        }
        
        //Packet looks sane. Copy the contents to our backing array.
        System.arraycopy(payload, 0, this.bbuf.array(), 0, payloadlength);
        this.pos_opc2 = 0;
        bbuf.limit(payloadlength);
        this.validate();
        return true;
    }
    
    public EOJ getSEOJ(){
        byte [] eoj = new byte[3];
        this.pushAndSetPosition(pos_seoj);
        bbuf.get(eoj);
        this.popPosition();
        return new EOJ(eoj);
    }
    
    public EOJ getDEOJ(){
        byte [] eoj = new byte[3];
        this.pushAndSetPosition(pos_deoj);
        bbuf.get(eoj);
        this.popPosition();
        return new EOJ(eoj);
    }
    
    public short getTID(){
        return bbuf.getShort(pos_tid);
    }
    
    public byte getESV(){
        return bbuf.get(pos_esv);
    }
    
    public byte getOPC(){
        return bbuf.get(pos_opc);
    }
    
    public byte getEHD1(){
        return bbuf.get(pos_ehd1);
    }
    
    public byte getEHD2(){
        return bbuf.get(pos_ehd2);
    }
    
    public byte getOPC2(){
        if(pos_opc2 == 0)
            return 0;
        else return bbuf.get(pos_opc2);
    }
    
    
    
    private List<EchonetProperty> getPropertyList(int whichopc){
        List<EchonetProperty> list = new ArrayList();
        bbuf.position(whichopc);
        int opc = bbuf.get();
        
        for(int opcount = 0; opcount < opc; opcount++){
            byte propcode = bbuf.get();
            //nasty bug... pdc was byte before and i'd sometimes get negative
            //array error
            int pdc = 0x000000ff & bbuf.get();
            byte [] pdata = new byte[pdc];
            bbuf.get(pdata);
            EchonetProperty property = new EchonetCharacterProperty(propcode, true, true, pdata);
            list.add(property);
        }
        return list;
    }
   
    
    public List<EchonetProperty> getPropertyList(){
        return getPropertyList(pos_opc);
    }
    
    public List<EchonetProperty> getSecondPropertyList() {
        switch(ServiceCode.getOpcode(this.getESV())) {
                case SetGet:
                case SetGetI_SNA:
                case SetGet_Res:
                    return getPropertyList(pos_opc2);
                default :return null;
                }
    }
    
    public void dumpPacket(){
        System.out.printf("EHD1: 0x%02x, EHD2: 0x%02x, TID: %02x, SEOJ: 0x%06x, DEOJ: 0x%06x\n",
                this.getEHD1(), this.getEHD2(), this.getTID(), this.getSEOJ().intValue(), this.getDEOJ().intValue());
        System.out.printf("ESV: 0x%02x (%s), OPC: %d\n",this.getESV(), ServiceCode.getOpcode(this.getESV()), this.getOPC());
        for(EchonetProperty property: this.getPropertyList())
            System.out.printf("EPC: 0x%02x, PDC: 0x%02x, EDT: %s\n",
                    property.getPropertyCode(),property.read().length,new String(property.read()));
    }
    
}
