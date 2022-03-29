package com.badzzz.pasteany.core.nsd.peer.connection;

import com.imob.lib.lib_common.Logger;

public class PeerConnectionListenerWrapper implements PeerConnectionListener {
    private static final String TAG = "PeerConnectionListenerWrapper";

    private PeerConnectionListener base;
    private boolean printLog;

    private String tag = TAG + " # " + hashCode();

    public PeerConnectionListenerWrapper(PeerConnectionListener base, boolean printLog) {
        this.base = base;
        this.printLog = printLog;
    }

    public PeerConnectionListenerWrapper(PeerConnectionListener base) {
        this(base, false);
    }


    @Override
    public void onPeerDiscovered(String did, String ip, int port) {
        base.onPeerDiscovered(did, ip, port);
        if (printLog) {
            Logger.i(tag, "onPeerDiscovered, did: " + did + ", ip: " + ip + ", port: " + port);
        }
    }

    @Override
    public void onStalePeerDiscovered(String did, String ip, int port, long lastTime) {
        base.onStalePeerDiscovered(did, ip, port, lastTime);

        if (printLog) {
            Logger.i(tag, "onStalePeerDiscovered, did: " + did + ", ip: " + ip + ", port: " + port);
        }
    }

    @Override
    public void onPeerConnecting(String did, String ip, int port) {
        base.onPeerConnecting(did, ip, port);

        if (printLog) {
            Logger.i(tag, "onPeerConnecting, did: " + did + ", ip: " + ip + ", port: " + port);
        }
    }

    @Override
    public void onPeerConnected(String did, String ip, int port) {
        base.onPeerConnected(did, ip, port);

        if (printLog) {
            Logger.i(tag, "onPeerConnected, did: " + did + ", ip: " + ip + ", port: " + port);
        }
    }

    @Override
    public void onPeerConnectFailed(String did, String ip, int port) {
        base.onPeerConnectFailed(did, ip, port);

        if (printLog) {
            Logger.i(tag, "onPeerConnectFailed, did: " + did + ", ip: " + ip + ", port: " + port);
        }
    }
}
