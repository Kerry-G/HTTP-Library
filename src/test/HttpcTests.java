import httpClient.Httpc;
import com.beust.jcommander.JCommander;
import http.Request;
import http.Response;
import logger.Logger;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class HttpcTests {

    @Test
    void GetStatus200(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "http://www.httpbin.org/"};

        final Response response = httpc.interpret(argv).orElse(null);
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


        final Response response = httpc.interpret(argv).orElse(null);
        assertNull(response);
        //assertTrue(testablePrintStream.getInternalListString().stream().anyMatch(s -> s.equals("[ERROR]: No such URL is known")));

    }

    @Test
    void GetStatus418(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "http://www.httpbin.org/status/418"};

        final Response response = httpc.interpret(argv).orElse(null);
        assertNotNull(response);
        assertEquals(418, response.getStatus());
        assertEquals("I'M A TEAPOT", response.getPhrase());
    }

    @Test
    void PostStatus200(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"POST", "http://www.httpbin.org/status/204"};


        final Response response = httpc.interpret(argv).orElse(null);
        assertNotNull(response);
        assertEquals(204, response.getStatus());
    }

    @Test
    void PostDataQuery(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String query = "?riding=dirty";

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything" + query};


        final Response response = httpc.interpret(argv).orElse(null);
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

        final Response response = httpc.interpret(argv).orElse(null);
        assertNotNull(response);
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(data));
    }

    @Test
    void PostDataWithFileUsingRelativePath(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"POST", "http://www.httpbin.org/anything", "-f", "src/test/data.txt"};

        final Response response = httpc.interpret(argv).orElse(null);
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

        final Response response = httpc.interpret(argv).orElse(null);
        assertNull(response);

    }

    @Test
    void GetRedirectionUnderMaxTries(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "-v", "http://httpbin.org/absolute-redirect/" + Integer.toString(Request.MAXTRIES-1)};

        final Response response = httpc.interpret(argv).orElse(null);
        Logger.println(response.getHeaders().toString());
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void GetRedirectionOverMaxTries(){
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"GET", "-v", "http://httpbin.org/absolute-redirect/" + Integer.toString(Request.MAXTRIES+1)};

        final Response response = httpc.interpret(argv).orElse(null);
        Logger.println(response.getHeaders().toString());
        assertNotNull(response);
        assertEquals(302, response.getStatus());
    }


    @Test
    void TestHelpCommand(){
        TestablePrintStream stream = new TestablePrintStream();
        Logger.setPrintStream(stream);
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"HELP"};
        httpc.interpret(argv).orElse(null);

        assertTrue(stream.getInternalListString().size() > 0);
        assertTrue(stream.getInternalListString().get(0).contains("httpc is a curl-like application but supports HTTP protocol only."));

    }

    @Test
    void TestHelpGet(){
        TestablePrintStream stream = new TestablePrintStream();
        Logger.setPrintStream(stream);
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"HELP", "get"};
        httpc.interpret(argv).orElse(null);

        assertTrue(stream.getInternalListString().size() > 0);
        assertTrue(stream.getInternalListString().get(0).contains("usage: httpc get [-v] [-h key:value] URL"));

    }

    @Test
    void TestHelpPost(){
        TestablePrintStream stream = new TestablePrintStream();
        Logger.setPrintStream(stream);
        Httpc httpc = new Httpc();
        final JCommander httpcJc = httpc.getJc();

        String[] argv = new String[]{"HELP", "post"};
        httpc.interpret(argv).orElse(null);

        assertTrue(stream.getInternalListString().size() > 0);
        assertTrue(stream.getInternalListString().get(0).contains("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL"));

    }



}


