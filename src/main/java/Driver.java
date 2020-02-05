import java.net.URL;

import http.Method;
import http.Request;
import http.RequestBuilder;
import http.Response;
import logger.Logger;

class Driver {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://httpbin.org/get?course=networking&assignment=1");
            Request request = new RequestBuilder().setUrl(url).setMethod(Method.GET).createRequest();
            final Response send = request.send();
            Logger.println(send.getBody());

        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
