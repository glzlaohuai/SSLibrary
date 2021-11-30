package com.imob.app.pasteew;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.badzzz.pasteany.lib.core.android.AndroidPlatformManager;

public class XApplication extends Application {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        AndroidPlatformManager androidPlatformManager = new AndroidPlatformManager(this);

        // TODO: 2021/11/30 just for tests, should be removed later
        PreferenceManagerWrapper.getInstance().saveDeviceName(Build.BRAND + "#" + Build.DEVICE.toString());
        PreferenceManagerWrapper.getInstance().saveServiceName("a_test_service_name");

        androidPlatformManager.initPlatform();
    }
}
