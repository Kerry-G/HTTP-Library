package http;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class RequestBuilder {

    public RequestBuilder(){
        
    }

    public Request build(){
        return new Request();
    }
}