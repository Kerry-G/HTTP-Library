package http;

import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

public class Request {

    private Method method;
    private String version;

    private InetAddress address;
    private String url;
    private String path;


    private Headers headers;
    private String body;

    public Request(URL url, Method method) throws UnknownHostException {
        this.path = url.getPath() + "?" + url.getQuery();
        this.address = InetAddress.getByName(url.getHost());
        this.method = method;
        this.body = "";
        this.version = "HTTP/1.0";
        this.headers = new Headers();
    }

    private StringBuilder getRequestLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(method)
          .append(Constants.SPACE)
          .append(this.path)
          .append(Constants.SPACE)
          .append("HTTP/1.0")
          .append(Constants.CARRIAGE)
          .append(Constants.NEW_LINE);

        return sb;
    }

    private String getSerialized(){
        StringBuilder sb = getRequestLine();
        sb.append(headers)
          .append(Constants.CARRIAGE)
          .append(Constants.NEW_LINE)
          .append(this.body);

        return sb.toString();
    }



    public Response send() throws IOException {
        Socket socket = new Socket(this.address, 80);

        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

        String serialized = this.getSerialized();
        Logger.println(serialized);
        out.write(serialized);
        out.flush();

        boolean firtLine = true;
        boolean iteratorReachedBody = false;
        StringBuilder body = new StringBuilder();
        Iterator<String> iterator = in.lines().iterator();
        while(iterator.hasNext()){
           String line = iterator.next();
           if (firtLine){
               firtLine = false;
               System.out.println(line);
           }
           else if(line.isEmpty()) {
               iteratorReachedBody = true;
            }
           else if(iteratorReachedBody){
               body.append(line).append("\n");
            } else {
               String[] split = line.split(":");
               System.out.println(split[0] + ":" + split[1].trim());
           }

       }

        System.out.println("Body: " +body);

        out.close();
        in.close();
        socket.close();

        return new Response();
    }
}
