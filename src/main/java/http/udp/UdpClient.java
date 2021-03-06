package http.udp;

import http.Packet;
import http.PacketListHandler;
import http.Response;
import logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.List;
import java.util.Objects;

public class UdpClient {

    Receiver receiver;
    Sender sender;
    Selector selector;
    InetAddress address;
    int port;
    PacketListHandler packetListHandler;
    boolean connectionEstablish = false;
    boolean sendingDone = false;
    final String NO_PAYLOAD = "NO_PAYLOAD";
    DatagramChannel channel;
    Integer nbOfPackets = -1;
//    long startingSequenceNumber = (long) (Math.random() * 100);
    long startingSequenceNumber = 0;
    long lastSequenceNumberReceived = -1;
    private boolean done = false;

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
    }

    public PacketListHandler getPacketListHandler() {
        return packetListHandler;
    }

    public void setConnectionEstablish(boolean connectionEstablish) {
        this.connectionEstablish = connectionEstablish;
    }

    public void setLastSequenceNumberReceived(long l){
        lastSequenceNumberReceived = l;
    }

    public boolean getConnectionEstablish(){
        return this.connectionEstablish;
    }

    public void handshake(){
        while(!receiver.listening){Thread.yield();}
        sendPacket(Packet.Type.SYN, startingSequenceNumber, String.valueOf(nbOfPackets));
        while(!connectionEstablish){
            try {
                Thread.sleep(200);
                if(!connectionEstablish) sendPacket(Packet.Type.SYN, startingSequenceNumber, String.valueOf(nbOfPackets));
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
        nbOfPackets = Math.floorDiv(serialized.length(), (Packet.MAX_LEN - Packet.MIN_LEN))+1;
        handshake();

        Logger.debug("Sending " + nbOfPackets + " packets.");
        Logger.debug("Currently at packets: " + lastSequenceNumberReceived);
        long lastSequence = nbOfPackets + lastSequenceNumberReceived + 1; // Last sequence number we receive is SYNACK (n+1) + nbOfPacket we sent + 1
        List<Packet> list = PacketListHandler.createPacketList(serialized, this.address, this.port, lastSequenceNumberReceived+1);

        // SYN seq. 0
        // DATA seq 3 (lastSequenceNbRec+1)
        // DATA FULLY SEND (lastSequenceNbRec+1 + nbOfPackets) -> 6
        // PACKET REQUEST -> 7
        if(getPacketListHandler() == null) {
           setPacketListHandler( new PacketListHandler(lastSequence + 2L));
        }

        for (Packet p: list){
            sendPacket(p);
        }
        if(lastSequenceNumberReceived > lastSequence) sendingDone = true;
        //ARQ stuff
        while (!sendingDone){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(lastSequenceNumberReceived > lastSequence) sendingDone = true; // send is done

            Logger.debug("Last sequence number received is: " + lastSequenceNumberReceived + ". Should be more or equal than: " + lastSequence);
            long packetToSend = lastSequenceNumberReceived;
            if(lastSequenceNumberReceived < list.get(0).getSequenceNumber()){
                packetToSend = list.get(0).getSequenceNumber();
            }
            Logger.debug("Trying to send packet#: " + packetToSend);
            for (Packet p : list){
                if (p.getSequenceNumber() == packetToSend) {
                    sendPacket(p);
                    break;
                }
            }
        }
        // wait for FIN
        while(!done){
            Thread.yield();
        }

       return Response.fromBufferedReader(packetListHandler.getPayload()).getSerialized();

    }

    public void setDone(boolean done) {
        Logger.println("Received response.");
        this.done = done;
    }

    public void setPacketListHandler(PacketListHandler packetListHandler) {
        this.packetListHandler = packetListHandler;
    }
}
