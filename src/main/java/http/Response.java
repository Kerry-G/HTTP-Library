package http;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.StringJoiner;

/**
 * HTTP Response class. Public interface allows you get information
 * about an HTTP request's response.
 */
public class Response implements HttpSerialize {

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

    public String getSerialized(){
        StringBuilder sb = new StringBuilder();
        sb
                .append(getVersion())
                .append(Constants.SPACE)
                .append(getStatus())
                .append(Constants.SPACE)
                .append(getPhrase())
                .append(Constants.CARRIAGE)
                .append(Constants.NEW_LINE)
                .append(headers)
                .append(Constants.CARRIAGE)
                .append(Constants.NEW_LINE)
                .append(getBody());

        return sb.toString();
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

    @Override public String toString() {
        return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]").add("version='" + version + "'")
                                                                                .add("phrase='" + phrase + "'")
                                                                                .add("status=" + status)
                                                                                .add("headers=" + headers)
                                                                                .add("body='" + body + "'")
                                                                                .toString();
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
                headers.put(split[0].toLowerCase(), line.replace(split[0], "").replace(": ", ""));
            }

        }
        return rb.setHeaders(headers)
                 .setBody(body.toString())
                 .createResponse();
    }

}
