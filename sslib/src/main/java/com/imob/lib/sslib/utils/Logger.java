package com.imob.lib.sslib.utils;

import android.util.Log;

public class Logger {

    private final static boolean debug = false;

    public static void e(Exception exception) {
        if (debug) {
            if (exception != null) {
                exception.printStackTrace();
            }
        }
    }


    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }
}
