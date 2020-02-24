package httpFileServer;

import http.Request;
import http.Response;
import http.ResponseBuilder;
import http.RequestHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileServerHandler implements RequestHandler {
    private String directoryPath;
    public FileServerHandler(String directoryPath){
        this.directoryPath = directoryPath;
    }

    public Response handleRequest (Request request) {
        Response response = null;
        ResponseBuilder responseBuilder = new ResponseBuilder();
        try {
            String path = request.getPath();
            switch (request.getMethod()) {
                case GET:
                    if (path == "/") {
                        Stream<Path> walk = Files.walk(Paths.get(directoryPath));
                        String files = walk
                                .filter(Files::isRegularFile)
                                .map(x -> x.toString())
                                .reduce((x,y) -> x+y).orElse("");
                        responseBuilder.setBody(files);
                        response = responseBuilder
                                .setStatus(200)
                                .setPhrase("OK")
                                .setVersion("HTTP/1.0")
                                .createResponse();

                    }
                    break;
                case POST:
                    break;
                case PUT:
                    break;
                case DELETE:
                    break;
                case HEAD:
                    break;
            }
        } catch (IOException e) {
            response =  responseBuilder
                    .setVersion("HTTP/1.0")
                    .setStatus(500)
                    .setPhrase("Internal Server Error")
                    .setBody("")
                    .createResponse();
        }
        return response;
    }
}
