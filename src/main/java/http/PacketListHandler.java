package http;

import logger.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PacketListHandler {

    public static List<Packet> createPacketList(String serialized, InetAddress inetAddress, int port, long sequenceNumber){
        List<Packet> l = new ArrayList<Packet>();

        int stringLength = serialized.length();
        int noOfChunks = Math.floorDiv(stringLength, (Packet.MAX_LEN - Packet.MIN_LEN))+1;

        Packet packet;
        String chunk;
        for (int i=0; i<noOfChunks; i++){
            if (i == noOfChunks-1) { // if last then substring until end of string
                chunk = serialized.substring(i*(Packet.MAX_LEN - Packet.MIN_LEN), stringLength);
            } else {
                // 0*Max to 1*Max, 1*Max to 2*Max, 2*Max to 3*Max, ...
                chunk = serialized.substring(i*(Packet.MAX_LEN - Packet.MIN_LEN), (i+1)*(Packet.MAX_LEN - Packet.MIN_LEN));
            }

            packet = new Packet.Builder()
                    .setType(Packet.Type.DATA)
                    .setSequenceNumber(sequenceNumber+i+1)
                    .setPortNumber(port)
                    .setPeerAddress(inetAddress)
                    .setPayload(chunk.getBytes())
                    .create();
            l.add(packet);
        }

        return l;
    }

    private List<Packet> list;
    private long lastKnownSequenceNumber;
    final private long firstSequenceNumber;

    public PacketListHandler(long firstSequenceNumber){
        list = new ArrayList<Packet>();
        this.firstSequenceNumber = firstSequenceNumber;
        this.lastKnownSequenceNumber = firstSequenceNumber;
        Logger.debug("firstSequenceNumber: " + firstSequenceNumber);
    }

    public long getFirstSequenceNumber(){
        return firstSequenceNumber;
    }

    public long add(Packet p){
        // Keeps Packets sorted as they enter

        boolean isPacketInTheList = list.stream().anyMatch(packet -> packet.getSequenceNumber() == p.getSequenceNumber());
        if(!isPacketInTheList) list.add(p);
        list.sort(Comparator.comparingLong(Packet::getSequenceNumber));

        lastKnownSequenceNumber = firstSequenceNumber;
        for (Packet packet: list){ // ordered list
            if(packet.getSequenceNumber() == lastKnownSequenceNumber){
                lastKnownSequenceNumber++;
            }
        }


        return lastKnownSequenceNumber;
    }

    public String getPayload(){

        StringBuilder sb = new StringBuilder();
        for ( Packet p: list) {
            String s = new String(p.getPayload());
            sb.append(s);
        }
        return sb.toString();
    }

    public Integer size() {
        return list.size();
    }
}
