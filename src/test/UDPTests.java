import http.*;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UDPTests {

    @Test
    void SendUdpRequest(){
        URL url = null;

        try {
            url = new URL("http://localhost/test");
        } catch ( MalformedURLException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<2500; i++){
            sb.append('i');
        }

        Request request = new RequestBuilder()
                .setUrl(url)
                .setMethod(Method.POST)
                .setBody(sb.toString())
                .createRequest();


        Response response = request.send(8080);
        System.out.println(response.getBody());
        assertEquals(200,response.getStatus(),"Status should be 200");
    }

    @Test
    void Kerry(){
//        UdpConnection udpConnection = null;
//        try {
//            udpConnection = new UdpConnection(InetAddress.getByName("127.0.0.1"), 8080);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        URL url = null;
//
//        try {
//            url = new URL("http://localhost/");
//        } catch ( MalformedURLException e) {
//            e.printStackTrace();
//        }
//        Request request = new RequestBuilder()
//                .setUrl(url)
//                .setMethod(Method.GET)
//                .addHeader("User-Agent", "Mozilla/5.0")
//                .createRequest();
//
//        String p = udpConnection.send(request.getSerialized());
//        System.out.println(p);
    }

    @Test
    void PacketHandlerTest(){
        try {
            URL url = new URL("http://localhost/");
            InetAddress address = InetAddress.getByName(url.getHost());
            List<Packet> l = PacketHandler.createPacketList("0123456789ABCDEFGHIJKLMNOP", address, 3000, 1);
            assertEquals(l.size() > 0, true);

            StringBuilder sb = new StringBuilder();
            for (int i=0; i<2500; i++){
                sb.append('0');
            }

            int sequenceNumber = 4;
            List<Packet> l2 = PacketHandler.createPacketList(sb.toString(), address, 3000, sequenceNumber);
            assertEquals(3, l2.size());

            long expectedLatestSequenceNumber = sequenceNumber+l2.size(); // what it should be
            long actualLatestSequenceNumber = l2.get(l2.size()-1).getSequenceNumber(); // get last element sequence no
            assertEquals(expectedLatestSequenceNumber,actualLatestSequenceNumber);
        } catch ( Exception e) {
            e.printStackTrace();
        }

    }

}


