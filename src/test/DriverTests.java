import CLI.Httpc;
import com.beust.jcommander.JCommander;
import http.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DriverTests {

    @Test
    void DriverTest(){
        try{
            Driver.main(new String[]{"GET", "http://www.httpbin.org/"});
        } catch(Exception e){
            assertEquals(null, e);
        }
    }
}
