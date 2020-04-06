package http.udp;

import http.Packet;
import http.PacketListHandler;
import logger.Logger;

import java.io.IOException;


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
            udpClient.setLastSequenceNumberReceived(sequenceNumber);
            switch (type){
                case DATA:
                    Logger.debug("Data Packet Received");
                    handleDataPacket(packet);
                    break;
                case SYN:
                    Logger.debug("SYN received (should not happen)");
                    break;
                case ACKSYN:
                    Logger.debug("SYN-ACK received");
                    handleAckSynPacket(packet, sequenceNumber);
                    break;
                case ACK:
                    Logger.debug("ACK received");
                    break;
                case ERR:
                    Logger.println("[ERROR] Something happened.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFinPacket(Packet packet) {
        try {
            sender.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpClient.setDone(true);
    }

    private void handleAckSynPacket(Packet packet, long sequenceNumber) throws IOException {
        udpClient.connectionEstablish = true;
        Packet p = packet.toBuilder()
              .setType(Packet.Type.ACK)
              .setSequenceNumber(sequenceNumber+1)
              .create();
        sender.send(p);
    }

    private void handleDataPacket(Packet packet) throws IOException {
        if(new String(packet.getPayload()).equals("FIN")){
            handleFinPacket(packet);
            return;
        }
        final PacketListHandler packetListHandler = udpClient.getPacketListHandler();
        long sequenceNumber = packetListHandler.add(packet);
        Packet p = packet.toBuilder().setSequenceNumber(sequenceNumber).setType(Packet.Type.ACK).setPayload("NO_PAYLOAD").create();
        sender.send(p);
    }

}
