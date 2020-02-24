package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    Short port;
    Boolean serverOn = true;

    public Server(short port){
        this.port = port;
    }

    public Server(){
        this.port = 8080;
    }

    public void close(){
        serverOn = false;
    }

    public void initialize(){
        try {
            ServerSocket server = new ServerSocket(port);

            while (serverOn){
                Socket clientSocket = server.accept();
                ServiceThread serviceThread = new ServiceThread(clientSocket);
                serviceThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
