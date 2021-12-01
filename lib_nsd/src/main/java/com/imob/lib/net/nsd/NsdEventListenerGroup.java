package com.imob.lib.net.nsd;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jmdns.ServiceEvent;

public class NsdEventListenerGroup implements NsdEventListener {

    private Queue<NsdEventListener> queue = new ConcurrentLinkedQueue<>();

    public synchronized void clear() {
        queue.clear();
    }


    public synchronized void add(NsdEventListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }


    public synchronized void remove(NsdEventListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }

    @Override
    public void onCreated(NsdNode nsdNode) {
        for (NsdEventListener listener : queue) {
            listener.onCreated(nsdNode);
        }
    }

    @Override
    public void onCreateFailed(String msg, Exception e) {
        for (NsdEventListener listener : queue) {
            listener.onCreateFailed(msg, e);
        }
    }

    @Override
    public void onDestroyed(NsdNode nsdNode) {
        for (NsdEventListener listener : queue) {
            listener.onDestroyed(nsdNode);
        }
    }

    @Override
    public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {
        for (NsdEventListener listener : queue) {
            listener.onRegisterServiceFailed(nsdNode, type, name, port, text, msg, e);
        }

    }

    @Override
    public void onServiceDiscoveryed(NsdNode nsdNode, ServiceEvent event) {
        for (NsdEventListener listener : queue) {
            listener.onServiceDiscoveryed(nsdNode, event);
        }
    }

    @Override
    public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {
        for (NsdEventListener listener : queue) {
            listener.onSuccessfullyWatchService(nsdNode, type, name);
        }
    }

    @Override
    public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {
        for (NsdEventListener listener : queue) {
            listener.onWatchServiceFailed(nsdNode, type, name, msg, e);
        }
    }

    @Override
    public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
        for (NsdEventListener listener : queue) {
            listener.onSuccessfullyRegisterService(nsdNode, type, name, text, port);
        }

    }
}
