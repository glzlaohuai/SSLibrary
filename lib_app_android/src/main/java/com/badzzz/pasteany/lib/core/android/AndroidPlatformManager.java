package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;
import com.badzzz.pasteany.core.utils.Constants;

public class AndroidPlatformManager extends IPlatformManager {

    private Context context;
    private AndroidAppManager androidAppManager;

    public AndroidPlatformManager(Context context) {
        super();
        this.context = context;
        androidAppManager = new AndroidAppManager(context);
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
