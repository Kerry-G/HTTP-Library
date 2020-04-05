package http;


import http.udp.Receiver;
import http.udp.Sender;
import http.udp.ServerPacketHandler;
import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class Server {

    Integer port = 8080;
    Sender sender;
    Receiver receiver;
    String directoryPath = "./";
    Boolean serverOn = true;
    public Server(int port){
        this.port = port;
    }

    public Server(String directoryPath){
        this.directoryPath = directoryPath;
    }

    public Server(int port, String directoryPath){
        this.port = port;
        this.directoryPath = directoryPath;
    }

    public void close(){
        serverOn = false;
    }

    public void initialize() {

        try {
            Selector selector = Selector.open();
            DatagramChannel datagramChannel = selector.provider().openDatagramChannel();
            receiver = new Receiver();
            sender = new Sender(datagramChannel);
            ServerPacketHandler serverPacketHandler = new ServerPacketHandler(sender);
            receiver.bind(datagramChannel, new InetSocketAddress(port));
            receiver.setPacketHandler(serverPacketHandler);
            receiver.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
