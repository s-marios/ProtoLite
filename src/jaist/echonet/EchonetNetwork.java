package jaist.echonet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;

/**
 * An abstraction of the used network. In this case the network handles IPv4 and
 * IPv6 traffic. In the future there may be other types of networks, and even
 * simulated networks
 *
 * @author Sioutis Marios, ymakino
 */
class EchonetNetwork {

    private InetAddress local, group;
    private short dport;
    private MulticastSocket msocket;
    private byte[] receivebuffer = new byte[1500];
    private byte[] sendbuffer = new byte[1500];

    public InetAddress getLocalIP() {
        return this.local;
    }

    public InetAddress getGroupIP() {
        Logging.getLogger().log(Level.FINE, "xxx Group: {0}", this.group);
        return this.group;
    }

    public short getDestinationPort() {
        return this.dport;
    }

    public EchonetNetwork() {
        setup(null);
    }

    public EchonetNetwork(InetAddress address) {
        setup(address);
    }

    private void setup(InetAddress address) {
        //setup socket
        boolean wasNull = address == null;
        try {
            this.local = address;
            if (this.local == null) {
                this.local = InetAddress.getLocalHost();
            }

            if (local.getAddress().length == 4) {
                group = InetAddress.getByName(EchonetProtocol.ECHONETMULTICASTV4);
            }
            if (local.getAddress().length == 16) {
                group = InetAddress.getByName(EchonetProtocol.ECHONETMULTICASTV6);
                Logging.getLogger().log(Level.FINE, "Using IPv6");
            }

            //TODO it is possible to bind to specific interface by using InetSocektAddress as an arg to multicast socket.
            dport = EchonetProtocol.ECHONETPORT;
            msocket = new MulticastSocket(EchonetProtocol.ECHONETPORT);
            //System.out.println("Ip address bound to: " + this.ip.getHostAddress());

            //TODO TEST THIS!!!
            //SELECTS THE MULTICAST INTERFACE
            if (!wasNull){
                msocket.setInterface(local);
            }
            
            msocket.setReceiveBufferSize(1000000);
            // want to avoid multiple instances of echonet node,
            // have them fail
            msocket.setReuseAddress(false);
            msocket.joinGroup(group);
            msocket.setSoTimeout(100);
            msocket.setLoopbackMode(false);
        } catch (IOException iOException) {
            Logging.getLogger().log(Level.SEVERE, "Failed to set up the socket properly", iOException);
            System.exit(1);
        }
    }

    public synchronized void sendData(InetAddress destination, byte[] data) throws IOException {
        DatagramPacket dsend = new DatagramPacket(sendbuffer, sendbuffer.length);
        dsend.setPort(dport);
        dsend.setData(data);
        dsend.setAddress(destination);
        msocket.send(dsend);
    }

    public synchronized void sendEchonetPayload(InetAddress destination, EchonetPayloadCreator payload) throws IOException {
        sendData(destination, payload.getPayload());
    }

    public InetAddress recvEchonetPayload(EchonetPayloadParser payload) throws IOException {
        DatagramPacket drecv = new DatagramPacket(receivebuffer, receivebuffer.length);
        msocket.receive(drecv);
        Logging.getLogger().log(Level.FINE, "Packet from: {0}", drecv.getAddress().getHostAddress());
        Logging.getLogger().log(Level.FINE, String.format("Received packet lenght: %d %d\n", drecv.getLength(), drecv.getOffset()));
        payload.setPayload(drecv.getData(), drecv.getLength());
        return drecv.getAddress();
    }
}
