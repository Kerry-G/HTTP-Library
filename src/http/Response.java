package http;

import java.io.BufferedReader;
import java.util.Iterator;

public class Response {

    private String version;
    private String phrase;
    private String status;
    private Headers headers;
    private String body;

    public String getVersion() {
        return version;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    static Response fromBufferedReader(BufferedReader in){
        Response response = new Response();
        Headers headers = new Headers();
        boolean firstLine = true;
        boolean iteratorReachedBody = false;

        StringBuilder body = new StringBuilder();

        Iterator<String> iterator = in.lines().iterator();
        while(iterator.hasNext()){
            String line = iterator.next();
            if (firstLine){
                firstLine = false;
                final String[] split = line.split(" ");
                response.version = split[0];
                response.status = split[1];
                response.phrase = split[2];
            }
            else if(line.isEmpty()) {
                iteratorReachedBody = true;
            }
            else if(iteratorReachedBody){
                body.append(line).append(Constants.NEW_LINE); // Adding a /n so it's matches what we originally receive
            } else {
                String[] split = line.split(":");
                headers.put(split[0], split[1].trim());
            }

        }
        response.headers = headers;
        response.body = body.toString();
        return response;
    }

}
