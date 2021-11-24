package com.badzzz.pasteany.lib.core.android;

import android.app.Application;
import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;
import com.badzzz.pasteany.core.utils.Constants;

public class AndroidPlatformManager extends IPlatformManager {

    private static AndroidPlatformManager instance;

    private static boolean inited = false;
    private final static Byte lock = 0x0;

    private Context context;
    private AndroidAppManager androidAppManager;

    public AndroidPlatformManager(Context context) {
        super();
        this.context = context;
        androidAppManager = new AndroidAppManager(context);
    }

    /**
     * call this in {@link Application#onCreate()}
     * @param context
     */
    public static void init(Context context) {
        if (context != null && !inited) {
            synchronized (lock) {
                if (!inited) {
                    instance = new AndroidPlatformManager(context);
                    instance.initPlatform();
                }
            }
        }
    }


    @Override
    public IAppManager getAppManager() {
        return androidAppManager;
    }

    @Override
    public String getPlatformName() {
        return Constants.Platforms.ANDROID;
    }

    public Context getContext() {
        return context;
    }
}
