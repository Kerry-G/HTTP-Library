package http;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PacketHandler {

    public static List<Packet> createPacketList(String serialized, InetAddress inetAddress, int port, long sequenceNumber){
        List<Packet> l = new ArrayList<Packet>();

        int stringLength = serialized.length();
        int noOfChunks = Math.floorDiv(stringLength, Packet.MAX_LEN)+1;

        Packet packet;
        String chunk;
        for (int i=0; i<noOfChunks; i++){
            if (i == noOfChunks-1) { // if last then substring until end of string
                chunk = serialized.substring(i*Packet.MAX_LEN, stringLength);
            } else {
                // 0*Max to 1*Max, 1*Max to 2*Max, 2*Max to 3*Max, ...
                chunk = serialized.substring(i*Packet.MAX_LEN, (i+1)*Packet.MAX_LEN);
            }

            packet = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(sequenceNumber+i+1)
                    .setPortNumber(port)
                    .setPeerAddress(inetAddress)
                    .setPayload(chunk.getBytes())
                    .create();
            l.add(packet);
        }

        return l;
    }

}
