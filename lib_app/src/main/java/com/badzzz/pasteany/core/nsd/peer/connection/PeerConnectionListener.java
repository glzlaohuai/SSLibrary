package com.badzzz.pasteany.core.nsd.peer.connection;

public interface PeerConnectionListener {

    void onPeerDiscovered(String did, String ip, int port);

    void onStalePeerDiscovered(String did, String ip, int port, long lastTime);

    void onPeerConnecting(String did, String ip, int port);

    void onPeerConnected(String did, String ip, int port);

    void onPeerConnectFailed(String did, String ip, int port);

}
