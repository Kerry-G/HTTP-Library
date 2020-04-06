package http.udp;

import http.PacketListHandler;

import java.net.InetAddress;

public class ServerConnection {

    private int nbOfPacketsExpected;
    private int nbOfResponsePacketsExpected;
    private int port;
    private long SequenceNumber;
    private PacketListHandler packetListHandler;
    private InetAddress inetAddress;
    private boolean finish;
    public int getNbOfPacketsExpected() {
        return nbOfPacketsExpected;
    }

    public void setNbOfPacketsExpected(int nbOfPacketsExpected) {
        this.nbOfPacketsExpected = nbOfPacketsExpected;
    }

    public long getSequenceNumber() {
        return SequenceNumber;
    }

    public PacketListHandler getPacketListHandler() {
        return packetListHandler;
    }

    public void setSequenceNumber(long sequenceNumber) {
        SequenceNumber = sequenceNumber;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getNbOfResponsePacketsExpected() {
        return nbOfResponsePacketsExpected;
    }

    public void setNbOfResponsePacketsExpected(int nbOfResponsePacketsExpected) {
        this.nbOfResponsePacketsExpected = nbOfResponsePacketsExpected;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPacketListHandler(PacketListHandler packetListHandler) {
        this.packetListHandler = packetListHandler;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFinish() {
        return finish;
    }
}
