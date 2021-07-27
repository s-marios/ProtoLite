/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui;

import jaist.echonet.util.Utils;

/**
 *
 * @author marios
 */
class TableProperty{
    private int code;
    private int access;
    private byte[] rawdata;
    private String accessstring;
    private String codestring;
    private String datastring;
    
    TableProperty(){}
    
    TableProperty(int code, int access, byte[] rawdata){
        setCode(code);
        setAccess(access);
        setRawdata(rawdata);
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public final void setCode(int code) {
        this.code = code;
        this.setCodeString();
    }

    /**
     * @return the access
     */
    public int getAccess() {
        return access;
    }

    protected void setAccessString(){
        String represent = "";
        if((this.access & 0x01) != 0)
            represent+="readable ";
        if((this.access & 0x02) != 0)
            represent+="writeable ";
        if((this.access & 0x04) != 0)
            represent+="notifies";
        this.accessstring = represent;
    }
    
    public String getAccessString(){
        return accessstring;
    }
    /**
     * @param access the access to set
     */
    public final void setAccess(int access) {
        this.access = access;
        setAccessString();
    }

    /**
     * @return the rawdata
     */
    public byte[] getRawdata() {
        return rawdata;
    }

    /**
     * @param rawdata the rawdata to set
     */
    public final void setRawdata(byte[] rawdata) {
        this.rawdata = rawdata;
        this.setDataString();
    }

    private void setCodeString() {
        this.codestring = Utils.toHexString((byte) (this.code & 0x000000ff));
    }
    
    public String getCodeString(){
        return codestring;
    }
    
    private void setDataString() {
        this.datastring = Utils.toHexString(rawdata);
    }

    String getDataString() {
        return datastring;
    }
    
}
