package com.imob.app.pasteew;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerWrapper;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.SettingsManager;
import com.badzzz.pasteany.lib.core.android.AndroidPlatformManager;
import com.github.anrwatchdog.ANRWatchDog;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerWrapper;
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
        // TODO: 2021/11/30 just for tests, should be removed later，设置默认的设备、注册&监听的服务名
        if (!SettingsManager.getInstance().hasSavedServiceName()) {
            SettingsManager.getInstance().saveServiceName("a_test_service_name");
        }
        if (!SettingsManager.getInstance().hasSavedDeviceName()) {
            SettingsManager.getInstance().saveDeviceName(Build.BRAND + "#" + Build.DEVICE.toString());
        }
        androidPlatformManager.initPlatform();

        //just for test, logPrint
        forTest();
        kickOffService();
    }

    private void kickOffService() {
        Intent intent = new Intent(getApplicationContext(), TestService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void forTest() {
        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerWrapper(new ConnectedPeerEventListenerAdapter(), true));
        Peer.monitorPeerState(new PeerListenerWrapper(new PeerListenerAdapter(), true));
    }


    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setEnableCatchAnrTrace(true);
        CrashReport.initCrashReport(getApplicationContext(), Constants.Others.BUGLY_APP_ID, true, strategy);
    }

}
