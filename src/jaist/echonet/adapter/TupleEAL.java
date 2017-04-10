/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.adapter;

/**
 *
 * @author haha
 */
final class TupleEAL {

    private byte epc;
    private byte access;
    private int length;

    private static final byte READABLE = (byte) 0x01;
    private static final byte WRITABLE = (byte) 0x02;
    private static final byte NOTIFIES = (byte) 0x04;
    
    TupleEAL(byte epc, byte access, byte length) {
        this.setEpc(epc);
        this.setAccess(access);
        //create from a "signed" byte type
        this.setLength(length);
    }

    TupleEAL(byte[] eal) {
        if (eal.length != 3) {
            throw new IllegalArgumentException("eal byte array size is not 3.");
        } else {
            this.setEpc(eal[0]);
            this.setAccess(eal[1]);
            this.setLength(eal[2]);
        }

    }

    /**
     * @return the epc
     */
    public byte getEpc() {
        return epc;
    }

    /**
     * @param epc the epc to set
     */
    public void setEpc(byte epc) {
        this.epc = epc;
    }

    /**
     * @return the access
     */
    public byte getAccess() {
        return access;
    }

    /**
     * @param access the access to set
     */
    public void setAccess(byte access) {
        this.access = access;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(byte length) {
        this.length = 0x000000FF & length;
    }

    public boolean isReadable() {
        int raccess = this.getAccess() & READABLE;
        return raccess != 0;
    }
    
    public boolean isWritable() {
        int raccess = this.getAccess() & WRITABLE;
        return raccess != 0;
    }

    public boolean doesNotify() {
        int raccess = this.getAccess() & NOTIFIES;
        return raccess != 0;
    }
}
