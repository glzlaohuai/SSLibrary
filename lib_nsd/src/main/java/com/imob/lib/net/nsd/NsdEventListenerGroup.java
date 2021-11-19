package com.imob.lib.net.nsd;

import java.util.HashSet;
import java.util.Set;

import javax.jmdns.ServiceEvent;

public class NsdEventListenerGroup implements NsdEventListener {

    private Set<NsdEventListener> set = new HashSet<>();

    public synchronized void clear() {
        set.clear();
    }


    public synchronized void add(NsdEventListener listener) {
        if (listener != null) {
            set.add(listener);
        }
    }


    public synchronized void remove(NsdEventListener listener) {
        if (listener != null) {
            set.remove(listener);
        }
    }

    @Override
    public void onCreated(NsdNode nsdNode) {
        for (NsdEventListener listener : set) {
            listener.onCreated(nsdNode);
        }
    }

    @Override
    public void onCreateFailed(String msg, Exception e) {
        for (NsdEventListener listener : set) {
            listener.onCreateFailed(msg, e);
        }
    }

    @Override
    public void onDestroyed(NsdNode nsdNode) {
        for (NsdEventListener listener : set) {
            listener.onDestroyed(nsdNode);
        }
    }

    @Override
    public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {
        for (NsdEventListener listener : set) {
            listener.onRegisterServiceFailed(nsdNode, type, name, port, text, msg, e);
        }

    }

    @Override
    public void onServiceDiscoveryed(NsdNode nsdNode, ServiceEvent event) {
        for (NsdEventListener listener : set) {
            listener.onServiceDiscoveryed(nsdNode, event);
        }
    }

    @Override
    public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {
        for (NsdEventListener listener : set) {
            listener.onSuccessfullyWatchService(nsdNode, type, name);
        }
    }

    @Override
    public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {
        for (NsdEventListener listener : set) {
            listener.onWatchServiceFailed(nsdNode, type, name, msg, e);
        }
    }

    @Override
    public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
        for (NsdEventListener listener : set) {
            listener.onSuccessfullyRegisterService(nsdNode, type, name, text, port);
        }

    }
}
