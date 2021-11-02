package com.imob.lib.lib_common;

public class Logger {


    private static final String TAG = "SS_LIB";
    private static final boolean DEBUG = true;

    public interface LogWatcher {
        void log(String log);
    }

    private static LogWatcher logWatcher;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            String info = TAG + " - " + tag + " : " + msg;
            System.out.println(info);

            if (logWatcher != null) {
                logWatcher.log(info);
            }
        }
    }

    public static void setLogWatcher(LogWatcher logWatcher) {
        Logger.logWatcher = logWatcher;
    }


    public static LogWatcher getLogWatcher() {
        return logWatcher;
    }


    public static void e(Throwable throwable) {
        if (DEBUG) {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }


}
