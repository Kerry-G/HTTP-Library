package CLI;

import com.beust.jcommander.Parameter;
import http.Response;

/**
 * Base class for Command classes. Define default value that every commands will need
 */
public abstract class Command {

    /**
     * Catch all the parameters not defined by a name in the argument values.
     * In our case, it should only catch the URL
     */
    @Parameter
    protected String parameters;

    @Override public String toString() {
        return "Command{" + "parameters=" + parameters + '}';
    }

    abstract Response run();

}
