package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;

public interface ServerListener {

    void onCreated();

    void onCreateFailed(Exception exception);

    void onDestroyed();

    void onCorrupted(String msg, Exception e);

    void onIncomingClient(Peer peer);
}
