package http;

import java.net.URL;
import java.net.UnknownHostException;

public class RequestBuilder {
    private URL url;
    private Method method;

    public RequestBuilder setUrl(URL url) {
        this.url = url;
        return this;
    }

    public RequestBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Request createRequest() throws UnknownHostException {
        return new Request(url, method);
    }
}
