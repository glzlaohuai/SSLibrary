package com.imob.lib.lib_common;

import java.io.Closeable;
import java.io.IOException;

public class Closer {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Logger.e(e);
            }
        }
    }
}
