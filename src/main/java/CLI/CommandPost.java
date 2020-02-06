package CLI;

import com.beust.jcommander.Parameter;
import http.Response;

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
    private List<String> headers = new ArrayList<>();

    @Override Response run() {
        return null;
    }
}
