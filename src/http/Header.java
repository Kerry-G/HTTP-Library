package http;

public class Header {



    private String name;
    private String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Header() {
        this.name = "";
        this.value = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String serialize() {
        return new StringBuilder().append(name).append(": ").append(value).append("\r\n").toString();
    }




}
