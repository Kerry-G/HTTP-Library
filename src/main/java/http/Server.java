package http;


import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {

    Integer port = 8080;
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

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            Logger.println("Server is listening at " + channel.getLocalAddress());
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);

            for (;;) {
                buf.clear();
                SocketAddress router = channel.receive(buf);

                // Parse a packet from the received raw data.
                buf.flip();
                if(buf.limit() == 0) continue;
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();
                System.out.println("test!");
                if(packet.getType() == 1){
                    Logger.println("SYN received");
                    UdpConnection udpConnection = new UdpConnection(packet.getPeerAddress(), packet.getPeerPort(), channel);
                    udpConnection.receiveHandShake(packet.getSequenceNumber());
                }
                if(packet.getType() == 3){
                    Logger.println("ACK received");
                }

//                String payload = new String(packet.getPayload(), UTF_8);
//                Logger.println("Packet: " + packet);
//                Logger.println("Payload: " + payload);
//                Logger.println("Router: " + router);
//
//                final Request request = Request.fromBufferedReader(payload);
//
//                Logger.debug(" === Request object === ");
//                Logger.debug(request.toString());
//                RequestHandler handler = new FileServerHandler(this.directoryPath);
//                Response response = handler.handleRequest(request);
//                Logger.debug(" === Response Object === ");
//                Logger.debug(response.toString());
//                String serialized = response.getSerialized();
//                Logger.debug(" === Serialized response === ");
//                Logger.debug(serialized);
//
//                Packet resp = packet.toBuilder().setPayload(serialized.getBytes()).create();
//                channel.send(resp.toBuffer(), router);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
