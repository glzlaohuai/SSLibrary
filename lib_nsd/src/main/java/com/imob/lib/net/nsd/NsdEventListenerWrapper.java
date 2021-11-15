package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;

import javax.jmdns.ServiceEvent;

class NsdEventListenerWrapper implements NsdEventListener {

    private static final String TAG = "NsdManager";

    private NsdEventListener base;

    public NsdEventListenerWrapper(NsdEventListener base) {
        this.base = base;
    }

    @Override
    public void onCreated(NsdNode nsdNode) {
        Logger.i(TAG, "onCreated: " + nsdNode);
        base.onCreated(nsdNode);
    }

    @Override
    public void onCreateFailed(String msg, Exception e) {
        Logger.i(TAG, "onCreateFailed, msg: " + msg + ", e: " + e);
        base.onCreateFailed(msg, e);
    }

    @Override
    public void onDestroyed(NsdNode nsdNode) {
        Logger.i(TAG, "onDestroyed, nsdNode: " + nsdNode);
        base.onDestroyed(nsdNode);
    }

    @Override
    public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {
        Logger.i(TAG, "onRegisterServiceFailed, nsdNode: " + nsdNode + ", type:" + type + ", name: " + name + ", txt: " + text + ", port: " + port + ", msg: " + msg + ", exception: " + e);
        base.onRegisterServiceFailed(nsdNode, type, name, port, text, msg, e);
    }

    @Override
    public void onServiceDiscoveryed(NsdNode nsdNode, ServiceEvent event) {
        Logger.i(TAG, "onServiceDiscoveryed, nsdNode: " + nsdNode + ", event: " + event.getInfo());
        base.onServiceDiscoveryed(nsdNode, event);
    }

    @Override
    public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {
        Logger.i(TAG, "onSuccessfullyWatchService, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name);
        base.onSuccessfullyWatchService(nsdNode, type, name);
    }

    @Override
    public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {
        Logger.i(TAG, "onWatchServiceFailed, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name + ", msg: " + msg + ", exception: " + e);
        base.onWatchServiceFailed(nsdNode, type, name, msg, e);
    }

    @Override
    public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
        Logger.i(TAG, "onSuccessfullyRegisterService, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name + ", text: " + text + ", port: " + port);
        base.onSuccessfullyRegisterService(nsdNode, type, name, text, port);
    }
}
