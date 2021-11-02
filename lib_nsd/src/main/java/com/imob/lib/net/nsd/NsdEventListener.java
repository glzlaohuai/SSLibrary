package com.imob.lib.net.nsd;

import javax.jmdns.ServiceEvent;

public interface NsdEventListener {

    void onInitSucceeded(NsdManager nsdManager);

    void onInitFailed(String msg, Exception e);

    void onDestroyed(NsdManager nsdManager);

    void onRegisterServiceFailed(NsdManager nsdManager, String type, String name, int port, String text, String msg, Exception e);

    void onServiceDiscoveryed(NsdManager nsdManager, ServiceEvent event);
}
