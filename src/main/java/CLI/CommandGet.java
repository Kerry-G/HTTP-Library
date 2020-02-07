package CLI;

import com.beust.jcommander.Parameter;
import http.*;
import logger.Logger;
import logger.Verbosity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    @Parameter (names={"--output", "-o"}, description= "output")
    private String output;

    @Override public String toString() {
        return "CommandGet{" + "verbosity=" + verbosity + ", headers=" + headersAsStringList + "} " + super.toString();
    }

    @Override Response run() {
        if(verbosity) Logger.setVerbosity(Verbosity.Debug);
        if(output != null) {
            try {
                Logger.setPrintStream(new PrintStream(new FileOutputStream(output)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Headers headers = Converters.stringListToHeaders(headersAsStringList);

        return new RequestBuilder()
                .setUrl(verifyUrl(parameters))
                .setMethod(Method.GET)
                .setHeaders(headers)
                .createRequest()
                .send();
    }
}
