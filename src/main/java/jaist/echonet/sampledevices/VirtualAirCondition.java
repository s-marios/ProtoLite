package jaist.echonet.sampledevices;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jaist.echonet.AbstractEchonetObject;
import jaist.echonet.EchoEventListener;
import jaist.echonet.WriteEventAdapter;
import jaist.echonet.EchonetAnswer;
import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.EchonetDateProperty;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.EchonetTimeProperty;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.PropertyMemento;
import jaist.echonet.RemoteEchonetObject;
import jaist.echonet.config.VirtualAirconditionInfo;
import jaist.echonet.gui.SupportsAppend;
import jaist.echonet.wrappers.Aircondition;
import jaist.echonet.wrappers.LocalWriter;
import jaist.echonet.util.Utils;

/**
 * Implementation of a virtual air condition unit. This air condition unit 
 * outputs the results of its operation on an "appendable" object (implement the
 * {@link SupportsAppend} interface and pass it as an argument to the constructor.
 * <p>
 * What this airconditon does, is it intercepts commands and prints out on the
 * appendable the actions it is going to take. It uses a mix of:
 * <ul>
 * <li> overriding the read operation of custom properties</li>
 * <li> overriding the write operation of custom properties</li>
 * <li> write listeners </li>
 * <li> use of wrapper functions </li>
 * </ul> 
 * to implement its device logic. Usually, the write listener will intercept
 * the command, check its validity and apply the requested setting. However, at 
 * a second level, the written property is also backed up by an EchonetCharacterProperty,
 * that will be written for consistency. When a write request fails, a Set*_SNA
 * is returned to the originator of the request.
 * <p>
 * This air condition supports save states, by using {@link PropertyMemento}s as
 * a serialization mechanism.
 * <p>
 * Setting the temperature property of the aircondition will also end up setting the
 * corresponding "mode"temperature property, i.e. if the temperature is set 
 * when the operation mode is in COOL, then also the "cooling"temperature will
 * be set at the same time. However, the other way around is not happening
 * <p>
 * As final remarks, this code was developed in a short amount of time for demo 
 * purposes, so its quality is at best throwaway. Do not consider it as "best 
 * practice" or as a reference for correctness, just use it as a hint to see
 * what is possible.
 * 
 * @author Sioutis Marios
 */
public class VirtualAirCondition extends Aircondition {

    enum OpMode {

        AUTO(100),
        COOLING(120),
        HEATING(150),
        DEHUMIDIFY(80),
        FAN(50),
        OTHER(100);
        int power; //percentage

        OpMode(int powerpercent) {
            this.power = powerpercent;
        }

        int getPower() {
            return power;
        }
    }

    enum FlowAmount {

        LEAST(50),
        LESS(75),
        MEDIUM(100),
        HIGH(120),
        HIGHEST(150),
        AUTO(100);
        int power; //percentage

        FlowAmount(int powerpercent) {
            this.power = powerpercent;
        }

        int getPower() {
            return power;
        }
    }

    enum FlowDirection {

        UP,
        DOWN,
        CENTER,
        CENTERUP,
        CENTERDOWN,}
    private SupportsAppend appendable;
    private boolean autotemp = false;
    private int temperature;
    private byte dehumidtemperature;
    private byte heattemperature;
    private byte cooltemperature;
    private FlowAmount flowAmount = FlowAmount.AUTO;
    private FlowDirection flowDirection = FlowDirection.CENTER;
    private OpMode opMode = OpMode.AUTO;
    private boolean autodirection = true;
    private TimeKeeper timer = null;
    final byte[] proplist = {
        (byte) 0xb0,
        (byte) 0xb1,
        (byte) 0xb3,
        (byte) 0xb5,
        (byte) 0xb6,
        (byte) 0xb7,
        (byte) 0xa0,
        (byte) 0xa1,
        (byte) 0xa4,
        (byte) 0x90,
        (byte) 0x92,
        (byte) 0x94,
        (byte) 0x96
    };
    final String filename = "aircon.state";
    EchoEventListener listener = new EchoEventListener() {

        @Override
        public boolean processWriteEvent(EchonetProperty property) {
            appendable.append("Received property: "
                    + Utils.toHexString(property.getPropertyCode())
                    + " data: "
                    + Utils.toHexString(property.read()) + "\n");
            return true;
        }

        @Override
        public boolean processNotificationEvent(RemoteEchonetObject robject, EchonetProperty property) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void processAnswer(EchonetAnswer answer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
    EchoEventListener writelistener = new WriteEventAdapter() {

        @Override
        public boolean processWriteEvent(EchonetProperty property) {
            byte[] data = property.read();
            if (data == null || data.length == 0) {
                return true;
            }

            switch (property.getPropertyCode()) {
                case (byte) 0xb0: //mode setting.
                    if (data.length != 1) {
                        return false;
                    }
                    return setOpMode(data[0]);
                case (byte) 0xb1: //auto temp control
                    if (data.length != 1) {
                        return false;
                    }
                    return setAutoTemp(data[0]);
                case (byte) 0xb3: //temp setting
                    if (data.length != 1) {
                        return false;
                    }
                    return setTemp(data[0]);
                case (byte) 0xA0:
                    if (data.length != 1) {
                        return false;
                    }
                    return setAirAmount(data[0]);

                //generic check for the temperatures
                case (byte) 0xB5:
                case (byte) 0xB6:
                case (byte) 0xB7:
                    if (data.length != 1 || data[0] < 18 || data[0] > 31) {
                        appendable.appendln("Requested temperature out of bounds");
                        return false;
                    }
                    return true;

                case (byte) 0xA1:
                    if (data.length != 1) {
                        return false;
                    }
                    return setAutoDirection(data[0]);
                case (byte) 0xA4:
                    if (data.length != 1) {
                        return false;
                    }
                    return setAirDirection(data[0]);
                case (byte) 0x90:
                    if (data.length != 1) {
                        return false;
                    }
                    return startOnTimer(data[0]);
                case (byte) 0x94:
                    if (data.length != 1) {
                        return false;
                    }
                    return startOffTimer(data[0]);

                default:
                    //this is necessary!
                    //we want to allow events we don't handle be handled by anybody
                    //else that can.
                    return true;
            }
        }
    };

    public VirtualAirCondition(EchonetNode node, SupportsAppend appendable) {
        setAppendable(appendable);
        setup();
        this.getLocalEchonetObject().registerListener(listener);
        this.getLocalEchonetObject().registerListener(writelistener);
        this.registerSelfWithNode(node);
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimeKeeper(), 4000, 4000);
    }

    private class TimeKeeper extends TimerTask {

        final int ONTIMER = 1;
        final int OFFTIMER = 0;

        @Override
        public void run() {
            //appendable.appendln("Time keeper run");
            String out = "Timer Expired: Air condition will ";
            if (isTimerEnabled(getTimerOnStatus())) {
                //on timer is enabled.
                if (decreaseTimer(ONTIMER)) {
                    //aircondition must turn on
                    appendable.appendln(out + "TURN ON");
                    setTimerOnStatus((byte) 0x42);
                    setStatus(true);
                }
            }
            if (isTimerEnabled(getTimerOffStatus())) {
                //on timer is enabled.
                if (decreaseTimer(OFFTIMER)) {
                    //aircondition must turn on
                    appendable.appendln(out + "TURN OFF");
                    setTimerOffStatus((byte) 0x42);
                    setStatus(false);
                }
            }
        }

        private boolean decreaseTimer(int timer) {
            String out = "Time left: ";
            int minutes = 0;
            if (timer == ONTIMER) {
                minutes = convertRelativeTimeToMinutes(getTimerOnRelative());
                setTimerOnRelative(convertMinutesToRelativeTime(--minutes));
                appendable.appendln("ON timer: " + out + minutes + "minutes");

            }
            if (timer == OFFTIMER) {
                minutes = convertRelativeTimeToMinutes(getTimerOffRelative());
                setTimerOffRelative(convertMinutesToRelativeTime(--minutes));
                appendable.appendln("OFF timer: " + out + minutes + "minutes");

            }
            if (minutes <= 0) {
                return true;
            }
            return false;
        }
    }

    class OnOffProperty extends EchonetProperty {

        public OnOffProperty() {
            //super((byte) 0x80, true, true);
            super((byte) 0x80, true, true, true, 1, EchonetProperty.EXACT);
        }
        byte abyte = 0x31;

        @Override
        public byte[] read() {
            return new byte[]{abyte};
        }

        @Override
        public boolean write(byte[] data) {
            if (data == null || data.length != 1) {
                return true;
            }
            if (data[0] == (byte) 0x31) {
                abyte = data[0];
                switchOff();
                return false;
            }
            if (data[0] == (byte) 0x30) {
                abyte = data[0];
                switchOn();
                return false;
            }
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    class PowerConsumption extends EchonetProperty {

        PowerConsumption() {
            super((byte) 0x84, false, false);
        }

        @Override
        public byte[] read() {
            short energy = 0;
            if (getStatus() == false) //device is turned off. no energy consumption.
            {
                return ShortToBytes(energy);
            }
            energy = 100; //a "base energy", nothing really meaningfull
            //simple formula: base + (base * flowamount/100 * opMode/100 ) 
            energy += energy * flowAmount.getPower() * opMode.getPower() / 10000;
            return ShortToBytes(energy);
        }

        @Override
        public boolean write(byte[] data) {
            return true; // this is not writeable, an "error" occured
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    class TemperatureHandler extends EchonetCharacterProperty {
        private byte opmode;
        
        public TemperatureHandler(byte propcode, byte opmode) {
            super(propcode, true, false, 1);
            this.opmode = opmode;
        }

        @Override
        public boolean write(byte[] data) {
            if (VirtualAirCondition.this.getOperationMode() == this.opmode) {
                //black magic: to avoid a loop we handle the raw temperature property by bypassing any check mechanisms
                VirtualAirCondition.this.getLocalEchonetObject().getProperty((byte)0xB3).write(data);
                String output = " set to " + data[0] + " degrees";
                switch (this.getPropertyCode()){
                    case (byte) 0xb5: appendable.appendln("Heating temperature"+output); break;
                    case (byte) 0xb6: appendable.appendln("Cooling temperature"+output); break;
                    case (byte) 0xb7: appendable.appendln("Dehumidify temperature"+output); break;
                }
            }
            super.write(data);
            return false;
        }
    }

    private void saveState() {
        ObjectOutputStream oout;
        try {
            oout = new ObjectOutputStream(new FileOutputStream(filename));
            AbstractEchonetObject raw = this.getEchonetObject();
            for (byte opcode : proplist) {
                oout.writeObject(raw.getProperty(opcode).getMemento());
                debug("Write: ", raw.getProperty(opcode).getMemento());
            }
            oout.close();
            appendable.appendln("Save state: Successful");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, "The file was not found", ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, "IO error occured", ex);
            appendable.appendln("IO Error: failed to save state");
            return;
        }
    }

    private void debug(String string, PropertyMemento memento) {
        appendable.appendln(string + Utils.toHexString(memento.getPropertyCode()) + " " + Utils.toHexString(memento.read()));
    }

    private void loadState() {
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(new FileInputStream(filename));
            PropertyMemento state;
            for (byte opcode : proplist) {
                try {
                    state = (PropertyMemento) oin.readObject();
                    debug("Read: ", state);
                    this.getEchonetObject().getProperty(state.getPropertyCode()).write(state.read());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            appendable.appendln("Load state: Successful");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oin.close();
            } catch (IOException ex) {
                Logger.getLogger(VirtualAirCondition.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                //do nothing
            }
        }
    }

    private void setup() {
        this.rawobject = new LocalEchonetObject(new VirtualAirconditionInfo());
        this.rawobject.addProperty(new EchonetDateProperty());

        this.rawobject.addProperty(new OnOffProperty());
        this.rawobject.addProperty(new EchonetTimeProperty());
        this.rawobject.addProperty(new PowerConsumption());
        this.rawobject.addProperty(new TemperatureHandler((byte) 0xb5,(byte) 0x42) );
        this.rawobject.addProperty(new TemperatureHandler((byte) 0xb6,(byte)0x43)) ;
        this.rawobject.addProperty(new TemperatureHandler((byte) 0xb7, (byte)0x44));
        this.writer = new LocalWriter((LocalEchonetObject) this.rawobject);
        this.getLocalEchonetObject().updatePropertyMap();
    }

    /**
     * @return the appendable
     */
    public SupportsAppend getAppendable() {
        return appendable;
    }

    /**
     * @param appendable the appendable to set
     */
    public void setAppendable(SupportsAppend appendable) {
        this.appendable = appendable;
        appendable.append("Virtual Aircondition: attatched to output\n");
    }

    private void switchOn() {
        appendable.append("Aircondition is switched on \n");
        this.loadState();
    }

    private void switchOff() {
        appendable.append("Aircondition is switched off \n");
        this.saveState();

    }

    private boolean setOpM(OpMode mode) {
        this.opMode = mode;
        appendable.appendln("Operation mode set to: " + mode);
        return true;
    }

    private boolean setOpMode(byte mode) {
        switch (mode) {
            case 0x41:
                return setOpM(OpMode.AUTO);
            case 0x42:
                return setOpM(OpMode.COOLING);
            case 0x43:
                return setOpM(OpMode.HEATING);
            case 0x44:
                return setOpM(OpMode.DEHUMIDIFY);
            case 0x45:
                return setOpM(OpMode.FAN);
            default:
                appendable.appendln("Operation mode: bad request");
                return false;
        }
    }

    private boolean setAutoTemp(byte auto) {
        if (auto == 0x41) {
            this.autotemp = true;
            appendable.append("Temperature mode set to: Auto\n");
            return true;
        }
        if (auto == 0x42) {
            this.autotemp = false;
            appendable.append("Temperature mode set to: Manual\n");
            return true;
        }
        appendable.appendln("Temperature mode: bad request (hex : " + Utils.toHexString(auto) + ")");
        return false;
    }

    private boolean setTemp(byte temp) {
        if (temp < 18 || temp > 30) {
            appendable.appendln("Requested Temperature is out of the specification (" + temp + " degrees).");
            return false;
        }
        this.temperature = temp;
        appendable.append("Temperature set to: " + temp + " degrees\n");
        switch (opMode) {
            case COOLING:
                return setCoolTemp(temp);
            case HEATING:
                return setHeatTemp(temp);
            case DEHUMIDIFY:
                return setDehumidTemp(temp);
        }
        return true;
    }

    private boolean setCoolTemp(byte temp) {
        if (temp < 0 || temp > 50) {
            appendable.appendln("Requested Cooling Temperature is out of the specification (" + temp + " degrees).");
            return false;
        }
        this.cooltemperature = temp;
        appendable.append("Cooling temperature set to: " + temp + " degrees\n");
        return true;
    }

    private boolean setHeatTemp(byte temp) {
        if (temp < 0 || temp > 50) {
            appendable.appendln("Requested Heating Temperature is out of the specification (" + temp + " degrees).");
            return false;
        }
        this.heattemperature = temp;
        appendable.append("Heating temperature set to: " + temp + " degrees\n");
        return true;
    }

    private boolean setDehumidTemp(byte temp) {
        if (temp < 0 || temp > 50) {
            appendable.appendln("Requested Dehumidify Temperature is out of the specification (" + temp + " degrees).");
            return false;
        }
        this.dehumidtemperature = temp;
        appendable.append("Dehumidify temperature set to: " + temp + " degrees\n");
        return true;
    }

    private boolean setAirA(FlowAmount flowa) {
        flowAmount = flowa;
        appendable.appendln("Air flow has been set to: " + flowAmount);
        return true;
    }

    private boolean setAirAmount(byte amount) {
        switch (amount) {
            case 0x31:
                return setAirA(FlowAmount.LEAST);
            case 0x32:
            case 0x33:
                return setAirA(FlowAmount.LESS);
            case 0x34:
            case 0x35:
                return setAirA(FlowAmount.MEDIUM);
            case 0x36:
            case 0x37:
                return setAirA(FlowAmount.HIGH);
            case 0x38:
                return setAirA(FlowAmount.HIGHEST);
            case 0x41:
                return setAirA(FlowAmount.AUTO);
            default:
                appendable.appendln("Air flow: bad request");
                return false;
        }
    }

    private boolean setAutoDirection(byte autodirect) {

        final String out = "Auto flow direction set to: ";
        switch (autodirect) {
            case 0x41:
                appendable.appendln(out + "TRUE");
                this.autodirection = true;
                return true;
            case 0x42:
                appendable.appendln(out + "FALSE");
                this.autodirection = false;
                return true;
            default:
                appendable.appendln(out + "Bad request");
                return false;
        }
    }

    private boolean setAirD(FlowDirection flowd) {
        this.flowDirection = flowd;
        appendable.appendln("Air direction set to: " + flowDirection);
        return true;
    }

    private boolean setAirDirection(byte direction) {

        switch (direction) {
            case 0x41:
                return setAirD(FlowDirection.UP);
            case 0x42:
                return setAirD(FlowDirection.DOWN);
            case 0x43:
                return setAirD(FlowDirection.CENTER);
            case 0x44:
                return setAirD(FlowDirection.CENTERUP);
            case 0x45:
                return setAirD(FlowDirection.CENTERDOWN);
            default:
                appendable.appendln("Air direction: bad request");
                return false;
        }
    }

    private boolean startOnTimer(byte setting) {
        String out = "On timer setting: ";
        switch (setting) {
            case 0x41:
            case 0x43:
            case 0x44:
                int minutesleft = convertRelativeTimeToMinutes(getTimerOnRelative());
                if (minutesleft == 0) {
                    appendable.appendln(out + "Request ignored, time is set to zero");
                    return false;
                }
                if (minutesleft < 0) {
                    appendable.appendln(out + "Request ignored, invalid time requested");
                    return false;
                } else {
                    appendable.appendln(out + " ON timer enabled");
                    return true;
                }
            case 0x42:
                appendable.appendln(out + "ON timer disabled");
                return true;
            default:
                appendable.appendln(out + "bad request");
                return false;
        }
    }

    private boolean startOffTimer(byte setting) {
        String out = "Off timer setting: ";
        switch (setting) {
            case 0x41:
            case 0x43:
            case 0x44:
                int minutesleft = convertRelativeTimeToMinutes(getTimerOffRelative());
                if (minutesleft <= 0) {
                    appendable.appendln(out + "Request ignored, time is set to ");
                    return false;
                }
                if (minutesleft < 0) {
                    appendable.appendln(out + "Request ignored, invalid time requested");
                    return false;
                } else {
                    appendable.appendln(out + " OFF timer enabled");
                    return true;
                }
            case 0x42:
                appendable.appendln(out + "OFF timer disabled");
                return true;
            default:
                appendable.appendln(out + "bad request");
                return false;
        }
    }

    private boolean isTimerEnabled(byte timersetting) {
        switch (timersetting) {
            case 0x41:
            case 0x43:
            case 0x44:
                return true;
            default:
                return false;
        }
    }
}
