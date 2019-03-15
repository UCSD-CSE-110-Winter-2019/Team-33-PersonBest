package android.util;

public class Log {
    public static int i(String tag, String msg) {
        System.err.println("Log/Info: " + tag + ": " + msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.err.println("Log/Err: " + tag + ": " + msg);
        return 0;
    }
}
