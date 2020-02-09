import CLI.Httpc;
import com.beust.jcommander.JCommander;
import http.Constants;
import logger.Logger;

import java.util.Map;

/**
 * Starting class of the HTTP Library project
 *
 * @author Kerry Gougeon Ducharme (40028722) and Jonathan Mongeau (40006501)
 */
class Driver {
    public static void main(String[] args) {
        Httpc httpc = new Httpc();
        JCommander jc = httpc.getJc();
        if (args.length == 0) {
            jc.usage();
            return;
        }

        httpc.interpret().ifPresent(response -> {
            Logger.debug(
                    response.getVersion() + Constants.SPACE + response.getStatus() + Constants.SPACE + response.getPhrase());
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                Logger.debug(header.getKey() + ": " + header.getValue());
            }
            Logger.println(response.getBody());
        });


    }
}
