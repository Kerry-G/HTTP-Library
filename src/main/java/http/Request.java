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

    public Request(URL url, Method method, Headers headers, String body) throws UnknownHostException {
        this.path = url.getPath() + "?" + url.getQuery();
        this.address = InetAddress.getByName(url.getHost());
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

    public Response send() throws IOException {
        Socket socket = new Socket(this.address, 80);

        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

        String serialized = this.getSerialized();
        out.write(serialized);
        out.flush();

        Response response = Response.fromBufferedReader(in);

        out.close();
        in.close();
        socket.close();

        return response;
    }
}
