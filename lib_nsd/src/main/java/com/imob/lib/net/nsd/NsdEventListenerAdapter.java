package com.imob.lib.net.nsd;

import javax.jmdns.ServiceEvent;

public class NsdEventListenerAdapter implements NsdEventListener {
    @Override
    public void onCreated(NsdNode nsdNode) {

    }

    @Override
    public void onCreateFailed(String msg, Exception e) {

    }

    @Override
    public void onDestroyed(NsdNode nsdNode) {

    }

    @Override
    public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {

    }

    @Override
    public void onServiceDiscoveryed(NsdNode nsdNode, ServiceEvent event) {

    }

    @Override
    public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {

    }

    @Override
    public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {

    }

    @Override
    public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {

    }
}
