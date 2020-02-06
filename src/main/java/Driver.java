import CLI.CommandGet;
import CLI.CommandPost;
import CLI.Httpc;
import com.beust.jcommander.JCommander;
import logger.Logger;

class Driver {
    public static void main(String[] args) {
        try {
            Httpc httpc = new Httpc();
            CommandGet commandGet = new CommandGet();
            CommandPost commandPost = new CommandPost();
            JCommander.newBuilder()
                      .addCommand("GET", commandGet)
                      .addCommand("POST", commandPost)
                      .addObject(httpc)
                      .build()
                      .parse(args);

            Logger.println(httpc.toString());
            Logger.println(commandGet.toString());

        }
        catch(Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
}
