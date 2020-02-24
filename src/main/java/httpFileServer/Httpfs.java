package httpFileServer;

import com.beust.jcommander.JCommander;
import http.Response;
import httpClient.CommandGet;
import httpClient.CommandType;
import logger.Logger;

import java.util.Optional;

public class Httpfs {

    private JCommander jc;

    CommandGet commandGet;

    public Httpfs(){
        commandGet = new CommandGet();
        jc = JCommander.newBuilder()
                       .addCommand(CommandType.GET.toString(), commandGet)
                       .build();
        jc.setProgramName("Httpfs");
    }

    public Optional<Response> interpret(String ...args){
        return Optional.empty();
    }

    public JCommander getJc(){
        return jc;
    }


}
