package logger;

public final class Logger {

    static Verbosity verbosity;

    public static void setVerbosity(Verbosity verbosity) {
        Logger.verbosity = verbosity;
    }

    public static void println(String s){
        System.out.println(s);
    }
    public static void debug(String s){
        if(verbosity.equals(Verbosity.Debug))  System.out.println("[DEBUG]: " + s);
    }
}
