
import CLI.CommandType;
import CLI.Httpc;
import com.beust.jcommander.JCommander;
import http.Constants;
import logger.Logger;

import java.util.Map;

class Driver {



    public static void main(String[] args) {
        try {

            Httpc httpc = new Httpc();
            JCommander jc = httpc.getJc();

            jc.parse(args);
            httpc.interpret().ifPresent(response -> {
                Logger.debug(response.getVersion() + Constants.SPACE + response.getStatus() + Constants.SPACE + response.getPhrase());
                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                    Logger.debug(header.getKey() + ": " + header.getValue());
                }
                Logger.println(response.getBody());
            });


        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
