package jaist.echonet.gui.ns;

import jaist.echonet.AbstractEchonetObject;

/**
 *
 * @author marios
 */
public class WrappedObject implements Comparable{

    public AbstractEchonetObject object;

    public WrappedObject(AbstractEchonetObject object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return ("Object: " + "0x" + object.getEOJ());
    }

    public AbstractEchonetObject getObject() {
        return object;
    }

    @Override
    public int compareTo(Object other) {
        return toString().compareTo(other.toString());
    }

}
