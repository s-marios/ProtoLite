/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jaist.echonet.adapter;

import java.nio.ByteBuffer;

/**
 *
 * @author haha
 */
public interface PacketHandler {
    boolean handlePacket(ByteBuffer bb);
    byte getRegistrationKey();
}
