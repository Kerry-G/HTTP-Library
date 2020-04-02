import http.Method;
import http.Request;
import http.RequestBuilder;
import http.Response;
import logger.Logger;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

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


        Response response = request.send(8007);

        assertEquals(200,response.getStatus(),"Status should be 200");
    }
}
