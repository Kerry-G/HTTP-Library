import http.Method;
import http.Request;
import http.RequestBuilder;
import http.Response;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestTests {

    @Test
    void GetStatus200(){
        URI url = null;
        
        try {
            url = new URI("http://httpbin.org/status/200");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        Request request = new RequestBuilder()
                    .setUrl(url)
                    .setMethod(Method.GET)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .createRequest();

        
        Response response = request.send();

        assertEquals(200,response.getStatus(),"Status should be 200");
    }

    @Test
    void GetStatus202() {
        URI url = null;

        try {
            url = new URI("http://httpbin.org/status/202");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Request request = null;
        request = new RequestBuilder().setUrl(url).setMethod(Method.GET).createRequest();

        Response send = request.send();

        assertEquals(202,send.getStatus(),"Status should be 200");
    }

    @Test
    void PostStatus200()  {
        URI url = null;
        try {
            url = new URI("http://httpbin.org/status/200");
        } catch (URISyntaxException e) {
            assertNull(e);
        }

        Request request = new RequestBuilder()
                .setUrl(url)
                .setMethod(Method.POST)
                .addHeader("accept", "application/json")
                .setBody("")
                .createRequest();
        Response response = request.send();

        assertEquals(200, response.getStatus());


    }
}
