package com.imob.lib.net.nsd;

import javax.jmdns.ServiceInfo;

public interface NsdEventListener {

    void onCreated(NsdNode nsdNode);

    void onCreateFailed(String msg, Exception e);

    void onDestroyed(NsdNode nsdNode);

    void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e);

    void onServiceDiscoveryed(NsdNode nsdNode, ServiceInfo event);

    void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name);

    void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e);


    void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port);
}
