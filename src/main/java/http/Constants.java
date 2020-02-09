package http;

/**
 * This enum is used to clearly identify certain tokens used
 * in HTTP specification. SPACE, NEW_LINE, CARRIAGE Return.
 */
public enum Constants {
    SPACE(" "),
    NEW_LINE("\n"),
    CARRIAGE("\r"),
    ;

    final String name;

    Constants(String n) {
        name = n;
    }

    @Override public String toString() {
        return name;
    }
}
