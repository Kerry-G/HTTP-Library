import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

class Driver {
    public static void main(String[] args) {
        try {
            InetAddress web = InetAddress.getByName("www.google.com");
            Socket socket = new Socket(web, 80);

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());

            out.write("GET / HTTP/1.0\r\nUser-Agent: Hello\r\n\r\n");

            out.flush();

            out.write("GET / HTTP/1.0\r\nUser-Agent: Hello\r\n\r\n");

            out.flush();

            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
            }

            out.close();
            in.close();
            socket.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
