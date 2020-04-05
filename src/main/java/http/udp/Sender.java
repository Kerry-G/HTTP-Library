package http.udp;

import http.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class Sender {

    DatagramChannel datagramChannel;
    InetSocketAddress router;

    public Sender(DatagramChannel channel) {
        this.datagramChannel = channel;
        this.router = new InetSocketAddress("localhost", 3000);
    }

    public void send(Packet packet) throws IOException {
        datagramChannel.send(packet.toBuffer(), router);
    }



}

