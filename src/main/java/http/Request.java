package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class Request {

    private Method method;
    private String version = "HTTP/1.0";

    private InetAddress address;
    private String path;
    private Headers headers;
    private String body;

    public static final Integer MAXTRIES = 5;

    public Request(URL url, Method method, Headers headers, String body){
        this.setURL(url);
        this.method = method;
        this.body = body;
        this.headers = headers;
    }

    public Method getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getPath() {
        return path;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void setURL(URL url){
        this.path = url.getPath();
        if(url.getQuery() != null && !url.getQuery().isEmpty()){
            this.path  += "?" + url.getQuery();
        }
        try {
            this.address = InetAddress.getByName(url.getHost());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("No such URL is known");
        }
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

    private void redirectTo(URL url){
        this.setURL(url);

    }

    private Response sendIsolatedRequest() {
        Socket socket = null;
        Response response = null;
        try {
            socket = new Socket(this.address, 80);

            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

            if(this.method.equals(Method.POST) && this.body != null && !this.body.isEmpty()){
                this.headers.put("Content-Length", String.valueOf(this.body.getBytes().length));
            }

            String serialized = this.getSerialized();
            out.write(serialized);
            out.flush();

            response = Response.fromBufferedReader(in);

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            throw new IllegalArgumentException("Can't establish an connection.");
        }
        return response;
    }

    public Response send(){

        Response response = this.sendIsolatedRequest();
        String location;
        Headers headers;
        URL url;

        Integer noTries = 0;
        while (response.getStatus() % 300 <= 99 && noTries < this.MAXTRIES){
            headers = response.getHeaders();
            location = headers.get("Location");
            try {
                url = new URL(location);
            } catch (MalformedURLException e) {
                break;
            }

            this.setURL(url);

            response = this.sendIsolatedRequest();
            noTries++;
        }
        return response;
    }

}
