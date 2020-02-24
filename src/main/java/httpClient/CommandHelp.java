package httpClient;

import http.Response;
import logger.Logger;

public class CommandHelp extends Command{

    /**
     * As this is a print-only class, it return no responses.
     * @return null
     */
    @Override Response run() {
        if (parameters == null || parameters.isEmpty()){
            Logger.println("httpc is a curl-like application but supports HTTP protocol only.\n" + "Usage:\n"
                                   + " httpc command [arguments]\n" + "The commands are:\n"
                                   + " get executes a HTTP GET request and prints the response.\n"
                                   + " post executes a HTTP POST request and prints the response.\n"
                                   + " help prints this screen.\n"
                                   + "Use \"httpc help [command]\" for more information about a command.\n");
            return null;
        }
        switch (CommandType.fromString(parameters)) {
            case GET:
                Logger.println("usage: httpc get [-v] [-h key:value] URL\n"
                                       + "Get executes a HTTP GET request for a given URL.\n"
                                       + "-v Prints the detail of the response such as protocol, status,\n"
                                       + "and headers.\n"
                                       + "-h key:value Associates headers to HTTP Request with the format\n"
                                       + "'key:value'.\n");
                break;
            case POST:
                Logger.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                                       + "Post executes a HTTP POST request for a given URL with inline data or from\n"
                                       + "file.\n" + "-v Prints the detail of the response such as protocol, status,\n"
                                       + "and headers.\n"
                                       + "-h key:value Associates headers to HTTP Request with the format\n"
                                       + "'key:value'.\n"
                                       + "-d string Associates an inline data to the body HTTP POST request.\n"
                                       + "-f file Associates the content of a file to the body HTTP POST\n" + "request.\n"
                                       + "Either [-d] or [-f] can be used but not both.");
                break;
            default:
                Logger.println("There is no " + parameters + " commands.");
        }
        return null;
    }
}
