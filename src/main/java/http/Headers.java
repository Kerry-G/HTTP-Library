package http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Headers implements Map<String, String> {

    private Map<String, String> headers;

    public Headers() {
        headers = new HashMap<>();
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override public int size() {
        return headers.size();
    }

    @Override public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override public boolean containsKey(Object o) {
        return headers.containsKey(o);
    }

    @Override public boolean containsValue(Object o) {
        return headers.containsValue(o);
    }

    @Override public String get(Object o) {
        return headers.get(o);
    }

    @Override public String put(String s, String s2) {
        return headers.put(s,s2);
    }

    @Override public String remove(Object o) {
        return headers.remove(o);
    }

    @Override public void putAll(Map<? extends String, ? extends String> map) {
        headers.putAll(map);
    }

    @Override public void clear() {
        headers.clear();
    }

    @Override public Set<String> keySet() {
        return headers.keySet();
    }

    @Override public Collection<String> values() {
        return headers.values();
    }

    @Override public Set<Entry<String, String>> entrySet() {
        return headers.entrySet();
    }


    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) -> sb.append(key)
                                          .append(":")
                                          .append(Constants.SPACE)
                                          .append(value)
                                          .append(Constants.CARRIAGE)
                                          .append(Constants.NEW_LINE)
                       );
        return sb.toString();
    }
}
