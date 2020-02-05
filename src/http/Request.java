package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

public class Request {

    private Method method;
    private InetAddress url;
    private Integer version;

    private ArrayList<Header> headerList;
    private String body;

    public Request(InetAddress url, Method method) {
        this.url = url;
        this.method = method;
        this.body = "";
        this.version = 1;
        this.headerList = new ArrayList<Header>();
    }



    public Response send() throws IOException {

        Socket socket = new Socket(this.url, 80);


        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

        out.write("GET / HTTP/1.0\r\nUser-Agent: Hello\r\n\r\n");
        out.flush();

        in.lines().forEach((line) -> {
            System.out.println(line);
        });


        out.close();
        in.close();
        socket.close();

        return new Response();
    }
}
