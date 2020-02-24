package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServiceThread extends Thread {

    private Socket socket;


    public ServiceThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        OutputStreamWriter out;
        BufferedReader in;

        try{
            out = new OutputStreamWriter(socket.getOutputStream());
            in = new BufferedReader (new InputStreamReader(socket.getInputStream()));

            Request.fromBufferedReader(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
