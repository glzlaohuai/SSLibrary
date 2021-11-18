package com.badzzz.pasteany.core.nsd;

public class NsdServiceHandler {

    private static final String TAG = "NsdServiceHandler";

    private boolean isInited;
    private boolean isDestroyed;


    public synchronized void init() {
        if (!isDestroyed && !isInited) {
            isInited = true;
            doInit();
        }
    }

    private synchronized void doInit() {

    }


    public synchronized void destroy() {
        if (!isDestroyed) {
            isDestroyed = true;
            doDestroy();
        }
    }


    private synchronized void doDestroy() {

    }
}
