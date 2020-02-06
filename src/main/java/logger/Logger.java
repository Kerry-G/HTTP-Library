package logger;

public final class Logger {

    static Verbosity verbosity = Verbosity.Regular;

    public static void setVerbosity(Verbosity verbosity) {
        Logger.verbosity = verbosity;
    }

    private static void internalPrintLn(String s){
        if(verbosity.equals(Verbosity.None)) return;
        System.out.println(s);
    }

    public static void println(String s){
        internalPrintLn(s);
    }
    public static void debug(String s){
        if(verbosity.equals(Verbosity.Debug))  internalPrintLn("[DEBUG]: " + s);
    }
    public static void error(String s){ internalPrintLn("[ERROR]: " + s);
    }

}
