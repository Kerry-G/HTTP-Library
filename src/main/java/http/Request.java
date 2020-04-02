package http;

import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.channels.DatagramChannel;

import static java.nio.channels.SelectionKey.OP_READ;


/**
 * Request object which can represent any
 * http request. Specified by it's method, url, headers and body.
 */
public class Request implements HttpSerialize {

    private Method method;
    private String version = "HTTP/1.0";

    private InetAddress address;
    private SocketAddress router;
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

    public Request(URL url, Method method, Headers headers, String body, String version) {
        this.setURL(url);
        this.method = method;
        this.body = body;
        this.headers = headers;
        this.version = version;
        this.router = new InetSocketAddress("localhost", 3000);
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
    public String getSerialized(){
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
    private Response sendIsolatedRequest(int port) {
        DatagramChannel channel = null;
        Response response = null;
        try {
            Logger.debug("Creating socket at port " + port + " with address: " + this.address.getHostAddress());

            if(this.method.equals(Method.POST) && this.body != null && !this.body.isEmpty()){
                this.headers.put("Content-Length", String.valueOf(this.body.getBytes().length));
            }

            channel = DatagramChannel.open();
            Packet packet = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(port)
                    .setPeerAddress(address)
                    .setPayload(getSerialized().getBytes())
                    .create();
            Logger.debug("Sending " + getSerialized() + " to router at " + router );
            channel.send(packet.toBuffer(), router);

            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            Logger.debug("Waiting for the response");
            selector.select(5000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if(keys.isEmpty()){
                Logger.println("No response after timeout");
                // return an empty response
            }

            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            buf.flip();
            Packet resp = Packet.fromBuffer(buf);
            Logger.println("Packet: " + resp);
            Logger.println("Router: " + router);
            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
            Logger.println("Payload: " +  payload);

            keys.clear();

            response = Response.fromBufferedReader(payload);


        } catch (IOException e) {
            Logger.debug(e.getMessage());
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
    public Response send(int port){

        Response response = this.sendIsolatedRequest(port);
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
                Logger.debug(e.getMessage());
                break;
            }

            this.setURL(url);

            response = this.sendIsolatedRequest(port);
            noTries++;
        }
        return response;
    }


    public static Request fromBufferedReader(BufferedReader in) throws IOException, RuntimeException {
        RequestBuilder rb = new RequestBuilder();
        Headers headers = new Headers();
        boolean firstLine = true;
        boolean iteratorReachedBody = false;
        boolean done = false;
        Method method = null;
        StringBuilder body = new StringBuilder();
        String line = null;

        Iterator<String> iterator = in.lines().iterator();

        while(!done) {
            try {
                line = iterator.next();
            } catch (Throwable e){
                throw new RuntimeException("Request not well defined.");
            }
            if (firstLine) {
                // this happens at first line
                firstLine = false;
                final String[] split = line.split(" ");
                method = Method.valueOf(split[0]);
                rb.setMethod(method)
                  .setUrl(new URL( "http://www.foo.com" + split[1]))
                  .setVersion(split[2]);
            } else if( line.isEmpty() ) {
                // Empty line therefore done, process body (GET) will skip over this
                done = true;
                if(method == Method.GET) break;
                int contentLength = Integer.parseInt(headers.get("content-length"));
                int r;
                for (int i=0; i<contentLength; i++){
                    r = in.read();
                    body.append((char) r);
                }
            }  else {
                // Process headers
                String[] split = line.split(":");
                headers.put(split[0], line.replace(split[0], "").replace(": ", ""));
            }
        }

        return rb.setHeaders(headers)
                 .setBody(body.toString())
                 .createRequest();
        }


    @Override public String toString() {
        return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]").add("method=" + method)
                                                                               .add("version='" + version + "'")
                                                                               .add("address=" + address)
                                                                               .add("path='" + path + "'")
                                                                               .add("headers=" + headers)
                                                                               .add("body='" + body + "'")
                                                                               .toString();
    }
}
