package jaist.echonet;

/**
 * The ECHONET payload creator of this framework. Main use is to call various
 * set methods to construct the payload of an echonet lite packet. Get the final
 * payload using <code>getPayload()</code>
 *
 * @author Sioutis Marios
 */
class EchonetPayloadCreator extends EchonetPayload {

    private short tid = 1;
    private int whichopc;

    public void setSEOJ(EOJ eoj) {
        byte echoclassgroup = eoj.getClassGroupCode();
        byte echoclass = eoj.getClassCode();
        byte instance = eoj.getInstanceCode();
        setSEOJ(echoclassgroup, echoclass, instance);
    }

    public void setSEOJ(byte echoclassgroup, byte echoclass, byte instance) {
        pushAndSetPosition(pos_seoj);
        bbuf.put(echoclassgroup)
                .put(echoclass)
                .put(instance);
        popPosition();
    }

    public void setDEOJ(EOJ eoj) {
        byte echoclassgroup = eoj.getClassGroupCode();
        byte echoclass = eoj.getClassCode();
        byte instance = eoj.getInstanceCode();
        setDEOJ(echoclassgroup, echoclass, instance);
    }

    public void setDEOJ(byte echoclassgroup, byte echoclass, byte instance) {
        pushAndSetPosition(pos_deoj);
        bbuf.put(echoclassgroup)
                .put(echoclass)
                .put(instance);
        popPosition();
    }

    public void setESV(ServiceCode esv) {
        this.setESV((byte) esv.getOpcode());
    }

    public void setESV(byte esv) {
        pushAndSetPosition(pos_esv);
        bbuf.put(esv);
        popPosition();
    }

    protected void increaseOPC() {
        pushAndSetPosition(whichopc);
        bbuf.array()[whichopc]++;
        popPosition();
    }

    public EchonetPayloadCreator writeOperand(byte opcode, byte[] opdata) {
        increaseOPC();
        bbuf.put(opcode);
        if (opdata == null || opdata.length == 0) {
            bbuf.put((byte) 0);
        } else {
            bbuf.put((byte) opdata.length);
            //TODO this breaks if total data more than maxsize, consider use
            //of exceptions here.
            bbuf.put(opdata);
        }
        return this;
    }

    public byte[] getPayload() {
        byte[] payload = new byte[bbuf.position()];
        bbuf.rewind();
        bbuf.get(payload);
        return payload;
    }

    public void resetCreator(short tid) {
        //this is usefull when we're crafting a reply and want the reply tid
        //in the packet
        this.whichopc = pos_opc;
        bbuf.array()[pos_opc] = 0;
        bbuf.rewind();
        bbuf.put(ehd1).put(ehd2).putShort(tid);
        bbuf.position(pos_epc1);
    }

    public void resetCreator() {
        //to "reset" the creator, we reset the OPC as well as set
        //bbuf.position to the first property
        //make sure to call all other functions one by one.
        //Also, write the headers and increase transaction id
        //Intended for new packets.
        this.tid++;
        resetCreator(tid);
    }

    public short getCurrentTID() {
        return this.tid;
    }

    public void startOPC2() {
        this.pos_opc2 = bbuf.position();
        bbuf.put((byte) 0x00);
        whichopc = pos_opc2;
    }
}
