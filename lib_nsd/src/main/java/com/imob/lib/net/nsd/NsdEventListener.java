package com.imob.lib.net.nsd;

import javax.jmdns.ServiceEvent;

public interface NsdEventListener {

    void onInitSucceeded(NsdManager nsdManager);

    void onInitFailed(String msg, Exception e);

    void onDestroyed(NsdManager nsdManager);

    void onRegisterServiceFailed(NsdManager nsdManager, String type, String name, int port, String text, String msg, Exception e);

    void onServiceDiscoveryed(NsdManager nsdManager, ServiceEvent event);

    void onSuccessfullyWatchService(NsdManager nsdManager, String type, String name);

    void onWatchServiceFailed(NsdManager nsdManager, String type, String name, String msg, Exception e);


    void onSuccessfullyRegisterService(NsdManager nsdManager, String type, String name, String text, int port);
}
