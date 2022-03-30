package com.imob.lib.sslib.client;

import com.imob.lib.sslib.peer.PeerListener;

public interface ClientListener extends PeerListener {

    void onClientDestroyed(ClientNode clientNode, String reason, Exception exception);

    void onClientCreating(ClientNode clientNode);

    void onClientCreated(ClientNode clientNode);

    void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception);

}
