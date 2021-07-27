/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.config;

import jaist.echonet.EOJ;

/**
 *
 * @author ymakino
 */
public class AirconditionFullInfo extends DeviceSuperClassInfo {
        
    @Override
    protected final void addProperties(){
        
        add((byte) 0xB0, true, true, true, 1);
        add((byte) 0xB1, true, true, false, 1);
        add((byte) 0xB2, true, true, false, 1);
        add((byte) 0xB3, true, true, false, 1);
        add((byte) 0xB4, true, true, false, 1);
        add((byte) 0xB5, true, true, false, 1);
        add((byte) 0xB6, true, true, false, 1);
        add((byte) 0xB7, true, true, false, 1);
        add((byte) 0xB8, true, false, false, 8);
        add((byte) 0xB9, true, false, false, 2);
        add((byte) 0xBA, true, false, false, 1);
        add((byte) 0xBB, true, false, false, 1);
        add((byte) 0xBC, true, false, false, 1);
        add((byte) 0xBD, true, false, false, 1);
        add((byte) 0xBE, true, false, false, 1);
        add((byte) 0xBF, true, true, false, 1);
        
        
        add((byte) 0xA0, true, true, false, 1);
        add((byte) 0xA1, true, true, false, 1);
        add((byte) 0xA3, true, true, false, 1);
        add((byte) 0xA4, true, true, false, 1);
        add((byte) 0xA5, true, true, false, 1);
        
        add((byte) 0xAA, true, false, false, 1);
        add((byte) 0xAB, true, false, false, 1);
        
        add((byte) 0xC0, true, true, false, 1);
        add((byte) 0xC1, true, true, false, 1);
        
        add((byte) 0xC2, true, true, false, 1);
        add((byte) 0xC4, true, true, false, 1);
        add((byte) 0xC6, true, false, false, 1);
        add((byte) 0xC7, true, true, false, 8);
        add((byte) 0xC8, true, false, false, 1);
        add((byte) 0xC9, true, true, false, 8);
        
        
        add((byte) 0xCA, true, false, false, 1);
        add((byte) 0xCB, true, true, false, 8);
        add((byte) 0xCC, true, true, false, 1);
        add((byte) 0xCD, true, false, false, 1);
        add((byte) 0xCE, true, true, false, 1);
        add((byte) 0xCF, true, true, false, 1);
        
        add((byte) 0x90, true, true, false, 1);
        add((byte) 0x91, true, true, false, 2);
        add((byte) 0x92, true, true, false, 2);
        add((byte) 0x94, true, true, false, 1);
        add((byte) 0x95, true, true, false, 2);
        add((byte) 0x96, true, true, false, 2);
    }

    @Override
    protected final void setObjectClass() {
        setClassEOJ(new EOJ((byte)0x01, (byte)0x30, (byte)0x00));
    }
}
