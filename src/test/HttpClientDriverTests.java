import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpClientDriverTests {

    @Ignore
    @Test
    void DriverTest(){
        try{
            HttpClientDriver.main(new String[]{"GET", "http://www.httpbin.org/"});
        } catch(Exception e){
            assertNull(e);
        }
    }
}
