package http.udp;

import http.Packet;
import logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

/*
 * Type
 * 0: DATA
 * 1: SYN
 * 2: SYN-ACK
 * 3: ACK
 */

public class UdpClient {

    Receiver receiver;
    Sender sender;
    Selector selector;
    InetAddress address;
    int port;
    InetSocketAddress router;
    boolean connectionEstablish = false;
    final String NO_PAYLOAD = "NO_PAYLOAD";
    DatagramChannel channel;
    Integer nbOfPackets;

    public UdpClient(InetAddress address, int port) {
        try {
            this.selector = Selector.open();
            this.channel = selector.provider().openDatagramChannel();
            this.sender = new Sender(channel);
            ClientPacketHandler clientPacketHandler = new ClientPacketHandler(this.sender, this);
            this.receiver = new Receiver();
            receiver.bind(channel);
            receiver.setPacketHandler(clientPacketHandler);
            receiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.address = address;
        this.port = port;
        this.router = new InetSocketAddress("localhost", 3000);
    }

    public void setConnectionEstablish(boolean connectionEstablish) {
        this.connectionEstablish = connectionEstablish;
    }

    public boolean getConnectionEstablish(){
        return this.connectionEstablish;
    }

    public void handshake(){
        while(!receiver.listening){Thread.yield();}
        sendPacket(Packet.Type.SYN, 0, String.valueOf(nbOfPackets));
        while(!connectionEstablish){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(Packet.Type type, long sequenceNumber, String payload){
        try {
            Logger.println("Sending packet number " + sequenceNumber + " at port " + port + " with address: " + this.address.getHostAddress());
            Packet packet = new Packet.Builder().setType(type)
                                                .setSequenceNumber(sequenceNumber)
                                                .setPortNumber(port)
                                                .setPeerAddress(address)
                                                .setPayload(payload.getBytes())
                                                .create();

            sender.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet p){
        try {
            sender.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String send(String serialized)  {
        handshake();
        return "done";
    }

//
//    public String send(String serialized){
//        nbOfPackets = Math.floorDiv(serialized.length(), (Packet.MAX_LEN - Packet.MIN_LEN))+1;
//        long sequenceNumber = startHandShake();
//
//        ARQ(serialized, sequenceNumber);
//
//        Logger.println("done sending");
//
//        //should listen in a loop
//
//        String answer = "";
//        try {
//        channel.configureBlocking(false);
//        Selector selector = Selector.open();
//        channel.register(selector, OP_READ);
//        Logger.debug("Waiting for the response");
//        selector.select(5000);
//        Set<SelectionKey> keys = selector.selectedKeys();
//        if (keys.isEmpty()) {
//            Logger.println("No response after timeout");
//            // return an empty response
//        }
//
//        ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
//        SocketAddress router = channel.receive(buf);
//        buf.flip();
//            answer = new String(Packet.fromBuffer(buf).getPayload());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // we know that we send everythiung and we should receive response from server
//        return answer;
//    }
//
//    public void ARQ(String serialized, long sequenceNumber) {
//        List<Packet> list = PacketListHandler.createPacketList(serialized, this.address, this.port, sequenceNumber);
//        long lastSequence = sequenceNumber;
//
//        for (Packet p: list){
//            Packet ack = this.sendPacketAndReceiveAnswer(p);
//            lastSequence = ack.getSequenceNumber();
//        }
//
//
//        long lastSequenceNumberExcepted = list.get(list.size()-1).getSequenceNumber();
//
//        while(lastSequenceNumberExcepted != lastSequence){
//            Packet packetToSend = null;
//            for (Packet packet : list){
//                if (packet.getSequenceNumber() == lastSequence) {
//                    packetToSend = packet;
//                    break;
//                }
//            }
//            if (packetToSend == null) break;
//            Packet ack = this.sendPacketAndReceiveAnswer(packetToSend);
//            lastSequence = ack.getSequenceNumber();
//        }
//    }


}
