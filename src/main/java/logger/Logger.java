package logger;

import java.io.PrintStream;

/**
 * Centralization of the logging systems.
 * You can set the amount of details you can with setVerbosity by giving it an enum Verbosity.
 * @see #setVerbosity
 * @see Verbosity
 */
public final class Logger {

    static PrintStream ps = System.out;

    static Verbosity verbosity = Verbosity.Regular;

    public static void setVerbosity(Verbosity verbosity) {
        Logger.verbosity = verbosity;
    }

    public static void setPrintStream(PrintStream ps){
        Logger.ps = ps;
    }

    private static void internalPrintLn(String s){
        if(verbosity.equals(Verbosity.None)) return;
        ps.println(s);
    }

    public static void println(String s){
        internalPrintLn(s);
    }
    public static void debug(String s){
        if(verbosity.equals(Verbosity.Debug))  {
            s.lines().forEach(x-> internalPrintLn("[DEBUG - TID:" + Thread.currentThread().getId() + "]: " + x));
        }
    }
    public static void error(String s){
        internalPrintLn("[ERROR]: " + s);
    }

}
