package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

class ServerListenerWrapper implements ServerListener {
    private static final String TAG = "ServerManager";

    private ServerListener base;

    public ServerListenerWrapper(ServerListener base) {
        this.base = base;
    }

    @Override
    public void onCreated(ServerNode serverNode) {
        Logger.i(TAG, "onCreated, serverNode: " + serverNode);

        base.onCreated(serverNode);
    }

    @Override
    public void onCreateFailed(Exception exception) {
        Logger.i(TAG, "onCreateFailed, " + exception);

        base.onCreateFailed(exception);
    }

    @Override
    public void onDestroyed(ServerNode serverNode) {
        Logger.i(TAG, "onDestroyed, serverNode: " + serverNode);

        base.onDestroyed(serverNode);
    }

    @Override
    public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
        Logger.i(TAG, "onCorrupted, serverNode: " + serverNode + ", msg: " + msg + ", exception: " + e);

        base.onCorrupted(serverNode, msg, e);
    }

    @Override
    public void onIncomingClient(ServerNode serverNode, Peer peer) {
        Logger.i(TAG, "onIncomingClient, serverNode: " + serverNode + ", peer: " + peer.getTag());

        base.onIncomingClient(serverNode, peer);
    }
}
