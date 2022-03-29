package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

public interface ServerListener {

    void onCreated(ServerNode serverNode);

    void onCreateFailed(ServerNode serverNode, Exception exception);

    void onDestroyed(ServerNode serverNode, String reason, Exception e);

    void onCorrupted(ServerNode serverNode, String msg, Exception e);

    void onIncomingClient(ServerNode serverNode, Peer peer);
}
