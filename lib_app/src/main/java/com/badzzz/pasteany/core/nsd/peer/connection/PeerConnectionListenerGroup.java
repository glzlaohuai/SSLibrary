package com.badzzz.pasteany.core.nsd.peer.connection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeerConnectionListenerGroup implements PeerConnectionListener {

    private Queue<PeerConnectionListener> queue = new ConcurrentLinkedQueue<>();


    public synchronized void add(PeerConnectionListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }


    public synchronized void remove(PeerConnectionListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }

    public synchronized void clear() {
        queue.clear();
    }

    @Override
    public void onPeerDiscovered(String did, String ip, int port) {
        for (PeerConnectionListener listener : queue) {
            listener.onPeerDiscovered(did, ip, port);
        }
    }

    @Override
    public void onStalePeerDiscovered(String did, String ip, int port, long lastTime) {
        for (PeerConnectionListener listener : queue) {
            listener.onStalePeerDiscovered(did, ip, port, lastTime);
        }
    }

    @Override
    public void onPeerConnecting(String did, String ip, int port) {
        for (PeerConnectionListener listener : queue) {
            listener.onPeerConnecting(did, ip, port);
        }
    }

    @Override
    public void onPeerConnected(String did, String ip, int port) {
        for (PeerConnectionListener listener : queue) {
            listener.onPeerConnected(did, ip, port);
        }
    }

    @Override
    public void onPeerConnectFailed(String did, String ip, int port) {
        for (PeerConnectionListener listener : queue) {
            listener.onPeerConnectFailed(did, ip, port);
        }
    }
}
