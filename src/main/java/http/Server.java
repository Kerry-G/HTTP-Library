package http;


import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.IOException;
import java.math.BigInteger;
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
    Integer numberOfPacketsExpected = 0;
    long sequence = 0;
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

            PacketHandler ph = new PacketHandler();

            for (;;) {
                buf.clear();
                SocketAddress router = channel.receive(buf);

                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();
                int type = packet.getType();
                UdpConnection udpConnection = new UdpConnection(packet.getPeerAddress(), packet.getPeerPort(), channel);
                /**
                 * SYN -> server
                 * SYN-ACK -> client
                 * ACK -> server
                 * DATA -> server
                 * DATA -> server
                 * ....
                 * FIN -> server
                 * data -> client
                 * data -> client
                 * FIN -> client
                 */
                switch (type){
                    case 0:
                        Logger.debug("Data Packet Received");
                        sequence = ph.add(packet);
                        udpConnection.sendPacket(3, sequence, "NO_PAYLOAD");
                        break;
                    case 1:
                        Logger.debug("SYN received");
                        numberOfPacketsExpected = Integer.parseInt(new String(packet.getPayload()));
                        udpConnection.receiveHandShake(packet.getSequenceNumber());
                        break;
                    case 2:
                        Logger.debug("SYN-ACK received (should not happen)");
                        break;
                    case 3:
                        Logger.debug("ACK received");
                        break;
                }
                if (numberOfPacketsExpected.equals(ph.size())){
                    String payload = ph.getPayload();

                    final Request request = Request.fromBufferedReader(payload);

                    Logger.debug(" === Request object === ");
                    Logger.debug(request.toString());
                    RequestHandler handler = new FileServerHandler(this.directoryPath);
                    Response response = handler.handleRequest(request);
                    Logger.debug(" === Response Object === ");
                    Logger.debug(response.toString());
                    String serialized = response.getSerialized();
                    Logger.debug(" === Serialized response === ");
                    Logger.debug(serialized);
                    System.out.println(serialized);


                    //Up to here is fine
                    udpConnection.ARQ(serialized, ++sequence);

                }


            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
