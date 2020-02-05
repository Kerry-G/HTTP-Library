package http;

public class ResponseBuilder {
    private String version;
    private String phrase;
    private Integer status;
    private Headers headers;
    private String body;

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
