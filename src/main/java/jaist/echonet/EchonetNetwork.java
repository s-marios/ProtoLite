package jaist.echonet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
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
    private final byte[] receivebuffer = new byte[1500];
    private final byte[] sendbuffer = new byte[1500];

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

    private InetAddress getIPv4FromReasonableInterface() throws SocketException, UnknownHostException {
        for (Enumeration<NetworkInterface> netifs = NetworkInterface.getNetworkInterfaces(); netifs.hasMoreElements();) {
            NetworkInterface netif = netifs.nextElement();
            //pick an interface that up, supports multicast, is not virtual, is not loopback, is not point to point
            if (netif.isUp() && netif.supportsMulticast() && !netif.isVirtual() && !netif.isLoopback() && !netif.isPointToPoint()) {
                for (InterfaceAddress ifAddr : netif.getInterfaceAddresses()) {
                    InetAddress addr = ifAddr.getAddress();
                    if (addr instanceof Inet4Address) {
                        //gete the first IpV4 address
                        return addr;
                    }
                }
            }
        }
        //God have mercy..
        return InetAddress.getLocalHost();
    }

    private void setup(InetAddress address) {
        //setup socket
        boolean wasNull = address == null;
        try {
            this.local = address;
            if (this.local == null) {
                this.local = getIPv4FromReasonableInterface();
                Logging.getLogger().log(Level.INFO, "Defaulting to IP address: {0}", this.local);
            }

            if (local.getAddress().length == 4) {
                group = InetAddress.getByName(EchonetProtocol.ECHONETMULTICASTV4);
            }
            if (local.getAddress().length == 16) {
                group = InetAddress.getByName(EchonetProtocol.ECHONETMULTICASTV6);
                Logging.getLogger().log(Level.FINE, "Using IPv6");
            }

            dport = EchonetProtocol.ECHONETPORT;
            msocket = new MulticastSocket(EchonetProtocol.ECHONETPORT);

            //TODO TEST THIS!!!
            //SELECTS THE MULTICAST INTERFACE
            if (!wasNull) {
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
