package jaist.echonet;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 * Common abstractions and definitions for the packet parser and packet
 * creators 
 * @author Sioutis Marios
 */
class EchonetPayload {

    protected void popPosition() {
        bbuf.position(temp_position);
    }

    protected void pushAndSetPosition(int toPosition) {
        temp_position = bbuf.position();
        bbuf.position(toPosition);
    }
    protected int temp_position = 0;
    
    public EchonetPayload() {
    }
    // 255 of data + 4 bytes ehd1&2 + tid + 6 sdeoj +2 opc esv
    //TODO ask kawaguchi about that.
    //This is echonet lite v1.00 limitation, changed it
    //protected static final int MAXLENGTH = 269;
    protected static final int MAXLENGTH = 10000;
    
    protected ByteBuffer bbuf = MappedByteBuffer.allocate(MAXLENGTH);
    //the payload length is equal to bbuf.limit();
    protected static final byte ehd1 = 0x10;
    protected static final byte ehd2 = (byte) 0x81;
    protected static final int pos_ehd1 = 0;
    protected static final int pos_ehd2 = 1;
    protected static final int pos_deoj = 7;
    protected static final int pos_epc1 = 12;
    protected static final int pos_esv = 10;
    protected static final int pos_opc = 11;
    protected static final int pos_seoj = 4;
    protected static final int pos_tid = 2;
    protected int pos_opc2 = 0;
    
}
