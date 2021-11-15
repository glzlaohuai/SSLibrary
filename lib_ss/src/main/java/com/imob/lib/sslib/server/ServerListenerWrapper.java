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
    public void onCreated() {
        Logger.i(TAG, "onCreated");

        base.onCreated();
    }

    @Override
    public void onCreateFailed(Exception exception) {
        Logger.i(TAG, "onCreateFailed, " + exception);

        base.onCreateFailed(exception);
    }

    @Override
    public void onDestroyed() {
        Logger.i(TAG, "onDestroyed");

        base.onDestroyed();
    }

    @Override
    public void onCorrupted(String msg, Exception e) {
        Logger.i(TAG, "onCorrupted, msg: " + msg + ", exception: " + e);

        base.onCorrupted(msg, e);
    }

    @Override
    public void onIncomingClient(Peer peer) {
        Logger.i(TAG, "onIncomingClient: " + peer.getSocket());

        base.onIncomingClient(peer);
    }
}
