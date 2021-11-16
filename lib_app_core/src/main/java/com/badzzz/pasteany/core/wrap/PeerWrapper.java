package com.badzzz.pasteany.core.wrap;

import com.imob.lib.sslib.peer.Peer;

public class PeerWrapper {
    private Peer peer;
    private String name;
    private String platform;

    public PeerWrapper(Peer peer, String name, String platform) {
        this.peer = peer;
        this.name = name;
        this.platform = platform;
    }

    public boolean isPeerConnected() {
        return peer != null && !peer.isDestroyed();
    }


    public boolean hasPeerDetails() {
        return name != null && platform != null;
    }


    public Peer getPeer() {
        return peer;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
