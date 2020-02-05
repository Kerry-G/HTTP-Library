import java.net.InetAddress;
import java.net.URL;

import http.Method;
import http.Request;

class Driver {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://httpbin.org/get?course=networking&assignment=1");
            Request request = new Request(url, Method.GET);
            request.send();
        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
