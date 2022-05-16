/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaist.echonet.adapter;

import jaist.echonet.EOJ;
import jaist.echonet.EchonetNode;
import jaist.echonet.EchonetProperty;
import jaist.echonet.LocalEchonetObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haha
 */
class MiddlewareCommunicator implements Runnable {

    private ServerSocket socket;
    private Socket adapterfd;
    private Thread t;
    protected EchonetNode context;
    final Collection<MiddlewareJob> jobs;
    List<MiddlewareObject> objects;
    ByteBuffer bbin, bbout;
    byte fn;
    byte[] bbarr;
    Map<Byte, PacketHandler> handlers;
    PacketHandler defaulthandler;

    private boolean handleError(ByteBuffer bbin) {

        if (isErrorFCC(bbin)) {

        }
        return false;
    }

    class InitRequestHandler implements PacketHandler {

        @Override
        public boolean handlePacket(ByteBuffer bb) {
            boolean result = true;
            try {
                int length = bb.position();
                int dl = 0x000000FF & bb.get(5);
                bb.position(7);
                int neoj = 0x000000FF & bb.get();
                Map<EOJ, List<TupleEAL>> info = new HashMap<EOJ, List<TupleEAL>>();
                for (int j = 0; j < neoj; j++) {

                    byte[] eoj = new byte[3];
                    bb.get(eoj);
                    int nepc = 0x000000FF & bb.get();
                    List<TupleEAL> listeal = new ArrayList<TupleEAL>();
                    for (int i = 0; i < nepc; i++) {
                        byte[] eal = new byte[3];
                        bb.get(eal);
                        listeal.add(new TupleEAL(eal));
                    }
                    info.put(new EOJ(eoj), listeal);
                }
                for (Map.Entry<EOJ, List<TupleEAL>> entry : info.entrySet()) {
                    List<MiddlewareProperty> properties = new ArrayList<MiddlewareProperty>();
                    for (TupleEAL eal : entry.getValue()) {
                        properties.add(
                                new MiddlewareProperty(
                                        eal.getEpc(),
                                        eal.isReadable(),
                                        eal.isWritable(),
                                        eal.doesNotify(),
                                        eal.getLength(),
                                        EchonetProperty.UPTO
                                )
                        );

                    }
                    MiddlewareObject mobj;
                    mobj = new MiddlewareObject(entry.getKey(), properties, MiddlewareCommunicator.this);
                    MiddlewareCommunicator.this.context.registerEchonetObject(mobj);
                    System.out.println("");
                }
            } catch (BufferUnderflowException ex)  {
                //you've dun goofed. packet format is bad, reconsider
                result = false;
            } catch ( IllegalArgumentException ex) {
                result = false;
            }

            //send response, reuse bb.
            bb.put(3, (byte) 0xF0);
            bb.position(5);
            bb.putShort((short) 2);
            short rshort = 0;
            if (result == false) {
                rshort = (short) 0x0000FFFF;
            }
            bb.putShort(rshort);
            fillFCC(bb);
            sendByteBuffer(bb);
            return result;
        }

        @Override
        public byte getRegistrationKey() {
            return 0x70;
        }

    }

    class StatusAccessHandler implements PacketHandler {

        @Override
        public boolean handlePacket(ByteBuffer bb) {
            int size = bb.position();
            byte fn = bb.get(4);
            bb.position(7);
            byte[] eoj = new byte[3];
            bb.get(eoj);
            short result = bb.getShort();
            short length = bb.getShort();
            byte pc = bb.get();
            byte[] data = null;
            if (length > 1) {
                data = new byte[length - 1];
                bb.get(data);
            }
            MiddlewareJob job = findJob(eoj, pc, fn);
            if (job == null) {
                //non existing job
                System.out.println("Status Access: non existing job");
                return false;
            }
            synchronized (job) {
                //TODO: set propert job.result
                if (data != null) {
                    job.payload = data;
                }
                if (result == 0) {
                    job.result = MiddlewareJob.OK;
                } else {
                    job.result = MiddlewareJob.ERROR;
                }
                job.notifyAll();
            }
            return true;
        }

        @Override
        public byte getRegistrationKey() {
            return (byte) 0x90;
        }
    }

    class StatusNotificationHandler implements PacketHandler {

        @Override
        public boolean handlePacket(ByteBuffer bb) {
            bb.position(7);

            byte[] eoj = new byte[3];
            bb.get(eoj);
            short len = bb.getShort();
            byte epc = bb.get();
            byte[] data = new byte[len - 1];
            bb.get(data);

            LocalEchonetObject localobj = context.getEchonetObject(new EOJ(eoj));
            if (localobj == null) {
                System.out.println("unknown echonet object.");
                return false;
            }
            EchonetProperty exists = localobj.getProperty(epc);
            if (exists == null) {
                System.out.println("unknown echonet property");
                return false;
            }

            EchonetProperty property = new EchonetImmutableProperty(epc, data);
            context.makeNotification(localobj, property);
            //as far as we know, we have sent a notification command on the wire
            //reuse buffer and return good response.
            bb.put(3, (byte) 0x91);
            bb.position(5);
            bb.putShort((short) 0x0005);
            bb.putShort((short) 0x0000);
            bb.put(eoj);
            fillFCC(bb);
            sendByteBuffer(bb);
            return true;
        }

        @Override
        public byte getRegistrationKey() {
            return 0x11;
        }

    }

    class DefaultHandler implements PacketHandler {

        @Override
        public boolean handlePacket(ByteBuffer bb) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public byte getRegistrationKey() {
            throw new UnsupportedOperationException("I should not be registered"); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public MiddlewareCommunicator(InetAddress address, int port) {
        try {
            SocketAddress sockaddr = new InetSocketAddress(address, port);
            context = new EchonetNode(address);

            //socket = new ServerSocket(port);

            socket = new ServerSocket(port, 1, address);
            System.out.println("Socket created successfully. Port:" + port + " address: " + address.getHostAddress());
        } catch (IOException ex) {
            Logger.getLogger(MiddlewareCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not create socket, now exiting.");
            System.exit(1);
        }
        jobs = Collections.synchronizedList(new ArrayList<MiddlewareJob>());
        bbout = MappedByteBuffer.allocate(2048);
        bbin = MappedByteBuffer.allocate(2048);
        bbout.order(ByteOrder.BIG_ENDIAN);
        bbin.order(ByteOrder.BIG_ENDIAN);
        bbarr = new byte[2048];

        //init handlers
        handlers = new HashMap<Byte, PacketHandler>();
        PacketHandler handler;
        handler = new InitRequestHandler();
        handlers.put(handler.getRegistrationKey(), handler);
        handler = new StatusAccessHandler();
        handlers.put(handler.getRegistrationKey(), handler);
        handler = new StatusNotificationHandler();
        handlers.put(handler.getRegistrationKey(), handler);

        defaulthandler = new DefaultHandler();

        fn = 100;
    }

    @Override
    public void run() {
        while (true) {
            try {
                adapterfd = socket.accept();
                System.out.println("Remore address:" + adapterfd.getRemoteSocketAddress());
                adapterfd.setTcpNoDelay(true);
                receiveLoop();
            } catch (IOException ex) {
                Logger.getLogger(MiddlewareCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("TODO: Clean up in case of backend disconnect");
            }
        }
    }

    private void receiveLoop() throws IOException{
        while (true) {
            this.receive();
        }
    }

    public void start() {
        t = new Thread(this);
        t.start();
        context.start();

    }

    public void postJob(MiddlewareJob job) {
        synchronized (jobs) {
            jobs.add(job);
        }
        sendJob(job);

    }

    synchronized private void sendJob(MiddlewareJob job) {
        bbout.rewind();
        bbout.put((byte) 0x02);
        bbout.putShort((short) 0x0003);
        bbout.put((byte) 0x0010);
        byte fn1 = getFN();
        job.setFn(fn1);
        bbout.put(fn1);
        short dl = 6;
        short payloadlength = (short) (job.payload == null ? 0 : job.payload.length);
        bbout.putShort((short) (dl + payloadlength));
        bbout.put(job.eoj.getBytes());
        bbout.putShort((short) (payloadlength + 1));
        bbout.put((byte) job.propertycode);
        if (job.payload != null) {
            bbout.put(job.payload);
        }
        fillFCC(bbout);
        job.stamp();
        //get our bytes
        sendByteBuffer(bbout);

    }

    synchronized private void sendByteBuffer(ByteBuffer bb) {
        byte[] bytes = new byte[bb.position()];
        bb.rewind();
        bb.get(bytes);
        sendBytes(bytes);
    }

    synchronized private void sendBytes(byte[] bytes) {
        try {
            OutputStream out = adapterfd.getOutputStream();
            out.write(bytes);
        } catch (IOException ex) {
            Logger.getLogger(MiddlewareCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void receive() throws IOException{
        InputStream in = this.adapterfd.getInputStream();

            int bytesin = 0;
            while (bytesin < 3) {
                bytesin += in.read(bbarr, bytesin, 3 - bytesin);
            }
            if (bbarr[0] == 0x02 && bbarr[1] == 0 && bbarr[2] == 3) {
                //got the start of a packet, read more
                while (bytesin < 8) {
                    bytesin += in.read(bbarr, bytesin, 8 - bytesin);
                }
                //have the header down, read the data length
                bbin.rewind();
                bbin.put(bbarr, 0, 8);
                short dl = bbin.getShort(5);
                if (dl > 0) {
                    //read the rest of the packet
                    while (bytesin < dl + 8) {
                        bytesin += in.read(bbarr, bytesin, dl + 8 - bytesin);
                    }
                    bbin.put(bbarr, 8, dl);
                }

                byte cn = bbin.get(3);
                if (cn != (byte) 0x90) {
                    if (handleError(bbin)) {
                        return;
                    }
                }
                handlePacket(bbin);
                /*
                 if (isErrorFCC(bbin)) {
                 //packet has wrong fcc, drop it?
                 System.out.println("Bad fcc. dropping packet.");
                 } else {
                 handlePacket(bbin);
                 }*/
            }
        
    }

    private static boolean isErrorFCC(ByteBuffer bb) {
        int pos = bb.position();
        bb.position(1);
        byte fcc = 0;
        for (int i = 1; i < pos; i++) {
            fcc += bb.get();
        }
        return fcc == 0 ? false : true;
    }

    private static byte fillFCC(ByteBuffer bb) {
        int pos = bb.position();
        bb.position(1);
        byte fcc = 0;
        for (int i = 1; i < pos; i++) {
            fcc += bb.get();
        }
        bb.put((byte) -fcc);
        return (byte) -fcc;
    }

    void removeJob(MiddlewareJob job) {
        synchronized (jobs) {
            jobs.remove(job);
        }
    }

    byte getFN() {
        fn += 1;
        return fn;
    }

    public static void main(String[] args) {
        ByteBuffer bb = MappedByteBuffer.allocate(100);
        byte fcc;
        for (int i = 0; i < 7; i++) {
            bb.put((byte) 0);
        }
        fcc = fillFCC(bb);
        System.out.println("FCC: is zero? "
                + Byte.toString(fcc)
                + " " + (fcc == 0 ? "true" : "false")
        );
        bb.put((byte) 1);
        fcc = fillFCC(bb);
        System.out.println("FCC: is FF? "
                + Byte.toString(fcc)
                + " " + (fcc == -1 ? "true" : "false")
        );
        System.out.println("bb fcc ok? " + (isErrorFCC(bb) ? "false" : "true"));
        System.out.println("fcc raw: " + Byte.toString(bb.get(bb.position() - 1)));

    }

    private MiddlewareJob findJob(byte[] eoj, byte propertycode, byte fn) {
        for (MiddlewareJob job : jobs) {
            if (job.getFn() == fn
                    && job.propertycode == propertycode
                    && Arrays.equals(job.eoj.getBytes(), eoj)) {
                return job;
            }
        }
        return null;
    }

    private void handlePacket(ByteBuffer bb) {
        byte cn = bb.get(3);
        PacketHandler handler = handlers.get(cn);
        if (handler != null) {
            handler.handlePacket(bb);
        } else {
            defaulthandler.handlePacket(bb);
        }
    }
}
