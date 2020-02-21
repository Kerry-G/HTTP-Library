package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;


/**
 * Request object which can represent any
 * http request. Specified by it's method, url, headers and body.
 */
public class Request {

    private Method method;
    private String version = "HTTP/1.0";

    private InetAddress address;
    private String path;
    private Headers headers;
    private String body;

    public static final Integer MAXTRIES = 5; // Max tries for redirect as specified in HTTP 1.0 Redirection specification

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

    /**
     * Private setter for setting the URI of the location the lib
     * is trying to access
     * @param url
     */
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

    /**
     * Private getter to generate the first line in the http request.
     * @return requestLine
     */
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

    /**
     * Private getter to generate the the serialized request
     * @return serializedRequest
     */
    private String getSerialized(){
        StringBuilder sb = getRequestLine();
        sb.append(headers)
          .append(Constants.CARRIAGE)
          .append(Constants.NEW_LINE)
          .append(this.body);

        return sb.toString();
    }

    /**
     * This method sends a single request and returns a single response (ie. this method does not support redirection)
     * @return A single response object
     */
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

    /**
     * This public send method
     * It supports redirection for Responses who's status code
     * is in the 300 range.
     * @return Resulting response object after MAXTRIES redirections
     */
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
