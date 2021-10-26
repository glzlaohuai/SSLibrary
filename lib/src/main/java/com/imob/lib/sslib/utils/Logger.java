package com.imob.lib.sslib.utils;

public class Logger {

    private static final String TAG = "SS_LIB";
    private static final boolean DEBUG = true;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            System.out.println(TAG + " - " + tag + " : " + msg);
        }
    }


    public static void e(Throwable throwable) {
        if (DEBUG) {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }


}
