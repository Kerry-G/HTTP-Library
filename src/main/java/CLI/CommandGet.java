package CLI;

import com.beust.jcommander.Parameter;
import http.*;
import logger.Logger;
import logger.Verbosity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandGet extends Command{

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

    @Override public String toString() {
        return "CommandGet{" + "verbosity=" + verbosity + ", headers=" + headersAsStringList + "} " + super.toString();
    }

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

        return new RequestBuilder()
                .setUrl(url)
                .setMethod(Method.GET)
                .setHeaders(headers)
                .createRequest()
                .send();
    }
}