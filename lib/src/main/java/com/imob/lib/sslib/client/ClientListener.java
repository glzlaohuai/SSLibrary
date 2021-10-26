package com.imob.lib.sslib.client;

import com.imob.lib.sslib.peer.PeerListener;

public interface ClientListener extends PeerListener {

    void onClientCreated(ClientNode clientNode);

    void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception);


}
