package http;

import java.net.URL;

/**
 * Builder class that builds Request Objects
 */
public class RequestBuilder {
    private URL url;
    private Method method;
    private Headers headers;
    private String body;


    public RequestBuilder(){
        this.url = null;
        this.method = null;
        this.headers = new Headers();
        this.body = "";
    }

    public RequestBuilder setUrl(URL url) {
        this.url = url;
        return this;
    }

    public RequestBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder addHeader(String key, String value){
        this.headers.put(key, value);
        return this;
    }

    public RequestBuilder setBody(String body){
        this.body = body;
        return this;
    }

    public Request createRequest()  {
        return new Request(url, method, headers, body);
    }
}
