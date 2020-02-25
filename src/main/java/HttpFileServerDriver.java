import http.Server;

/**
 * Starting class of the HTTP Library project
 *
 * @author Kerry Gougeon Ducharme (40028722) and Jonathan Mongeau (40006501)
 */
class HttpFileServerDriver {
    public static void main(String[] args) {
        Server server = new Server( 8080, "./");
        server.initialize();
    }
}
