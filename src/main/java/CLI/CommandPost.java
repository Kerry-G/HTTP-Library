package CLI;

import com.beust.jcommander.Parameter;
import http.Headers;
import http.Method;
import http.RequestBuilder;
import http.Response;
import logger.Logger;
import logger.Verbosity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommandPost extends Command {

    /**
     * When -verbosity is set to true, it will change the Logger's verbosity value.
     */
    @Parameter(names={"--verbosity", "-v"}, description= "Debug mode")
    private boolean verbosity = false;

    /**
     * Catches all the headers passed in the arguments.
     */
    @Parameter (names={"--headers", "-h"}, description= "Headers")
    private List<String> headersAsStringList = new ArrayList<>();

    @Parameter (names={"--data", "-d"}, description= "inline-data")
    private String data;

    @Parameter (names={"--file", "-f"}, description= "file")
    private String file;


    @Override Response run() {
        if(verbosity) Logger.setVerbosity(Verbosity.Debug);
        Headers headers = Converters.stringListToHeaders(headersAsStringList);

        URL url = null;
        if (!parameters.startsWith("http://")) {
            parameters = "http://" + parameters;
        }
        try {
            url = new URL(parameters);
        } catch (MalformedURLException e) {
            Logger.error("Given URL is not well formatted.");
        }

        String body = data;

        return new RequestBuilder()
                .setUrl(url)
                .setMethod(Method.POST)
                .setHeaders(headers)
                .setBody(body)
                .createRequest()
                .send();
    }
}
