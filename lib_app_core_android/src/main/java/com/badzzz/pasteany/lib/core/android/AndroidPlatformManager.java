package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;

public class AndroidPlatformManager implements IPlatformManager {

    private static AndroidPlatformManager instance;

    private static boolean inited = false;
    private final static Byte lock = 0x0;

    private Context context;
    private AndroidAppManager androidAppManager;

    public AndroidPlatformManager(Context context) {
        this.context = context;
        androidAppManager = new AndroidAppManager(context);
    }

    public static void init(Context context) {
        if (context != null && !inited) {
            synchronized (lock) {
                if (!inited) {
                    instance = new AndroidPlatformManager(context);
                }
            }
        }
    }


    @Override
    public IAppManager getAppManager() {
        return androidAppManager;
    }

    public Context getContext() {
        return context;
    }
}
