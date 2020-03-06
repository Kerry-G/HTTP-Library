package httpFileServer;

import com.beust.jcommander.JCommander;
import http.Response;
import http.Server;
import httpClient.CommandGet;

import java.util.Optional;

public class Httpfs {

    private JCommander jc;
    Arguments arguments;

    public Httpfs(){
        arguments = new Arguments();
        jc = JCommander.newBuilder()
                       .addObject(arguments)
                       .build();

        jc.setProgramName("Httpfs");
    }

    public Optional<Response> interpret(String ...args){
        try {
            jc.parse(args);
            arguments.run(jc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public JCommander getJc(){
        return jc;
    }


}
