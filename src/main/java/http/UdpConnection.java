package http;

import logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

/*
 * Type
 * 0: DATA
 * 1: SYN
 * 2: SYN-ACK
 * 3: ACK
 */

public class UdpConnection {

    InetAddress address;
    int port;
    InetSocketAddress router;
    boolean connectionAccepted;
    final String NO_PAYLOAD = "NO_PAYLOAD";
    DatagramChannel channel;

    public UdpConnection(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.router = new InetSocketAddress("localhost", 3000);
        try {
            this.channel = DatagramChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public UdpConnection(InetAddress address, int port, DatagramChannel channel) {
        this.address = address;
        this.port = port;
        this.router = new InetSocketAddress("localhost", 3000);
        this.channel = channel;
    }

    long startHandShake(){
        Packet synack = sendPacketAndReceiveAnswer(1,0, NO_PAYLOAD);
        Logger.println(synack.toString());
        return finishHandShake(synack.getSequenceNumber());
    }

    long receiveHandShake(long seqNumber){
        sendPacket(2, ++seqNumber , NO_PAYLOAD);
        return seqNumber;
    }

    long finishHandShake(long seqNumber){
        sendPacket(3, ++seqNumber, NO_PAYLOAD);
        return seqNumber;
    }

    public void sendPacket(int type, long sequenceNumber, String payload){
        try {
            Logger.println("Sending packet number " + sequenceNumber + " at port " + port + " with address: " + this.address.getHostAddress());
            Packet packet = new Packet.Builder().setType(type)
                                                .setSequenceNumber(sequenceNumber)
                                                .setPortNumber(port)
                                                .setPeerAddress(address)
                                                .setPayload(payload.getBytes())
                                                .create();
            System.out.println("Sending: " + packet.toBuffer().limit());
            channel.send(packet.toBuffer(), router);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet p){
        try {
            channel.send(p.toBuffer(), router);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Packet sendPacketAndReceiveAnswer(int type, long sequenceNumber, String payload) {
        Packet answer = null;
        try {
            sendPacket(type,sequenceNumber,payload);

            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            Logger.debug("Waiting for the response");
            selector.select(5000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                Logger.println("No response after timeout");
                // return an empty response
            }

            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            buf.flip();
            answer = Packet.fromBuffer(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public Packet sendPacketAndReceiveAnswer(Packet p) {
        Packet answer = null;
        try {
            sendPacket(p);

            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            Logger.debug("Waiting for the response");
            selector.select(5000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                Logger.println("No response after timeout");
                // return an empty response
            }

            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            buf.flip();
            answer = Packet.fromBuffer(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public String send(String serialized){
        long sequenceNumber = startHandShake();
        List<Packet> list = PacketHandler.createPacketList(serialized, this.address, this.port, sequenceNumber);
        long lastSequence = sequenceNumber;
        for (Packet p: list){
            Packet ack = this.sendPacketAndReceiveAnswer(p);

            lastSequence = p.getSequenceNumber();
        }

        return "";
    }



    /*
              DatagramChannel channel = null;
              Response response = null;
              try {
                  Logger.debug("Creating socket at port " + port + " with address: " + this.address.getHostAddress());

                  if(this.method.equals(Method.POST) && this.body != null && !this.body.isEmpty()){
                      this.headers.put("Content-Length", String.valueOf(this.body.getBytes().length));
                  }

                  channel = DatagramChannel.open();
                  Packet packet = new Packet.Builder()
                          .setType(0)
                          .setSequenceNumber(1L)
                          .setPortNumber(port)
                          .setPeerAddress(address)
                          .setPayload(getSerialized().getBytes())
                          .create();
                  Logger.debug("Sending " + getSerialized() + " to router at " + router );
                  channel.send(packet.toBuffer(), router);

                  channel.configureBlocking(false);
                  Selector selector = Selector.open();
                  channel.register(selector, OP_READ);
                  Logger.debug("Waiting for the response");
                  selector.select(5000);
                  Set<SelectionKey> keys = selector.selectedKeys();
                  if(keys.isEmpty()){
                      Logger.println("No response after timeout");
                      // return an empty response
                  }

                  ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
                  SocketAddress router = channel.receive(buf);
                  buf.flip();
                  Packet resp = Packet.fromBuffer(buf);
                  Logger.println("Packet: " + resp);
                  Logger.println("Router: " + router);
                  String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
                  Logger.println("Payload: " +  payload);

                  keys.clear();

                  response = Response.fromBufferedReader(payload);
     */

}
