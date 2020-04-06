package http;


import http.udp.Receiver;
import http.udp.Sender;
import http.udp.ServerConnection;
import http.udp.ServerPacketHandler;
import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;

public class Server {

    Integer port = 8080;
    Sender sender;
    Receiver receiver;
    String directoryPath = "./";
    Boolean serverOn = true;
    HashMap<InetAddress, ServerConnection> serverConnectionHashMap;
    ArrayDeque<ServerConnection> serverConnectionsDone = new ArrayDeque<ServerConnection>();
    ServerConnection currentlyHandledServerConnection;


    public Server(int port){
        this.port = port;
        serverConnectionHashMap = new HashMap<>();
    }

    public Server(String directoryPath){
        this.directoryPath = directoryPath;
        serverConnectionHashMap = new HashMap<>();
    }

    public Server(int port, String directoryPath){
        this.port = port;
        this.directoryPath = directoryPath;
        serverConnectionHashMap = new HashMap<>();
    }

    public HashMap<InetAddress, ServerConnection> getServerConnectionHashMap() {
        return serverConnectionHashMap;
    }

    public ServerConnection getCurrentlyHandledServerConnection() {
        return currentlyHandledServerConnection;
    }

    public void setCurrentlyHandledServerConnection(ServerConnection currentlyHandledServerConnection) {
        this.currentlyHandledServerConnection = currentlyHandledServerConnection;
    }

    public void addServerConnectionToQueue(ServerConnection sc){
        serverConnectionsDone.add(sc);
    }

    public boolean isConnectionActive(InetAddress key){
        return serverConnectionHashMap.containsKey(key);
    }

    public void addConnection(ServerConnection serverConnection){
        Logger.println("Starting a connection with " + serverConnection.getInetAddress().toString() + ". Expected: " + serverConnection.getNbOfPacketsExpected() + " packets." );
        serverConnectionHashMap.put(serverConnection.getInetAddress(), serverConnection);
    }

    public void removeConnection(ServerConnection serverConnection){
        serverConnectionHashMap.remove(serverConnection.getInetAddress());
    }

    public void removeConnection(InetAddress inetAddress){
        serverConnectionHashMap.remove(inetAddress);
    }

    public void initialize() {

        try {
            Selector selector = Selector.open();
            DatagramChannel datagramChannel = selector.provider().openDatagramChannel();
            receiver = new Receiver();
            sender = new Sender(datagramChannel);
            ServerPacketHandler serverPacketHandler = new ServerPacketHandler(sender, this);
            receiver.bind(datagramChannel, new InetSocketAddress(port));
            receiver.setPacketHandler(serverPacketHandler);
            receiver.start();

            while(true){
                Thread.yield();
                if(serverConnectionsDone.isEmpty()) {
                    continue;
                }
                ServerConnection sc = serverConnectionsDone.pop();
                setCurrentlyHandledServerConnection(sc);
                Request request = Request.fromBufferedReader(sc.getPacketListHandler().getPayload());
                RequestHandler handler = new FileServerHandler(getDirectoyPath());
                Response response = handler.handleRequest(request);
                sendResponse(response, sc);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendResponse(Response response, ServerConnection sc){
        String serialized = response.getSerialized();
        sc.setNbOfResponsePacketsExpected(Math.floorDiv(serialized.length(), (Packet.MAX_LEN - Packet.MIN_LEN))+1);

        long lastSequence = sc.getNbOfResponsePacketsExpected() + sc.getSequenceNumber() + 1; // Last sequence number we receive is SYNACK (n+1) + nbOfPacket we sent + 1
        List<Packet> list = PacketListHandler.createPacketList(serialized, sc.getInetAddress(), sc.getPort() , sc.getSequenceNumber());

        for (Packet p: list){
            try {
                sender.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (sc.getSequenceNumber() < lastSequence){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Logger.debug("Last sequence number received is: " + sc.getSequenceNumber() + ". Should be : " + lastSequence);
            long packetToSend = sc.getSequenceNumber();
            if(sc.getSequenceNumber() < list.get(0).getSequenceNumber()){
                packetToSend = list.get(0).getSequenceNumber();
            }
            Logger.debug("Trying to send packet#: " + packetToSend);
            for (Packet p : list){
                if (p.getSequenceNumber() == packetToSend) {
                    try {
                        sender.send(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }

        Packet FIN = new Packet.Builder()
                .setType(Packet.Type.DATA)
                .setSequenceNumber(lastSequence+1)
                .setPeerAddress(sc.getInetAddress())
                .setPortNumber(sc.getPort())
                .setPayload("FIN")
                .create();

        try {
            sender.send(FIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDirectoyPath() {
        return directoryPath;
    }
}
