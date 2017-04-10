/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.gui.aircontroller;

import jaist.echonet.wrappers.AbstractObjectWrapper;

/**
 *
 * @author marios
 */
public abstract class AbstractPropertyHandler implements PropertyHandler{
    
    protected AbstractObjectWrapper object;
    byte opcode;
    protected AcceptsQueryResults output;

    public AbstractPropertyHandler(AbstractObjectWrapper wrapper, byte opcode) {
        this.opcode = opcode;
        this.object = wrapper;
    }

    /**
     * @return the output
     */
    @Override
    public AcceptsQueryResults getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    @Override
    public void setOutput(AcceptsQueryResults output) {
        this.output = output;
    }

    @Override
    public byte getOpCode(){
        return opcode;
    }
    
    @Override
    public int compareTo(PropertyHandler t) {
        return this.opcode - t.getOpCode();
    }
    
    
    
}
