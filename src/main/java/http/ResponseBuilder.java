package http;

/**
 * Builder class that builds Response object
 */
public class ResponseBuilder {
    private String version;
    private String phrase;
    private Integer status;
    private Headers headers;
    private String body;

    public ResponseBuilder(){
        this.headers = new Headers();
        this.version = "HTTP/1.0";
        this.phrase = "";
        this.status = null;
        this.body = "";
    }
    public ResponseBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public ResponseBuilder setPhrase(String phrase) {
        this.phrase = phrase;
        return this;
    }

    public ResponseBuilder setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public ResponseBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public Response createResponse() {
        return new Response(version, phrase, status, headers, body);
    }
}
