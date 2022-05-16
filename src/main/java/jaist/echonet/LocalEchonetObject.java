package jaist.echonet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import jaist.echonet.config.DeviceInfo;
import jaist.echonet.config.PropertyInfo;

/**
 * A generator to get unused instance numbers associated to class codes
 *
 * @author ymakino
 */
class UnusedEOJGenerator {

    private HashMap<EOJ, Byte> usedEOJMap;

    public UnusedEOJGenerator() {
        usedEOJMap = new HashMap<>();
    }

    public EOJ generate(EOJ eoj) {
        byte unused = 1;
        EOJ ceoj = eoj.getClassEOJ();
        Byte b = usedEOJMap.get(ceoj);
        if (b != null) {
            unused = (byte) (b + 1);
        }

        usedEOJMap.put(ceoj, unused);
        return ceoj.getEOJWithInstanceCode(unused);
    }
}

/**
 * Representation of a local ECHONET object.
 *
 * @author Sioutis Marios
 */
public class LocalEchonetObject extends AbstractEchonetObject {

    private DeviceInfo deviceInfo;
    private static UnusedEOJGenerator generator = new UnusedEOJGenerator();

    /**
     * Constructor
     *
     * @param deviceInfo the configuration to initialize this object with. The
     * properties in this parameter will be translated to
     * {@link EchonetCharacterProperty} objects
     */
    public LocalEchonetObject(DeviceInfo deviceInfo) {
        super(generator.generate(deviceInfo.getClassEOJ()));
        this.deviceInfo = deviceInfo;

        int size = deviceInfo.size();
        for (int i = 0; i < size; i++) {
            PropertyInfo info = deviceInfo.getAtIndex(i);
            addProperty(new EchonetCharacterProperty(info.epc, info.readable, info.writable, info.notifies, info.size, info.capacitypolicy, info.data));
        }
        updatePropertyMap();
    }

    /**
     * Constructor without configuration, just raw properties as a collection.
     * Added to handle the requirements of the middleware adapter.
     *
     * @param eoj
     * @param properties A collection of the object properties initialized
     */
    public LocalEchonetObject(EOJ eoj, Collection<? extends EchonetProperty> properties) {
        super(eoj);
        for (EchonetProperty property : properties) {
            addProperty(property);
        }
        updatePropertyMap();
    }

    /**
     * Constructor with base configuration and extra properties. This is useful
     * for quickly adding the device super-class properties and EOJ using the
     * deviceInfo object, and furthermore customizing behavior with the
     * properties collection. If a property is both on the deviceInfo and
     * properties collection, the latter takes effect.
     *
     * @param deviceInfo see the documentation for the constructor with only
     * deviceInfo argument
     * @param properties A collection of EchonetProperty properties that
     * implement the behavior of this object.
     */
    public LocalEchonetObject(DeviceInfo deviceInfo, Collection<? extends EchonetProperty> properties) {
        super(generator.generate(deviceInfo.getClassEOJ()));
        this.deviceInfo = deviceInfo;

        int size = deviceInfo.size();
        for (int i = 0; i < size; i++) {
            PropertyInfo info = deviceInfo.getAtIndex(i);
            addProperty(new EchonetCharacterProperty(info.epc, info.readable, info.writable, info.notifies, info.size, info.capacitypolicy, info.data));
        }
        for (EchonetProperty property : properties) {
            addProperty(property);
        }
        updatePropertyMap();
    }

    /**
     * Computes the property maps for this object (properties 0x9d, 0x9e, 0x9f).
     * Should be called whenever a new property is added. This method will be
     * called once as the last part of the initialization of a local echonet
     * object. If more properties are added after that, this method must also be
     * called.
     */
    public final void updatePropertyMap() {
        Collection<EchonetProperty> props = getPropertyList();
        List<EchonetProperty> readable = new ArrayList<>();
        List<EchonetProperty> writeable = new ArrayList<>();
        List<EchonetProperty> notifies = new ArrayList<>();

        //Add 9d 9e 9f as dummies so that they are counted when we generate the
        //property maps
        addProperty(new EchonetCharacterProperty((byte) 0x9d, true, false, false, compilePropertyMap(notifies)));
        addProperty(new EchonetCharacterProperty((byte) 0x9e, true, false, false, compilePropertyMap(writeable)));
        addProperty(new EchonetCharacterProperty((byte) 0x9f, true, false, false, compilePropertyMap(readable)));

        for (EchonetProperty prop : props) {
            if (prop.isReadable()) {
                readable.add(prop);
            }
            if (prop.isWriteable()) {
                writeable.add(prop);
            }
            if (prop.doesNotify()) {
                notifies.add(prop);
            }
        }

        //replace the dummies with the real ones that have proper maps.
        addProperty(new EchonetCharacterProperty((byte) 0x9d, true, false, false, compilePropertyMap(notifies)));
        addProperty(new EchonetCharacterProperty((byte) 0x9e, true, false, false, compilePropertyMap(writeable)));
        addProperty(new EchonetCharacterProperty((byte) 0x9f, true, false, false, compilePropertyMap(readable)));
    }

    protected byte[] compilePropertyMap(List<EchonetProperty> properties) {
        byte[] map = new byte[properties.size() < 17 ? properties.size() + 1 : 17];
        map[0] = (byte) properties.size();
        if (properties.size() < 17) {
            //just enummerate the property codes.
            int i = 1;
            for (EchonetProperty property : properties) {
                map[i++] = property.getPropertyCode();
            }
        } else {
            //binary representation.
            for (EchonetProperty property : properties) {
                //1) extract witch byte we're interested in.
                int byteno = property.getPropertyCode() & (byte) 0xf;
                byteno++;
                //2) extract witch bit we must flip
                int bittoflip = property.getPropertyCode() & (byte) 0x70;
                bittoflip >>= 4;
                //3) to get the bit mask, we rol by the bit number
                byte mask = 1;
                mask <<= bittoflip;
                //4)or the mask with whatever results we have there.
                map[byteno] |= mask;
            }
        }
        return map;
    }

    /**
     * Gets the associated device information used during initialization.
     *
     * @return associated device info
     */
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * Writes the property with the given property code using the given data,
     * without consulting the write permissions. The write permissions represent
     * how the object is viewed from the "outside" world (i.e. other objects).
     * However, there's a need to change the data of the local object from the
     * device behaviour defining threads, so this privileged write operation is
     * necessary
     *
     * @param propertycode the property to write
     * @param data the data to write
     * @return true in case of error, false otherwise
     */
    public boolean adminWriteProperty(byte propertycode, byte[] data) {
        EchonetProperty property = properties.get(propertycode);
        if (property == null) {
            return true;
        }

        boolean error = property.write(data);
        if (!error && property.doesNotify()) {
            this.inform(property);
        }
        return error;
    }

    /**
     * Makes a read attempt for a property
     *
     * @param whoasks effectively ignored for local objects
     * @param copyfrom a property (usually a dummy one) that has the same
     * property code as the property that will be read
     * @return the data read
     */
    @Override
    public byte[] readProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom) {
        return readProperty(copyfrom.getPropertyCode());
    }

    //the next two functions are used by the echonode receiving thread. 
    //No one else should touch these!
    //TODO is this needed as public?
    /**
     * Unprivileged write operation. Respects the access rights of the property.
     *
     * @param whoasks ignored
     * @param property the code of the property to write
     * @param data the data to write as a byte array
     * @return true if an error occurred, false otherwise
     */
    public boolean writeProperty(AbstractEchonetObject whoasks, byte property, byte[] data) {
        return writeProperty(property, data);
    }

    /**
     * Unprivileged write operation. Respects the access rights of the property.
     *
     * @param whoasks ignored
     * @param copyfrom property to copy data and opcode from
     * @return true if an error occurred, false otherwise
     */
    @Override
    public boolean writeProperty(AbstractEchonetObject whoasks, EchonetProperty copyfrom) {
        return writeProperty(copyfrom);
    }

    /**
     * Read operation. Gets the data that this property holds
     *
     * @param whoasks ignored
     * @param property the property code of the property to be read
     * @return a byte array containing the data, may be null
     */
    @Override
    public byte[] readProperty(AbstractEchonetObject whoasks, byte property) {
        return readProperty(property);
    }

}
