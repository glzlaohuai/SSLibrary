package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.api.request.APIRequester;
import com.badzzz.pasteany.core.api.response.APIResponserManager;
import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.client.ClientListenerAdapter;
import com.imob.lib.sslib.client.ClientListenerWrapper;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.ServiceInfo;

public class ConnectedPeersHandler {

    private static final String S_TAG = "ConnectedPeersHandler";

    private boolean destroyed = false;

    private Map<String, Set<Peer>> detailedInfoPeers = new ConcurrentHashMap<>();
    private List<ClientNode> clientNodeList = new LinkedList<>();

    private Map<String, Integer> clientConnectRetryMap = new HashMap<>();


    private String tag = S_TAG + " # " + hashCode();

    private static ConnectedPeerEventListenerGroup listenerGroup = new ConnectedPeerEventListenerGroup();

    public static void monitorConnectedPeersEvents(ConnectedPeerEventListener eventListener) {
        ConnectedPeersHandler.listenerGroup.add(eventListener);
    }


    public static void unmonitorConnectedPeersEvents(ConnectedPeerEventListener eventListener) {
        ConnectedPeersHandler.listenerGroup.remove(eventListener);
    }


    private PeerListener peerNameRetrieveListeenr = new PeerListenerAdapter() {
        @Override
        public void onIOStreamOpened(Peer peer) {
            afterPeerIncoming(peer);
        }


        @Override
        public void onDestroy(Peer peer) {
            afterPeerDropped(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            afterIncomingPeerDetail(id, peer);
        }


        private synchronized void afterPeerDropped(Peer peer) {
            removePeerFromMap(peer);
            callbackPeerDropped(peer);
        }


        private boolean isPeerInMap(Peer peer) {
            if (peer == null || peer.getTag() == null) return false;
            Set<String> keys = detailedInfoPeers.keySet();
            for (String key : keys) {
                Set<Peer> peers = detailedInfoPeers.get(key);
                if (peers != null && peers.contains(peer)) {
                    return true;
                }
            }
            return false;
        }


        private void addPeerToMap(Peer peer) {
            if (peer == null || peer.getTag() == null) return;

            String tag = peer.getTag();
            Set<Peer> peers = detailedInfoPeers.get(tag);
            if (peers == null) {
                peers = new HashSet<>();
                detailedInfoPeers.put(tag, peers);
            }

            peers.add(peer);
        }


        private void removePeerFromMap(Peer peer) {
            if (peer != null && peer.getTag() != null) {
                Set<Peer> peers = detailedInfoPeers.get(peer.getTag());
                if (peers != null) {
                    peers.remove(peer);
                }
            }

        }


        private synchronized void afterIncomingPeerDetail(String msgID, Peer peer) {
            if (!isPeerInMap(peer)) {
                peer.setTag(MsgID.buildWithJsonString(msgID).getDevice());
                destroyStalePeerByTagBeforeAddNewIncomingDetailedPeer(peer);
                addPeerToMap(peer);
                callbackPeerDetailInfoGot(peer);
            }
        }


        private synchronized void afterPeerIncoming(Peer peer) {
            Logger.i(tag, "incoming a new peer, send msg to retrieve its detail device info.");
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
    };

    private void destroyStalePeerByTagBeforeAddNewIncomingDetailedPeer(Peer incomingPeer) {
        if (incomingPeer == null || incomingPeer.getTag() == null || !incomingPeer.getLocalNode().isServerNode())
            return;

        Set<Peer> sameTagPeers = detailedInfoPeers.get(incomingPeer.getTag());
        if (sameTagPeers == null) {
            return;
        }

        for (Peer peer : sameTagPeers) {
            if (peer == incomingPeer) continue;
            if (peer.getTag().equals(incomingPeer.getTag()) && peer.getLocalNode().isServerNode()) {
                Logger.i(tag, "peer's connection must has already lost, but not be detected by system yet, destroy it immediatelly: " + peer.toString());
                peer.destroy();
            }
        }
    }


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

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            super.onIncomingConfirmMsg(peer, id, soFar, total);
            handleIncomingConfirmMsg(peer, id, soFar, total);

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
                PlatformManagerHolder.get().getAppManager().getFileManager().mergeAllFileChunks(PeerUtils.getDeviceIDFromPeer(peer), id, new File(msgID.getData()).getName(), new IFileManager.FileMergeListener() {
                    @Override
                    public void onSuccess(File finalFile) {
                        callbackFileMergeSucceeded(peer, PeerUtils.getDeviceIDFromPeer(peer), id, finalFile);
                    }

                    @Override
                    public void onFailed() {
                        callbackFileMergeFailed(peer, PeerUtils.getDeviceIDFromPeer(peer), id);
                    }
                });
                break;
        }
    }


    private void handleIncomingConfirmMsg(Peer peer, String id, int sofar, int total) {
        Logger.i(tag, "handle incoming confirm msg, id: " + id + ", soFar: " + sofar + ", total: " + total);
        MsgID msgID = MsgID.buildWithJsonString(id);

        switch (msgID.getType()) {
            case Constants.PeerMsgType.TYPE_FILE:
                callbackFileChunkSendConfirmed(peer, id, sofar, total);
                break;
            case Constants.PeerMsgType.TYPE_STR:
                //soFar must be always equals to total
                callbackStringMsgSendConfirmed(peer, id, sofar, total);
                break;
            //just do nothing
            default:
                break;
        }

    }


    private void handleIncomingMsgReadFailed(Peer peer, String id) {
        Logger.i(tag, "handle incoming msg read failed, id: " + id);
        callbackMsgReadFailed(peer, PeerUtils.getDeviceIDFromPeer(peer), id);
    }


    private void callbackFileChunkSendConfirmed(Peer peer, String id, int soFar, int total) {
        listenerGroup.onFileChunkMsgSendConfirmed(this, peer, id, soFar, total);
    }


    private void callbackStringMsgSendConfirmed(Peer peer, String id, int soFar, int total) {
        listenerGroup.onStringMsgSendConfirmed(this, peer, id, soFar, total);
    }

    private void callbackMsgReadFailed(Peer peer, String deviceID, String id) {
        listenerGroup.onIncomingMsgReadFailed(this, peer, deviceID, id);
    }

    private void callbackFileMergeFailed(Peer peer, String deviceID, String msgID) {
        listenerGroup.onIncomingFileChunkMergeFailed(this, peer, deviceID, msgID);
    }


    private void callbackFileMergeSucceeded(Peer peer, String deviceID, String msgID, File finalFile) {
        listenerGroup.onIncomingFileChunkMerged(this, peer, deviceID, msgID, finalFile);
    }


    private void callbackFileChunkSaved(Peer peer, String deviceID, String msgID, File chunkFile, int soFar, int chunkSize) {
        listenerGroup.onIncomingFileChunkSaved(this, peer, deviceID, msgID, soFar, chunkSize, chunkFile);
    }


    private void callbackFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        listenerGroup.onIncomingFileChunkSaveFailed(this, peer, deviceID, msgID, soFar, chunkSize);
    }

    private void callbackPeerDropped(Peer peer) {
        listenerGroup.onPeerDropped(this, peer);


        if (PeerUtils.getDeviceIDFromPeer(peer) != null) {
            String deviceID = PeerUtils.getDeviceIDFromPeer(peer);

            // TODO: 2021/11/25 triggerServiceInfo
            if (ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler() == this) {
                NsdNode nsdNode = ConnectedPeersManager.getInUsingServiceHandler().getNsdNode();
                if (nsdNode != null) {
                    Logger.i(tag, "try to retrieve lost peer's info");
                    nsdNode.triggerServiceInfoResolve(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(deviceID, PreferenceManagerWrapper.getInstance().getServiceName()));
                }
            }
        }
    }

    private void callbackIncomingNewPeer(Peer peer) {
        listenerGroup.onIncomingPeer(this, peer);
    }

    private void callbackPeerDetailInfoGot(Peer peer) {
        listenerGroup.onPeerDetailedInfoGot(this, peer);
    }

    private void callbackStringMsgReaded(Peer peer, String deviceIDFromPeer, String id, String content) {
        listenerGroup.onIncomingStringMsg(this, peer, deviceIDFromPeer, id, content);
    }

    private void handleIncomingMsgChunk(final Peer peer, final String id, final int chunkSize, final int soFar, int available, byte[] bytes) {
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
                        callbackFileChunkSaved(peer, deviceID, id, chunkFile, soFar, chunkSize);
                    }

                    @Override
                    public void onFailed() {
                        callbackFileChunkSaveFailed(peer, deviceID, id, soFar, chunkSize);
                    }
                });
                break;
            //stringmsg only have one msgChunk
            case Constants.PeerMsgType.TYPE_STR:
                callbackStringMsgReaded(peer, PeerUtils.getDeviceIDFromPeer(peer), id, new String(bytes, 0, chunkSize));
                break;

            /**
             * do nothing here, this type of msg will be handled in {@link APIRequester#requestAPI(Peer, String, APIRequester.APIRequestListener)}
             */
            case Constants.PeerMsgType.TYPE_API_RESPONSE:
                break;

            case Constants.PeerMsgType.TYPE_PING:
                Logger.i(tag, "incoming a ping msg, ignore it.");
                break;
        }
    }


    public ConnectedPeersHandler() {
        globalListener.add(peerNameRetrieveListeenr);
        globalListener.add(mainGlobalPeerListener);

        Peer.setGlobalPeerListener(globalListener);
    }


    public Map<String, Set<Peer>> getDetailedInfoPeers() {
        return detailedInfoPeers;
    }

    public synchronized void destroy() {
        if (!destroyed) {
            destroyed = true;
            doDestroy();
        }
    }


    private ClientNode getClientNodeReferToThisIP(String ip) {
        if (ip == null || ip.isEmpty()) return null;
        for (ClientNode clientNode : clientNodeList) {
            if (clientNode.getIp() != null && clientNode.getIp().equals(ip)) {
                return clientNode;
            }
        }
        return null;
    }

    private synchronized void doDestroy() {
        for (ClientNode clientNode : clientNodeList) {
            clientNode.destroy();
        }
        clientNodeList.clear();
    }


    public synchronized void afterServiceDiscoveryed(final ServiceInfo info) {
        if (info != null) {

            String deviceID = null;
            String serviceName = null;

            try {
                JSONObject serviceJsonObject = new JSONObject(info.getName());
                deviceID = serviceJsonObject.getString(Constants.Device.KEY_DEVICEID);
                serviceName = serviceJsonObject.getString(Constants.NSD.Key.SERVICE_NAME);
            } catch (JSONException e) {
                Logger.e(e);
            }

            if (deviceID != null && !deviceID.equals(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID()) && serviceName != null && serviceName.equals(PreferenceManagerWrapper.getInstance().getServiceName())) {
                Logger.i(tag, "discoveryed nsd service's name match, and not the one created from localhost, so connect to it.");
                Inet4Address inetAddresses = info.getInet4Address();
                int port = info.getPort();

                if (inetAddresses != null) {
                    String ip4 = inetAddresses.getHostAddress();
                    final ClientNode referedClientNode = getClientNodeReferToThisIP(ip4);

                    if (ip4 != null && referedClientNode == null) {
                        final String finalServiceName = serviceName;


                        ClientNode node = new ClientNode(ip4, port, new ClientListenerWrapper(new ClientListenerAdapter() {
                            @Override
                            public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
                                super.onClientCreateFailed(clientNode, msg, exception);
                                clientNodeList.remove(clientNode);
                                //connect to server failed, so send another service info retrieve msg
                                if (!destroyed && ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler() == ConnectedPeersHandler.this) {
                                    Logger.i(tag, "create client failed, maybe the server info changed? do another retrieve service info loop and if retrieved, try to reconnect to it again.");
                                    NsdNode nsdNode = ConnectedPeersManager.getInUsingServiceHandler().getNsdNode();
                                    if (nsdNode != null) {
                                        nsdNode.triggerServiceInfoResolve(info.getType(), info.getName());
                                    }
                                }
                            }

                            @Override
                            public void onClientDestroyed(ClientNode clientNode) {
                                super.onClientDestroyed(clientNode);
                                afterClientDestroyed(clientNode);
                            }

                            private void afterClientDestroyed(ClientNode clientNode) {
                                clientNodeList.remove(clientNode);
                                //after be destroyed, reset its retry number
                                clientConnectRetryMap.put(info.getName(), 0);
                            }

                            @Override
                            public void onDestroy(Peer peer) {
                                super.onDestroy(peer);
                                afterClientDestroyed((ClientNode) peer.getLocalNode());
                            }
                        }, true));
                        node.create(Constants.Others.TIMEOUT);
                        clientNodeList.add(node);
                    } else {
                        Logger.i(tag, "there already has a peer connected to the nsd service, so no need to connect to it again, just send a ping msg to check if it's still available.");
                        referedClientNode.sendMsg(MsgCreator.createPingMsg());
                    }
                }
            } else {
                Logger.i(tag, "discoveryed nsd service's name mismatch or it's a localhost nsd service, ignore it.");
            }
        }
    }
}
