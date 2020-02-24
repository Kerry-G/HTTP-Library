package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    Integer port = 8080;
    String directoryPath = "./";
    Boolean serverOn = true;

    public Server(int port){
        this.port = port;
    }

    public Server(String directoryPath){
        this.directoryPath = directoryPath;
    }

    public Server(int port, String directoryPath){
        this.port = port;
        this.directoryPath = directoryPath;
    }

    public void close(){
        serverOn = false;
    }

    public void initialize(){
        try {
            ServerSocket server = new ServerSocket(port);

            while (serverOn){
                Socket clientSocket = server.accept();
                ServiceThread serviceThread = new ServiceThread(clientSocket, directoryPath);
                serviceThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
