package CLI;

import com.beust.jcommander.JCommander;
import http.Constants;
import http.Response;
import logger.Logger;

import java.util.Map;
import java.util.Optional;

public class Httpc {

    private JCommander jc;

    CommandGet commandGet;
    CommandPost commandPost;
    CommandHelp commandHelp;

    public Httpc(){
        commandGet = new CommandGet();
        commandPost = new CommandPost();
        commandHelp = new CommandHelp();
        jc = JCommander.newBuilder()
              .addCommand(CommandType.GET.toString(), commandGet)
              .addCommand(CommandType.POST.toString(), commandPost)
              .addCommand(CommandType.HELP.toString(), commandHelp)
              .build();
    }

    public Optional<Response> interpret(){
        switch (CommandType.fromString(jc.getParsedCommand())) {
            case GET:
                return Optional.of(commandGet.run());
            case POST:
                return Optional.of(commandPost.run());
            case HELP:
                commandHelp.run();
                return Optional.empty();
        }
        return Optional.empty();
    }

    public JCommander getJc(){
        return jc;
    }


}