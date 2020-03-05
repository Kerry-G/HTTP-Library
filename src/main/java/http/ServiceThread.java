package http;

import httpFileServer.FileServerHandler;
import logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServiceThread extends Thread {

    private Socket socket;
    private String directoryPath;


    public ServiceThread(Socket clientSocket, String directoryPath) {
        this.socket = clientSocket;
        this.directoryPath = directoryPath;
    }

    @Override
    public void run() {
        OutputStreamWriter out;
        BufferedReader in;

        try{
            out = new OutputStreamWriter(socket.getOutputStream());
            in = new BufferedReader (new InputStreamReader(socket.getInputStream()));


            /*
            Get the request from the buffered reader, create a handler and get the appropriate response
             */
            final Request request = Request.fromBufferedReader(in);
            RequestHandler handler = new FileServerHandler(this.directoryPath);
            Response response = handler.handleRequest(request);


            String serialized = response.getSerialized();
            Logger.debug(serialized);
            out.write(serialized);
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
