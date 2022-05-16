/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jaist.echonet.adapter;

import jaist.echonet.EchonetProperty;

/**
 *
 * @author haha
 */
public class EchonetImmutableProperty extends EchonetProperty{
    byte [] data;

    public EchonetImmutableProperty(byte propcode, byte [] data){
        super(propcode, true, false, false);
        this.data = data;
    }
    
    @Override
    public byte[] read() {
        return data;
    }

    @Override
    public boolean write(byte[] data) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
}
