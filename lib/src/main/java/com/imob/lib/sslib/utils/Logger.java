package com.imob.lib.sslib.utils;


public class Logger {

    private final static boolean debug = true;

    public static void e(Exception exception) {
        if (debug) {
            if (exception != null) {
                exception.printStackTrace();
            }
        }
    }


    public static void i(String tag, String msg) {
        if (debug) {
            System.out.println("< " + tag + " > : " + msg);
        }
    }
}
