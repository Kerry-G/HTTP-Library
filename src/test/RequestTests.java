import static org.junit.jupiter.api.Assertions.assertEquals;

import http.Method;
import http.Request;
import http.RequestBuilder;
import http.Response;
import logger.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class RequestTests {

    @Test
    void GettingStatus200(){
        URL url = null;
        
        try {
            url = new URL("http://httpbin.org/status/200");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        Request request = null;
        try {
            request = new RequestBuilder()
                    .setUrl(url)
                    .setMethod(Method.GET)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .createRequest();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        Response response = null;
        try {
            response = request.send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200,response.getStatus(),"Status should be 200");
    }

    @Test
    void GettingStatus202(){
        URL url = null;

        try {
            url = new URL("http://httpbin.org/status/202");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Request request = null;
        try {
            request = new RequestBuilder().setUrl(url).setMethod(Method.GET).createRequest();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Response send = null;
        try {
            send = request.send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(202,send.getStatus(),"Status should be 200");
    }

}
