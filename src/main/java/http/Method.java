package http;

/**
 * All the supported method from the http module
 */
public enum Method {
    GET("GET"),
    POST("POST"),
    ;

    private final String name;
    Method(String s) {
        name = s;
    }

    @Override public String toString() {
        return name;
    }
}
