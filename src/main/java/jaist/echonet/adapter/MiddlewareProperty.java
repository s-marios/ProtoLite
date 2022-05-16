/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.adapter;

import jaist.echonet.EchonetProperty;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class MiddlewareProperty extends EchonetProperty {

    private MiddlewareObject parent;

    public MiddlewareProperty(byte propcode, boolean isreadable, boolean iswriteable, boolean notifies, int capacity, int capacitypolicy) {
        super(propcode, isreadable, iswriteable, notifies, capacity, capacitypolicy);
    }

    MiddlewareProperty(TupleEAL eal){
        super(eal.getEpc(), eal.isReadable(), eal.isWritable(), eal.doesNotify(), eal.getLength(), UPTO);
    }
    
    @Override
    public byte[] read() {
        MiddlewareJob job = new MiddlewareJob(parent.getEOJ(), this.getPropertyCode(), null);
        this.waitOnJob(job);
        return job.payload;
        
    }

    @Override
    public boolean write(byte[] data) {
        
        MiddlewareJob job = new MiddlewareJob(parent.getEOJ(), this.getPropertyCode(), data);
        this.waitOnJob(job);
        if (job.isSuccess()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isEmpty() {
        System.out.println("hit isEmpty() for middleware property");
        return false;
    }

    public void registerParent(MiddlewareObject parent) {
        this.parent = parent;
    }

    private void waitOnJob(MiddlewareJob job) {
        parent.getCommunicator().postJob(job);

        synchronized (job) {
            long start = System.currentTimeMillis();
            long waittime = MiddlewareJob.WAITTIME;
            while (!job.isCompleted()) {
                try {
                    job.wait(waittime);
                    //just woke up
                    waittime -= System.currentTimeMillis() - start;
                    //is this necessary?
                    if (waittime <= 0) {
                        break;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MiddlewareProperty.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            job.isCompleted();
            parent.getCommunicator().removeJob(job);

        }

    }
}
