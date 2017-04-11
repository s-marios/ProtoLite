/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.samples;

import jaist.echonet.EOJ;
import jaist.echonet.EchonetCharacterProperty;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.LocalEchonetObject;
import jaist.echonet.gui.NetworkScanner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
public class TemperatureSensorApp {

    EchonetNode context;
    LocalEchonetObject temperaturesensor;
    byte numberofsensors = 1;
    static byte TEMPSENSORCLASSGROUP = 0x00;
    static byte TEMPSENSORCLASS = 0x11;

    public static void main(String args[]) {
        InetAddress address = null;
        try {
            if (args.length > 0) {
                address = InetAddress.getByName(args[0]);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        new TemperatureSensorApp(address);
    }

    public TemperatureSensorApp(InetAddress address) {
        context = new EchonetNode(address);
        context.registerEchonetObject(getTemperatureSensorInstance());
        Thread t = context.start();
        //prevent exit of program; t is a daemon thread.
        while (true) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(TemperatureSensorApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private LocalEchonetObject getTemperatureSensorInstance() {
        EOJ sensoreoj = new EOJ(TEMPSENSORCLASSGROUP, TEMPSENSORCLASS, numberofsensors++);
        List<EchonetProperty> properties = new ArrayList<>();
        properties.add(new EchonetCharacterProperty((byte) 0x80, true, false, true, 1, 0, new byte[]{0x30}));
        properties.add(new EchonetProperty((byte) 0xE0, true, false, true, 2, 1) {

            short temperature = 0;

            @Override
            public byte[] read() {
                byte[] result = new byte[2];
                //convert the temperature
                result[0] = (byte) (temperature >> 8);
                result[1] = (byte) (temperature & 0x00ff);
                System.out.println("Current temperature: " + temperature * 10 + "\n");

                //increase temperature for next time 
                temperature++;
                return result;
            }

            @Override
            public boolean write(byte[] data) {
                throw new UnsupportedOperationException("This will never be called");
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });

        LocalEchonetObject sensor = new LocalEchonetObject(sensoreoj, properties);
        return sensor;
    }
}
