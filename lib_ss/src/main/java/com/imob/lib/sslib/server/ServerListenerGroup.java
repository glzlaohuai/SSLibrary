package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerListenerGroup implements ServerListener {

    private final Queue<ServerListener> queue = new ConcurrentLinkedQueue<>();

    public void add(ServerListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }

    public void remove(ServerListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }

    public void clear() {
        queue.clear();
    }

    @Override
    public void onCreated(ServerNode serverNode) {
        for (ServerListener listener : queue) {
            listener.onCreated(serverNode);
        }
    }

    @Override
    public void onCreateFailed(Exception exception) {
        for (ServerListener listener : queue) {
            listener.onCreateFailed(exception);
        }
    }

    @Override
    public void onDestroyed(ServerNode serverNode) {
        for (ServerListener listener : queue) {
            listener.onDestroyed(serverNode);
        }
    }

    @Override
    public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
        for (ServerListener listener : queue) {
            listener.onCorrupted(serverNode, msg, e);
        }
    }

    @Override
    public void onIncomingClient(ServerNode serverNode, Peer peer) {
        for (ServerListener listener : queue) {
            listener.onIncomingClient(serverNode, peer);
        }
    }
}
