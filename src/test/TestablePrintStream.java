import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A Mock of a PrintStream to catch all the logs the application is sending.
 */
public class TestablePrintStream extends PrintStream {

    List<String> internalListString;

    public TestablePrintStream() {
        super(new OutputStream() {
            @Override public void write(int i) throws IOException {
                throw new IOException();
            }
        });
        internalListString = new ArrayList<>();
    }


    @Override public void println(String x) {
        internalListString.add(x);
    }

    @Override public String toString() {
        return internalListString.toString();
    }

    public List<String> getInternalListString(){
        return internalListString;
    }
}
