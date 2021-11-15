package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

public interface ServerListener {

    void onCreated(ServerNode serverNode);

    void onCreateFailed(Exception exception);

    void onDestroyed(ServerNode serverNode);

    void onCorrupted(ServerNode serverNode, String msg, Exception e);

    void onIncomingClient(ServerNode serverNode, Peer peer);
}
