package http.udp;

import http.Packet;

public interface PacketHandler {
    public void run(Packet packet);
}
