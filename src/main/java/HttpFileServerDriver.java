import com.beust.jcommander.JCommander;
import http.Server;
import httpFileServer.Httpfs;

/**
 * Starting class of the HTTP Library project
 *
 * @author Kerry Gougeon Ducharme (40028722) and Jonathan Mongeau (40006501)
 */
class HttpFileServerDriver {
    public static void main(String[] args) {
        Httpfs httpfs = new Httpfs();


        httpfs.interpret(args);

    }
}
