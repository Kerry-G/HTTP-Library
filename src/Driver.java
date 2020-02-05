import java.net.InetAddress;
import http.Method;
import http.Request;

class Driver {
    public static void main(String[] args) {
        try {
            Request request = new Request(InetAddress.getByName("www.google.ca"), Method.GET);
            request.send();
        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
