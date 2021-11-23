package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.api.APIHandler;
import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.client.ClientListenerAdapter;
import com.imob.lib.sslib.client.ClientListenerWrapper;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.net.Inet4Address;
import java.util.HashSet;
import java.util.Set;

import javax.jmdns.ServiceEvent;

public class ConnectedPeersHandler {

    private static final String S_TAG = "ConnectedPeersHandler";

    private boolean destroyed = false;

    private Set<Peer> totalConnectedPeers = new HashSet<>();
    private Set<Peer> detailedInfoPeers = new HashSet<>();
    private Set<ClientNode> clientNodeSet = new HashSet<>();

    private String tag = S_TAG + " # " + hashCode();


    private PeerListener peerListener = new PeerListenerAdapter() {
        @Override
        public void onIOStreamOpened(Peer peer) {
            super.onIOStreamOpened(peer);
            totalConnectedPeers.add(peer);

            callbackIncomingNewPeer(peer);

            APIHandler.requestAPI(peer, Constants.PeerMsgAPI.PEER_DETAILS, new APIHandler.APIRequestListener() {
                @Override
                public void start(Peer peer, String api) {
                }

                @Override
                public void response(Peer peer, String msg) {
                    peer.setTag(msg);
                    detailedInfoPeers.add(peer);
                    callbackPeerDetailInfoGot(peer);
                }

                @Override
                public void error(Peer peer, String msg, Exception e) {
                    callbackPeerDetailInfoGotFailed(peer, msg, e);
                }

                @Override
                public void after(Peer peer) {

                }
            });
        }


        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
            super.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);

            handleIncomingMsgChunk(peer, id, chunkSize, soFar, available, chunkBytes);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            super.onIncomingMsgReadSucceeded(peer, id);

            handleIncomingMsgReadSucceeded(peer,id);

        }

        @Override
        public void onCorrupted(Peer peer, String msg, Exception e) {
            super.onCorrupted(peer, msg, e);
            totalConnectedPeers.remove(peer);
            detailedInfoPeers.remove(peer);

            callbackPeerDropped(peer);
        }


        @Override
        public void onDestroy(Peer peer) {
            super.onDestroy(peer);

            totalConnectedPeers.remove(peer);
            detailedInfoPeers.remove(peer);
            callbackPeerDropped(peer);
        }
    };



    private void handleIncomingMsgReadSucceeded(Peer peer,String id){

    }

    private void handleIncomingMsgChunk(Peer peer, String id, int chunkSize, int soFar, int available, byte[] bytes) {
        Logger.i(tag, "handle incoming msg chunk, peer: " + peer + ", id: " + id + ", chunkSize: " + chunkSize+", soFar: "+soFar+", available: "+available);
        MsgID msgID = MsgID.buildWithJsonString(id);
        Logger.i(tag, "incoming msg id: " + msgID);

        String type = msgID.getType();

        switch (type) {
            //delivery only one chunk, so handle it there, take it as the whole msg read completed
            case Constants.PeerMsgType.TYPE_API:

                break;
            case Constants.PeerMsgType.TYPE_FILE:

                break;

            case Constants.PeerMsgType.TYPE_STR:

                break;
        }
    }


    public ConnectedPeersHandler() {
        Peer.setGlobalPeerListener(peerListener);
    }

    public Set<Peer> getTotalConnectedPeers() {
        return totalConnectedPeers;
    }

    private void callbackPeerDropped(Peer peer) {

    }

    private void callbackIncomingNewPeer(Peer peer) {

    }

    private void callbackPeerDetailInfoGotFailed(Peer peer, String msg, Exception e) {

    }

    private void callbackPeerDetailInfoGot(Peer peer) {
    }


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
                    ClientNode node = new ClientNode(ip4, port, new ClientListenerWrapper(new ClientListenerAdapter() {
                        @Override
                        public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
                            super.onClientCreateFailed(clientNode, msg, exception);
                            clientNodeSet.remove(clientNode);
                        }
                    }, true));
                    node.create(Constants.Others.TIMEOUT);
                    clientNodeSet.add(node);
                }
            }
        }
    }
}
