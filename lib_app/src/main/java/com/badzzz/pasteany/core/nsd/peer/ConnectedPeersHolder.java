package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.sslib.client.ClientListenerAdapter;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.net.Inet4Address;
import java.util.HashSet;
import java.util.Set;

import javax.jmdns.ServiceEvent;

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

    private PeerListener peerListener = new PeerListenerAdapter() {
        @Override
        public void onIOStreamOpened(Peer peer) {
            super.onIOStreamOpened(peer);


        }
    };


    public synchronized void destroy() {
        if (!destroyed) {
            destroyed = true;
            doDestroy();
        }
    }


    private boolean hasClientNodeReferToThisIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        for (ClientNode clientNode : clientNodeSet) {
            if (clientNode.getIp() != null && clientNode.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void doDestroy() {
        Peer.setGlobalPeerListener(null);
    }

    public synchronized void afterServiceDiscoveryed(ServiceEvent event) {
        if (event != null) {
            Inet4Address inetAddresses = event.getInfo().getInet4Address();
            int port = event.getInfo().getPort();

            if (inetAddresses != null) {
                String ip4 = inetAddresses.getHostAddress();
                if (ip4 != null && !hasClientNodeReferToThisIP(ip4)) {
                    ClientNode node = new ClientNode(ip4, port, new ClientListenerAdapter());
                    node.create(Constants.Others.TIMEOUT);
                    clientNodeSet.add(node);
                }
            }
        }
    }
}
