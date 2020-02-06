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
}
