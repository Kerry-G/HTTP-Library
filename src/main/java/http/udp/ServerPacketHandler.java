package http.udp;

import http.*;
import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.IOException;
import java.util.List;

public class ServerPacketHandler implements PacketHandler {

    Sender sender;
    Server server;
    public ServerPacketHandler(Sender sender, Server server) {
        this.sender = sender;
        this.server = server;
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
                    if (!server.isConnectionActive(packet.getPeerAddress())) return;
                    handleDataPacket(packet);
                    break;
                case SYN:
                    Logger.debug("SYN received");
                    handleSynPacket(packet, sequenceNumber, payload);
                    break;
                case ACKSYN:
                    Logger.debug("SYN-ACK received (should not happen)");
                    if (!server.isConnectionActive(packet.getPeerAddress())) return;
                    break;
                case ACK:
                    Logger.debug("ACK received");
                    if (!server.isConnectionActive(packet.getPeerAddress())) return;
                    handleAckPacket(packet, sequenceNumber);
                    break;
            }

            if (!server.getServerConnectionHashMap().containsKey(packet.getPeerAddress())) return;
            ServerConnection sc = server.getServerConnectionHashMap().get(packet.getPeerAddress());

            if (sc.getPacketListHandler().size() == sc.getNbOfPacketsExpected()){
                Logger.debug(sc.getInetAddress().getHostAddress() + " has finish sending");
                handleRequest(sc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleAckPacket(Packet packet, long sequenceNumber) {
        final ServerConnection serverConnection = server.getServerConnectionHashMap().get(
                packet.getPeerAddress());
        serverConnection.setSequenceNumber(sequenceNumber);
    }


    private void handleRequest(ServerConnection sc) throws IOException {
        server.getServerConnectionHashMap().remove(sc.getInetAddress());
        server.addServerConnectionToQueue(sc);
    }

    private void handleDataPacket(Packet packet) throws IOException {
        final ServerConnection serverConnection = server.getServerConnectionHashMap().get(
                packet.getPeerAddress());
        serverConnection.setSequenceNumber(packet.getSequenceNumber());
        final PacketListHandler packetListHandler = serverConnection.getPacketListHandler();
        packetListHandler.add(packet);
        Packet p = packet.toBuilder().setType(Packet.Type.ACK).create();
        sender.send(p);
    }

    private void handleSynPacket(Packet packet, long sequenceNumber, byte[] payload) throws IOException {
        ServerConnection serverConnection = new ServerConnection();
        serverConnection.setInetAddress(packet.getPeerAddress());
        serverConnection.setPort(packet.getPeerPort());
        serverConnection.setSequenceNumber(sequenceNumber);
        serverConnection.setNbOfPacketsExpected(Integer.parseInt(new String(payload)));

        server.addConnection(serverConnection);

        Packet p = packet.toBuilder()
              .setType(Packet.Type.ACKSYN)
              .setSequenceNumber((sequenceNumber+1))
              .create();

        sender.send(p);
    }

}
