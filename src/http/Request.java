package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Request {

    private Method method;
    private Integer version;

    private InetAddress address;
    private String url;
    private String path;


    private ArrayList<Header> headerList;
    private String body;

    public Request(URL url, Method method) throws UnknownHostException {
        this.path = url.getPath();
        this.address = InetAddress.getByName(url.getHost());
        this.method = method;
        this.body = "";
        this.version = 1;
        this.headerList = new ArrayList<Header>();
        this.headerList.add(new Header("User-Agent", "Hello"));
    }

    private StringBuilder getRequestLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(method.toString()).append(" ").append(this.path).append(" HTTP/1.0").append("\\r\\n");
        return sb;
    }

    private StringBuilder getSerialized(){
        StringBuilder sb = getRequestLine();
        for (Header header: headerList) {
            sb.append(header.serialize());
        }
        sb.append("\\r\\n").append(this.body);
        return sb;
    }



    public Response send() throws IOException {
        Socket socket = new Socket(this.address, 80);

        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

        String serialized = this.getSerialized().toString();
        out.write(serialized);
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
