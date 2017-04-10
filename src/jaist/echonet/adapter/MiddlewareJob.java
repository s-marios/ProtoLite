/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.adapter;

import jaist.echonet.EOJ;

/**
 *
 * @author haha
 */
public class MiddlewareJob {

    EOJ eoj;
    int propertycode;
    byte[] payload;
    private byte fn;
    int result;

    public static final int PENDING = 1;
    public static final int OK = 2;
    public static final int ERROR = 4;
    public static final int TIMEOUT = 8;
    public static final long WAITTIME = 3000;
    private long timestamp;

    public MiddlewareJob(EOJ eoj, int propertycode, byte[] payload) {
        this.eoj = eoj;
        this.propertycode = propertycode;
        this.payload = payload;
        this.result = 1;
        fn = 0;
    }

    boolean isCompleted() {
        if (this.result == OK || this.result == ERROR) {
            return true;
        }
        if (System.currentTimeMillis() - timestamp >= WAITTIME) {
            this.result = TIMEOUT;
            return true;
        }
        return false;
    }

    public void stamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        if (this.result == MiddlewareJob.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the fn
     */
    public byte getFn() {
        return fn;
    }

    /**
     * @param fn the fn to set
     */
    public void setFn(byte fn) {
        this.fn = fn;
    }

}
