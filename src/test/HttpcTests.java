import CLI.Httpc;
import com.beust.jcommander.JCommander;
import http.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpcTests {

    @Test
    void GetStatus200(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "http://www.httpbin.org/"};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void GetStatus418(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "http://www.httpbin.org/status/418"};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        assertEquals(418, response.getStatus());
        assertEquals("I'M A TEAPOT", response.getPhrase());
    }

    @Test
    void PostStatus200(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"POST", "http://www.httpbin.org/status/204"};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        assertEquals(204, response.getStatus());
    }

    @Test
    void PostDataQuery(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String query = "?riding=dirty";

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything" + query};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(query));
    }

    @Test
    void PostDataInline(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String data = "{test:stuff}";

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything", "-d", data};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(data));
    }

}
