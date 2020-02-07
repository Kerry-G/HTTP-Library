import CLI.Httpc;
import com.beust.jcommander.JCommander;
import http.Request;
import http.Response;
import logger.Logger;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

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
    void ErrorHandlingUrlMalformatted(){
        TestablePrintStream testablePrintStream = new TestablePrintStream();
        Logger.setPrintStream(testablePrintStream);

        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "sasd..ww.httpbin.asdasd...org/"};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNull(response);
        assertTrue(testablePrintStream.getInternalListString().stream().anyMatch(s -> s.equals("[ERROR]: No such URL is known")));

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

    @Test
    void PostFile(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything", "-f", "C:\\Users\\jonat\\dev\\HTTP-Library\\src\\test\\data.txt"};
        argv = new String[]{"POST", "http://www.httpbin.org/anything", "-f", "src/test/data.txt"};

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        System.out.println(response.getBody());
        assertEquals(200, response.getStatus());
        assertTrue(
                response.getBody().contains("here") &&
                response.getBody().contains("is") &&
                response.getBody().contains("some") &&
                response.getBody().contains("data"));
    }

    @Test
    void PostDataWithFileAndData(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();


        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything", "-d", " data", "-f", "file" };

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNull(response);

    }

    @Test
    void GetRedirectionUnderMaxTries(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "-v", "http://httpbin.org/absolute-redirect/" + Integer.toString(Request.MAXTRIES-1)};
        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        Logger.println(response.getHeaders().toString());
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void GetRedirectionOverMaxTries(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "-v", "http://httpbin.org/absolute-redirect/" + Integer.toString(Request.MAXTRIES+1)};
        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        Logger.println(response.getHeaders().toString());
        assertNotNull(response);
        assertEquals(302, response.getStatus());
    }


    @Test
    void PostDataWithFile(){

        final URL resourceAsStream = this.getClass().getClassLoader().getResource("data.txt");
        assert resourceAsStream != null;
        final String path = resourceAsStream.getPath().substring(1);

        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything", "-f", path };

        httpcJc.parse(argv);

        final Response response = httpc.interpret().orElse(null);
        assertNotNull(response);
        System.out.println(response.getBody());

    }



}

/*
final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("data.txt");
        assert resourceAsStream != null;
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
                String content = br.lines().reduce("", String::concat);
                System.out.println(content);
*/
