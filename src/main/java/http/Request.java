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

public class Request {

    private Method method;
    private String version = "HTTP/1.0";

    private InetAddress address;
    private String path;
    private Headers headers;
    private String body;

    public Request(URL url, Method method, Headers headers, String body){
        this.path = url.getPath() + "?" + url.getQuery();
        try {
            this.address = InetAddress.getByName(url.getHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.method = method;
        this.body = body;
        this.headers = headers;
    }

    private StringBuilder getRequestLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(method)
          .append(Constants.SPACE)
          .append(this.path)
          .append(Constants.SPACE)
          .append(this.version)
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

    public Response send() {
        Socket socket = null;
        Response response = null;
        try {
            socket = new Socket(this.address, 80);

            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

            String serialized = this.getSerialized();
            out.write(serialized);
            out.flush();

            response = Response.fromBufferedReader(in);

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
