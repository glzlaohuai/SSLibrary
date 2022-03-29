package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

public class ServerListenerAdapter implements ServerListener {
    @Override
    public void onCreated(ServerNode serverNode) {

    }

    @Override
    public void onCreateFailed(ServerNode serverNode, Exception exception) {

    }

    @Override
    public void onDestroyed(ServerNode serverNode, String reason, Exception e) {

    }

    @Override
    public void onCorrupted(ServerNode serverNode, String msg, Exception e) {

    }

    @Override
    public void onIncomingClient(ServerNode serverNode, Peer peer) {

    }
}
