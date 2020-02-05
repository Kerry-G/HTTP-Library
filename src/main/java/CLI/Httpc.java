package CLI;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Httpc {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter (names={"--verbosity", "-v"}, description= "Debug mode")
    private boolean verbosity = false;

    @Override public String toString() {
        return "Httpc{" + "parameters=" + parameters + ", verbosity=" + verbosity + '}';
    }
}
