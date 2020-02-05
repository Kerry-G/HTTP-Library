package http;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Request {

    private Method method;
    private InetAddress url;
    private Integer version;

    private ArrayList<Header> headerList;
    private byte[] body;



    public Response send() {

        try {
            Socket socket = new Socket(this.url, 80);

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());

            out.write("GET / HTTP/1.0\r\nUser-Agent: Hello\r\n\r\n");
            out.flush();

            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
            }

            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
