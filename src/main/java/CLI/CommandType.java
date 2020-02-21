package CLI;

public enum CommandType {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    HEAD("HEAD"),
    DELETE("DELETE"),
    HELP("HELP"),
    ;

    final String name;

    CommandType(String n) {
        name = n;
    }

    @Override public String toString() {
        return name;
    }

    public static CommandType fromString(String text) {
        for (CommandType b : CommandType.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with name" + text + " found.");
    }
}
