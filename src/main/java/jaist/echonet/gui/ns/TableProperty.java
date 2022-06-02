package jaist.echonet.gui.ns;

import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
class TableProperty {

    private int code;
    private int access;
    private byte[] rawdata;
    private String accessstring;
    private String codestring;
    private String datastring;

    public static final int READABLE = 1;
    public static final int WRITEABLE = 2;
    public static final int NOTIFIES = 4;
    
    TableProperty(int code, int access, byte[] rawdata) {
        setCode(code);
        setAccess(access);
        setRawdata(rawdata);
    }

    public int getCode() {
        return code;
    }

    public final void setCode(int code) {
        this.code = code;
        this.setCodeString();
    }

    public int getAccess() {
        return access;
    }

    protected void setAccessString() {
        String represent = "";
        if ((this.access & 0x01) != 0) {
            represent += "readable ";
        }
        if ((this.access & 0x02) != 0) {
            represent += "writeable ";
        }
        if ((this.access & 0x04) != 0) {
            represent += "notifies";
        }
        this.accessstring = represent;
    }

    public String getAccessString() {
        return accessstring;
    }

    public final void setAccess(int access) {
        this.access = access;
        setAccessString();
    }

    public byte[] getRawdata() {
        return rawdata;
    }

    public final void setRawdata(byte[] rawdata) {
        this.rawdata = rawdata;
        this.setDataString();
    }

    private void setCodeString() {
        this.codestring = Utils.toHexString((byte) (this.code & 0x000000ff));
    }

    public String getCodeString() {
        return codestring;
    }

    private void setDataString() {
        this.datastring = Utils.toHexString(rawdata);
    }

    String getDataString() {
        return datastring;
    }

}
