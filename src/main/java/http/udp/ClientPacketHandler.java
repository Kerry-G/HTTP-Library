package http.udp;

import http.Packet;
import logger.Logger;


public class ClientPacketHandler implements PacketHandler {

    Sender sender;
    UdpClient udpClient;

    public ClientPacketHandler(Sender sender, UdpClient udpClient) {
        this.sender = sender;
        this.udpClient = udpClient;
    }

    @Override public void run(Packet packet) {
        try {
            Logger.println("Received: " + packet.toString());
            final Packet.Type type = packet.getType();
            final long sequenceNumber = packet.getSequenceNumber();
            final byte[] payload = packet.getPayload();
            switch (type){
                case DATA:
                    Logger.debug("Data Packet Received");
                    break;
                case SYN:
                   Logger.debug("SYN received (should not happen)");
                    break;
                case ACKSYN:
                    Logger.debug("SYN-ACK received");
                    udpClient.connectionEstablish = true;
                    Packet p = packet.toBuilder()
                          .setType(Packet.Type.ACK)
                          .setSequenceNumber(sequenceNumber+1)
                          .create();
                    sender.send(p);
                    break;
                case ACK:
                    Logger.debug("ACK received");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
