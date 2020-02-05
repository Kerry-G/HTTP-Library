package http;

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
