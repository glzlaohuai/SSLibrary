package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.api.request.APIRequester;
import com.badzzz.pasteany.core.api.response.APIResponserManager;
import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.client.ClientListenerAdapter;
import com.imob.lib.sslib.client.ClientListenerWrapper;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerGroup;

import java.io.File;
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


    private PeerListener peerNameRetrieveListeenr = new PeerListenerAdapter() {
        @Override
        public void onIOStreamOpened(Peer peer) {
            totalConnectedPeers.add(peer);
            callbackIncomingNewPeer(peer);

            //send a api msg to peer to got its detail info
            APIRequester.requestAPI(peer, Constants.PeerMsgAPI.PEER_DETAILS, new APIRequester.APIRequestListener() {
                @Override
                public void start(Peer peer, String api) {

                }

                @Override
                public void response(Peer peer, String msg) {

                }

                @Override
                public void error(Peer peer, String msg, Exception e) {

                }

                @Override
                public void after(Peer peer) {

                }
            });
        }


        @Override
        public void onDestroy(Peer peer) {
            totalConnectedPeers.remove(peer);
            detailedInfoPeers.remove(peer);

            callbackPeerDropped(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            if (!detailedInfoPeers.contains(peer)) {
                peer.setTag(MsgID.buildWithJsonString(id).getDevice());
                detailedInfoPeers.add(peer);

                callbackPeerDetailInfoGot(peer);
            }
        }
    };

    private PeerListener mainGlobalPeerListener = new PeerListenerAdapter() {

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
            super.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
            handleIncomingMsgChunk(peer, id, chunkSize, soFar, available, chunkBytes);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            super.onIncomingMsgReadSucceeded(peer, id);
            handleIncomingMsgReadSucceeded(peer, id);
        }


        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            super.onIncomingMsgReadFailed(peer, id, total, soFar);
            handleIncomingMsgReadFailed(peer, id);
        }


    };

    private PeerListenerGroup globalListener = new PeerListenerGroup();

    private void handleIncomingMsgReadSucceeded(final Peer peer, final String id) {
        Logger.i(tag, "handle incoming msg read succeeded, id: " + id);
        MsgID msgID = MsgID.buildWithJsonString(id);
        String type = msgID.getType();
        switch (type) {
            //only handle file type msg here
            case Constants.PeerMsgType.TYPE_FILE:
                PlatformManagerHolder.get().getAppManager().getFileManager().mergeAllFileChunks(PeerUtils.getDeviceIDFromPeer(peer), id, msgID.getData(), new IFileManager.FileMergeListener() {
                    @Override
                    public void onSuccess(File finalFile) {
                        callbackFileMergeSucceeded(PeerUtils.getDeviceIDFromPeer(peer), id, finalFile);
                    }

                    @Override
                    public void onFailed() {
                        callbackFileMergeFailed(PeerUtils.getDeviceIDFromPeer(peer), id);
                    }
                });
                break;
        }
    }

    private void handleIncomingMsgReadFailed(Peer peer, String id) {
        Logger.i(tag, "handle incoming msg read failed, id: " + id);
        callbackMsgReadFailed(PeerUtils.getDeviceIDFromPeer(peer), id);
    }

    private void callbackMsgReadFailed(String deviceIDFromPeer, String id) {

    }

    private void callbackFileMergeFailed(String deviceID, String msgID) {

    }


    private void callbackFileMergeSucceeded(String deviceID, String msgID, File finalFile) {

    }


    private void callbackFileChunkSaved(String deviceID, String msgID, File chunkFile, int soFar, int chunkSize) {

    }


    private void callbackFileChunkSaveFailed(String deviceID, String msgID, int soFar, int chunkSize) {

    }


    private void handleIncomingMsgChunk(Peer peer, final String id, final int chunkSize, final int soFar, int available, byte[] bytes) {
        Logger.i(tag, "handle incoming msg chunk, peer: " + peer + ", id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", available: " + available);
        MsgID msgID = MsgID.buildWithJsonString(id);
        Logger.i(tag, "incoming msg id: " + msgID);

        String type = msgID.getType();

        switch (type) {
            //delivery only one chunk, so handle it there, take it as the whole msg read completed
            case Constants.PeerMsgType.TYPE_API_REQUEST:
                String api = msgID.getData();
                APIResponserManager.getResponser(api).response(peer, id);
                break;
            case Constants.PeerMsgType.TYPE_FILE:
                final String deviceID = PeerUtils.getDeviceIDFromPeer(peer);
                PlatformManagerHolder.get().getAppManager().getFileManager().saveFileChunk(deviceID, id, chunkSize, soFar, available, bytes, new IFileManager.FileChunkSaveListener() {
                    @Override
                    public void onSuccess(File chunkFile) {
                        callbackFileChunkSaved(deviceID, id, chunkFile, soFar, chunkSize);
                    }

                    @Override
                    public void onFailed() {
                        callbackFileChunkSaveFailed(deviceID, id, soFar, chunkSize);
                    }
                });
                break;
            //stringmsg only have one msgChunk
            case Constants.PeerMsgType.TYPE_STR:
                Logger.i(tag, "incoming msg: " + new String(bytes, 0, chunkSize));
                break;

            /**
             * do nothing here, this type of msg will be handled in {@link APIRequester#requestAPI(Peer, String, APIRequester.APIRequestListener)}
             */
            case Constants.PeerMsgType.TYPE_API_RESPONSE:
                break;
        }
    }


    public ConnectedPeersHandler() {
        globalListener.add(peerNameRetrieveListeenr);
        globalListener.add(mainGlobalPeerListener);

        Peer.setGlobalPeerListener(globalListener);
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
