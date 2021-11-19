package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;

import java.util.HashSet;
import java.util.Set;

public class ConnectedPeersHolder {

    private boolean destroyed = false;

    private Set<Peer> serverSidePeers = new HashSet<>();
    private Set<ClientNode> clientNodeSet = new HashSet<>();


    public Set<Peer> getServerSidePeers() {
        return serverSidePeers;
    }

    public Set<ClientNode> getClientNodeSet() {
        return clientNodeSet;
    }


    public synchronized void destroy() {
        if (!destroyed) {
            destroyed = true;
            doDestroy();
        }
    }

    private void doDestroy() {

    }


}
