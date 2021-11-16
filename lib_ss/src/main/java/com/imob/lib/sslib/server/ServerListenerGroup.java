package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

import java.util.LinkedHashSet;
import java.util.Set;

public class ServerListenerGroup implements ServerListener {

    private final Set<ServerListener> set = new LinkedHashSet<>();

    public void add(ServerListener listener) {
        if (listener != null) {
            set.add(listener);
        }
    }

    public void remove(ServerListener listener) {
        if (listener != null) {
            set.remove(listener);
        }
    }

    @Override
    public void onCreated(ServerNode serverNode) {
        for (ServerListener listener : set) {
            listener.onCreated(serverNode);
        }
    }

    @Override
    public void onCreateFailed(Exception exception) {
        for (ServerListener listener : set) {
            listener.onCreateFailed(exception);
        }
    }

    @Override
    public void onDestroyed(ServerNode serverNode) {
        for (ServerListener listener : set) {
            listener.onDestroyed(serverNode);
        }
    }

    @Override
    public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
        for (ServerListener listener : set) {
            listener.onCorrupted(serverNode, msg, e);
        }
    }

    @Override
    public void onIncomingClient(ServerNode serverNode, Peer peer) {
        for (ServerListener listener : set) {
            listener.onIncomingClient(serverNode, peer);
        }
    }
}
