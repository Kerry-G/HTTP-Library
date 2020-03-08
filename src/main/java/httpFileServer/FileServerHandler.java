package httpFileServer;

import http.Request;
import http.Response;
import http.ResponseBuilder;
import http.RequestHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        /*
        Date Header
         */
        ResponseBuilder responseBuilder = new ResponseBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        responseBuilder
                .addHeader("Date", dtf.format(now))
                .addHeader("Content-Type", "text/html");

        try {
            String path = request.getPath();
            if (path.contains("../")){
                response = responseBuilder
                        .setBody("Invalid path (cannot contain ../)")
                        .setPhrase("Bad Request")
                        .setVersion("HTTP/1.0")
                        .setStatus(400)
                        .createResponse();
                return response;
            }
            switch (request.getMethod()) {
                case GET:
                    if (path.equals("/")) {
                        Stream<Path> list = Files.list(Paths.get(directoryPath));
                        String files = list
                                .filter(Files::isRegularFile)
                                .map(x -> x.toString())
                                .reduce((x,y) -> x+"\n"+y).orElse("");

                        response = responseBuilder
                                .setBody(files)
                                .setStatus(200)
                                .setPhrase("OK")
                                .setVersion("HTTP/1.0")
                                .createResponse();
                    } else {
                        path = path.replace("/","");
                        Path filePath = Paths.get(path);
                        if (Files.isReadable(filePath)){
                            String content = new String(Files.readAllBytes(filePath));
                            response = responseBuilder
                                    .setBody(content)
                                    .setStatus(200)
                                    .setPhrase("OK")
                                    .setVersion("HTTP/1.0")
                                    .createResponse();
                        } else {
                            response = responseBuilder
                                    .setBody("")
                                    .setStatus(404)
                                    .setPhrase("Not Found")
                                    .setVersion("HTTP/1.0")
                                    .createResponse();
                        }
                    }
                    break;
                case POST:
                    if (path.equals("/")) {
                        Stream<Path> list = Files.list(Paths.get(directoryPath));
                        String files = list
                                .filter(Files::isRegularFile)
                                .map(x -> x.toString())
                                .reduce((x,y) -> x+"\n"+y).orElse("");

                        response = responseBuilder
                                .setBody(files)
                                .setStatus(200)
                                .setPhrase("OK")
                                .setVersion("HTTP/1.0")
                                .createResponse();
                    } else {
                        path = path.substring(1);
                        Path filePath = Paths.get(path);
                        Files.write(filePath, request.getBody().getBytes());
                        response = responseBuilder
                                    .setBody(request.getBody())
                                    .setStatus(200)
                                    .setPhrase("OK")
                                    .setVersion("HTTP/1.0")
                                    .createResponse();

                    }
                    break;
                case PUT:
                    break;
                case DELETE:
                    break;
                case HEAD:
                    break;
            }
        } catch (Exception e) {
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
