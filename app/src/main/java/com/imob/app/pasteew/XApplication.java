package com.imob.app.pasteew;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerWrapper;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.badzzz.pasteany.lib.core.android.AndroidPlatformManager;
import com.github.anrwatchdog.ANRWatchDog;
import com.tencent.bugly.crashreport.CrashReport;

public class XApplication extends Application {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initBugly();
        new ANRWatchDog().start();

        AndroidPlatformManager androidPlatformManager = new AndroidPlatformManager(this);
        // TODO: 2021/11/30 just for tests, should be removed later
        PreferenceManagerWrapper.getInstance().saveDeviceName(Build.BRAND + "#" + Build.DEVICE.toString());
        PreferenceManagerWrapper.getInstance().saveServiceName("a_test_service_name");
        androidPlatformManager.initPlatform();

        //just for test, logPrint
        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerWrapper(new ConnectedPeerEventListenerAdapter(), true));
    }


    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setEnableCatchAnrTrace(true);
        CrashReport.initCrashReport(getApplicationContext(), Constants.Others.BUGLY_APP_ID, true, strategy);
    }

}
