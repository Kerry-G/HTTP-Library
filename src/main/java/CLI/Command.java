package CLI;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Command classes. Define default value that every commands will need
 */
public abstract class Command {

    /**
     * Catch all the parameters not defined by a name in the argument values.
     * In our case, it should only catch the URL
     */
    @Parameter
    private List<String> parameters = new ArrayList<>();

    /**
     * When -verbosity is set to true, it will change the Logger's verbosity value.
     */
    @Parameter (names={"--verbosity", "-v"}, description= "Debug mode")
    private boolean verbosity = false;

    /**
     * Catches all the headers passed in the arguments.
     */
    @Parameter (names={"--headers", "-h"}, description= "Headers")
    private List<String> headers = new ArrayList<>();

    @Override public String toString() {
        return "Command{" + "parameters=" + parameters + ", verbosity=" + verbosity + ", headers=" + headers + '}';
    }
}
