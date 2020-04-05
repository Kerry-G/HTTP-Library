package http.udp;

import http.Packet;
import logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class Receiver extends Thread {

    DatagramChannel datagramChannel;
    final ByteBuffer readBuffer;
    boolean listening = false;
    private SelectionKey selectionKey;
    private PacketHandler packetHandler;
    private long lastCommunicationTime;

    public Receiver() {
        readBuffer = ByteBuffer.allocate(Packet.MAX_LEN);
    }

    public boolean isListening() {
        return listening;
    }

    public void bind(DatagramChannel datagramChannel) throws IOException {
        bind(datagramChannel, null);
    }

    public void bind(DatagramChannel datagramChannel, InetSocketAddress localPort) throws IOException {
        close();
        readBuffer.clear();
        try {
            this.datagramChannel = datagramChannel;
            this.datagramChannel.bind(localPort);
            this.datagramChannel.configureBlocking(false);
            Selector selector = Selector.open();
            selectionKey = datagramChannel.register(selector, SelectionKey.OP_READ);
            lastCommunicationTime = System.currentTimeMillis();
        } catch (IOException ex) {
            close();
            throw ex;
        }
    }

    public void close () {
        try {
            if (datagramChannel != null) {
                datagramChannel.close();
                datagramChannel = null;
                if (selectionKey != null) selectionKey.selector().wakeup();
            }
        } catch (IOException ex) {
            Logger.debug("Unable to close Receiver");
        }

    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void run() {
        if(packetHandler == null) throw new RuntimeException("PacketHandler must be defined in Receiver.");
        try {
            Logger.println("Listening at " + datagramChannel.getLocalAddress());
            listening = true;
            while(true) {
                readBuffer.clear();
                selectionKey.selector().select();
                if (selectionKey.isReadable()){
                    datagramChannel.receive(readBuffer);
                    readBuffer.flip();
                    Packet packet = Packet.fromBuffer(readBuffer);
                    readBuffer.flip();
                    packetHandler.run(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

