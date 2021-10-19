package com.imob.lib.sslib.utils;

public class Logger {

    private static final String TAG = "SS_LIB";
    private static final boolean DEBUG = true;

    public static void i(String msg) {
        if (DEBUG) {
            System.out.println(TAG + ": " + msg);
        }
    }


    public static void print(Throwable throwable) {
        if (DEBUG) {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }


}
