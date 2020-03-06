package httpFileServer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import http.Server;
import logger.Logger;
import logger.Verbosity;

import java.util.StringJoiner;

public class Arguments {

    @Parameter(names = { "--log", "--verbose", "-v" }, description = "Level of verbosity")
    private boolean verbose = false;

    @Parameter(names = {"--path", "-d"}, description = "Path to directory")
    private String path = "./";

    @Parameter(names = {"--port", "-p"}, description = "Port")
    private int port = 8080;

    @Parameter(names = {"--help", "-h"}, description = "help")
    private boolean help = false;

    @Override public String toString() {
        return new StringJoiner(", ", Arguments.class.getSimpleName() + "[", "]").add("verbose=" + verbose).add(
                "path='" + path + "'").add("port=" + port).toString();
    }

    void run(JCommander jc){
        if (help) jc.usage();
        if (verbose) Logger.setVerbosity(Verbosity.Debug);
        Logger.println("Starting server on port " + port + ", with file path: " + path);
        Server server = new Server(port, path);
        server.initialize();
    }
}
