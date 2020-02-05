import CLI.Httpc;
import com.beust.jcommander.JCommander;
import logger.Logger;

class Driver {
    public static void main(String[] args) {
        try {
            Httpc httpc = new Httpc();
            JCommander.newBuilder()
                      .addObject(httpc)
                      .build()
                      .parse(args);

            Logger.println(httpc.toString());

        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
