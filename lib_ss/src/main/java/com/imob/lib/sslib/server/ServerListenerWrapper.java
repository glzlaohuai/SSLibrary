package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

public class ServerListenerWrapper implements ServerListener {
    private static final String TAG = "ServerNodeListenerWrapper";

    private ServerListener base;
    private boolean printLog;

    public ServerListenerWrapper(ServerListener base, boolean printLog) {
        this.base = base;
        this.printLog = printLog;
    }

    public ServerListenerWrapper(ServerListener base) {
        this(base, true);
    }

    @Override
    public void onCreated(ServerNode serverNode) {
        if (printLog) {
            Logger.i(TAG, "onCreated, serverNode: " + serverNode);
        }

        base.onCreated(serverNode);
    }

    @Override
    public void onCreateFailed(Exception exception) {
        if (printLog) {
            Logger.i(TAG, "onCreateFailed, " + exception);

        }

        base.onCreateFailed(exception);
    }

    @Override
    public void onDestroyed(ServerNode serverNode) {
        if (printLog) {
            Logger.i(TAG, "onDestroyed, serverNode: " + serverNode);

        }

        base.onDestroyed(serverNode);
    }

    @Override
    public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
        if (printLog) {
            Logger.i(TAG, "onCorrupted, serverNode: " + serverNode + ", msg: " + msg + ", exception: " + e);

        }

        base.onCorrupted(serverNode, msg, e);
    }

    @Override
    public void onIncomingClient(ServerNode serverNode, Peer peer) {
        if (printLog) {
            Logger.i(TAG, "onIncomingClient, serverNode: " + serverNode + ", peer: " + peer.getTag());

        }

        base.onIncomingClient(serverNode, peer);
    }
}
