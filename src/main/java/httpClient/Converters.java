package httpClient;

import http.Headers;

import java.util.List;

public final class Converters {

    static Headers stringListToHeaders(List<String> s){
        Headers headers = new Headers();

        for (String line : s){
            String[] header = line.split(":");
            headers.put(header[0],header[1]);
        }

        return headers;
    }

}
