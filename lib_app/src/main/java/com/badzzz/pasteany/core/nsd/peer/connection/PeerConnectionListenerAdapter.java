package com.badzzz.pasteany.core.nsd.peer.connection;

public class PeerConnectionListenerAdapter implements PeerConnectionListener {
    @Override
    public void onPeerDiscovered(String did, String ip, int port) {

    }

    @Override
    public void onStalePeerDiscovered(String did, String ip, int port, long lastTime) {

    }

    @Override
    public void onPeerConnecting(String did, String ip, int port) {

    }

    @Override
    public void onPeerConnected(String did, String ip, int port) {

    }

    @Override
    public void onPeerConnectFailed(String did, String ip, int port) {

    }
}
