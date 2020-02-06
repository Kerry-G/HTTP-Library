package CLI;

import com.beust.jcommander.Parameter;
import http.Headers;
import http.Method;
import http.RequestBuilder;
import http.Response;
import logger.Logger;
import logger.Verbosity;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        String body = null;
        if(file != null && data != null){
            Logger.error("Define file or data, not both.");
        } else if ( data != null ){
            body = data;
        } else if ( file != null ){
            try {
                body = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
            } catch (IOException e) {
                Logger.error("Can't read given file");
            }
        }

        Headers headers = Converters.stringListToHeaders(headersAsStringList);


        return new RequestBuilder()
                .setUrl(verifyUrl(parameters))
                .setMethod(Method.POST)
                .setHeaders(headers)
                .setBody(body)
                .createRequest()
                .send();
    }
}
