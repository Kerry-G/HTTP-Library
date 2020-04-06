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
                    if(server.getCurrentlyHandledServerConnection() == null && !server.isConnectionActive(packet.getPeerAddress())) return;
                    handleDataPacket(packet);
                    break;
                case SYN:
                    Logger.debug("SYN received");
                    handleSynPacket(packet);
                    break;
                case ACKSYN:
                    Logger.debug("SYN-ACK received (should not happen)");
                    if (!server.isConnectionActive(packet.getPeerAddress())) return;
                    break;
                case ACK:
                    Logger.debug("ACK received");
                    // check if server is active
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
        ServerConnection serverConnection = server.getServerConnectionHashMap().get(packet.getPeerAddress());
        if(serverConnection == null){
            serverConnection = server.getCurrentlyHandledServerConnection();
        }
        serverConnection.setSequenceNumber(sequenceNumber);
    }


    private void handleRequest(ServerConnection sc) throws IOException {
        server.getServerConnectionHashMap().remove(sc.getInetAddress());
        server.addServerConnectionToQueue(sc);
    }

    private void handleDataPacket(Packet packet) throws IOException {
        if(new String(packet.getPayload()).equals("FIN")){
            server.getCurrentlyHandledServerConnection().setFinish(true);
            return;
        }
        final ServerConnection serverConnection = server.getServerConnectionHashMap().get(
                packet.getPeerAddress());

        final PacketListHandler packetListHandler = serverConnection.getPacketListHandler();
        long sequenceNumber = packetListHandler.add(packet);
        serverConnection.setSequenceNumber(sequenceNumber);

        Packet p = packet.toBuilder().setType(Packet.Type.ACK).setSequenceNumber(sequenceNumber).setPayload("NO PAYLOAD").create();
        sender.send(p);
    }

    private void handleSynPacket(Packet packet) throws IOException {
        ServerConnection serverConnection = new ServerConnection();
        serverConnection.setInetAddress(packet.getPeerAddress());
        serverConnection.setPort(packet.getPeerPort());
        serverConnection.setSequenceNumber(packet.getSequenceNumber());
        serverConnection.setNbOfPacketsExpected(Integer.parseInt(new String(packet.getPayload())));
        serverConnection.setPacketListHandler(new PacketListHandler(packet.getSequenceNumber()+3));
        server.addConnection(serverConnection);

        Packet p = packet.toBuilder()
              .setType(Packet.Type.ACKSYN)
              .setSequenceNumber((packet.getSequenceNumber()+1))
              .create();

        sender.send(p);
    }

}
