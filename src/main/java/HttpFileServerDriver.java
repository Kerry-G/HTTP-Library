import http.Server;
import httpClient.Httpc;
import com.beust.jcommander.JCommander;
import http.Constants;
import httpFileServer.Httpfs;
import logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Starting class of the HTTP Library project
 *
 * @author Kerry Gougeon Ducharme (40028722) and Jonathan Mongeau (40006501)
 */
class HttpFileServerDriver {
    public static void main(String[] args) {
        Server server = new Server();
        server.initialize();
    }
}
