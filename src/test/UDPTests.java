import http.*;
import logger.Logger;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UDPTests {

    @Test
    void SendUdpRequest(){
        URL url = null;

        try {
            url = new URL("http://localhost/");
        } catch ( MalformedURLException e) {
            e.printStackTrace();
        }

        Request request = new RequestBuilder()
                .setUrl(url)
                .setMethod(Method.GET)
                .addHeader("User-Agent", "Mozilla/5.0")
                .createRequest();


        Response response = request.send(8080);

        assertEquals(200,response.getStatus(),"Status should be 200");
    }

    @Test
    void Kerry(){
        UdpConnection udpConnection = null;
        try {
            udpConnection = new UdpConnection(InetAddress.getByName("127.0.0.1"), 8080);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        udpConnection.send();

    }
}
