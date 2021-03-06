package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;

import javax.jmdns.ServiceInfo;

public class NsdEventListenerWrapper implements NsdEventListener {

    private static final String TAG = "NsdEventListenerWrapper";

    private NsdEventListener base;
    private boolean printLog;

    public NsdEventListenerWrapper(NsdEventListener base, boolean printLog) {
        this.base = base;
        this.printLog = printLog;
    }

    public NsdEventListenerWrapper(NsdEventListener base) {
        this(base, true);
    }

    @Override
    public void onCreated(NsdNode nsdNode) {
        base.onCreated(nsdNode);
        if (printLog) {
            Logger.i(TAG, "onCreated: " + nsdNode);
        }
    }

    @Override
    public void onCreateFailed(String msg, Exception e) {
        base.onCreateFailed(msg, e);
        if (printLog) {
            Logger.i(TAG, "onCreateFailed, msg: " + msg + ", e: " + e);

        }
    }

    @Override
    public void onDestroyed(NsdNode nsdNode, String reason, Exception e) {
        base.onDestroyed(nsdNode, reason, e);
        if (printLog) {
            Logger.i(TAG, "onDestroyed, nsdNode: " + nsdNode + ", reason: " + reason + ", exception: " + e);
        }
    }


    @Override
    public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {
        base.onRegisterServiceFailed(nsdNode, type, name, port, text, msg, e);
        if (printLog) {
            Logger.i(TAG, "onRegisterServiceFailed, nsdNode: " + nsdNode + ", type:" + type + ", name: " + name + ", txt: " + text + ", port: " + port + ", msg: " + msg + ", exception: " + e);

        }
    }

    @Override
    public void onServiceDiscovered(NsdNode nsdNode, ServiceInfo info) {
        base.onServiceDiscovered(nsdNode, info);
        if (printLog) {
            Logger.i(TAG, "onServiceDiscovered, nsdNode: " + nsdNode + ", info: " + info);

        }
    }

    @Override
    public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {
        base.onSuccessfullyWatchService(nsdNode, type, name);
        if (printLog) {
            Logger.i(TAG, "onSuccessfullyWatchService, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name);

        }
    }

    @Override
    public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {
        base.onWatchServiceFailed(nsdNode, type, name, msg, e);
        if (printLog) {
            Logger.i(TAG, "onWatchServiceFailed, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name + ", msg: " + msg + ", exception: " + e);

        }
    }

    @Override
    public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
        base.onSuccessfullyRegisterService(nsdNode, type, name, text, port);
        if (printLog) {
            Logger.i(TAG, "onSuccessfullyRegisterService, nsdNode: " + nsdNode + ", type: " + type + ", name: " + name + ", text: " + text + ", port: " + port);

        }
    }
}
