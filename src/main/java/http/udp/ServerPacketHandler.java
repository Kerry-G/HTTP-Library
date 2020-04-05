package http.udp;

import http.Packet;
import logger.Logger;

import java.io.IOException;

public class ServerPacketHandler implements PacketHandler {

    Sender sender;

    public ServerPacketHandler(Sender sender) {
        this.sender = sender;
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
                    Logger.debug("SYN received");
                    Packet p = packet.toBuilder()
                          .setType(Packet.Type.ACKSYN)
                          .setSequenceNumber((sequenceNumber+1))
                          .create();

                    sender.send(p);
                    break;
                case ACKSYN:
                    Logger.debug("SYN-ACK received (should not happen)");
                    break;
                case ACK:
                    Logger.debug("ACK received");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSyn(){

    }
}
