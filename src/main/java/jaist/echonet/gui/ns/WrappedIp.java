package jaist.echonet.gui.ns;

import java.net.InetAddress;
import java.util.Arrays;

/**
 *
 * @author smarios <smarios@jaist.ac.jp>
 */
public class WrappedIp implements Comparable {
    public InetAddress addr;
    
    public WrappedIp(InetAddress addr) {
        this.addr = addr;
    }
    
    public byte[] asBytes() {
        return addr.getAddress();
    }
    
    @Override
    public String toString() {
        return addr.getHostAddress();
    }

    @Override
    public int compareTo(Object other) {
        return Arrays.compare(asBytes(), ((WrappedIp) other).asBytes());
    }
}
