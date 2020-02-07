package http;

import java.io.BufferedReader;
import java.util.Iterator;

public class Response {

    private String version;
    private String phrase;
    private Integer status;
    private Headers headers;
    private String body;

    public Response(String version, String phrase, Integer status, Headers headers, String body) {
        this.version = version;
        this.phrase = phrase;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public String getPhrase() {
        return phrase;
    }

    public Integer getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    /**
     * Takes a buffered reader that came from a Socket object and serialize it's information into a response object
     * @param in
     * @return Response object containing the information
     */
    static Response fromBufferedReader(BufferedReader in){
        ResponseBuilder rb = new ResponseBuilder();
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
                final String status = line.replace(split[0], "").replace(split[1], "").trim();
                rb.setVersion(split[0])
                  .setStatus(Integer.valueOf(split[1]))
                  .setPhrase(status);
            }
            else if(line.isEmpty()) {
                iteratorReachedBody = true;
            }
            else if(iteratorReachedBody){
                body.append(line).append(Constants.NEW_LINE); // Adding a /n so it's matches what we originally receive
            } else {
                String[] split = line.split(":");
                headers.put(split[0], line.replace(split[0], "").replace(": ", ""));
            }

        }
        return rb.setHeaders(headers)
                 .setBody(body.toString())
                 .createResponse();
    }

}
