package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.api.response.APIResponserManager;
import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.nsd.NsdServiceHandler;
import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerGroup;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectedPeersManager {

    private static final String TAG = "ConnectedPeersManager";
    private static boolean inited = false;

    private final static Byte peerMapLock = 0x0;
    private final static Map<String, Set<Peer>> connectedPeersMap = new ConcurrentHashMap<>();

    private static ConnectedPeerEventListenerGroup connectedPeerEventListenerGroup = new ConnectedPeerEventListenerGroup();
    private static ConnectedPeerEventListenerGroup globalConnectedPeerEventListener = new ConnectedPeerEventListenerGroup();
    private static ConnectedPeerEventListenerGroup monitoredListeners = new ConnectedPeerEventListenerGroup();


    private final static PeerListenerGroup globalPeerListener = new PeerListenerGroup();
    private final static PeerListener peerMapManagerListener = new PeerListenerAdapter() {
        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            super.onIncomingMsg(peer, id, available);
            afterPeerDetailGot(peer, id);
        }

        @Override
        public void onIOStreamOpened(Peer peer) {
            super.onIOStreamOpened(peer);
            afterPeerIncoming(peer);
        }

        @Override
        public void onDestroy(Peer peer) {
            super.onDestroy(peer);

            afterPeerDestroyed(peer);
        }
    };


    private final static boolean isThisMsgTypeNeedCallback(String id) {
        if (id == null || MsgID.buildWithJsonString(id) == null) {
            return false;
        } else {
            MsgID msgID = MsgID.buildWithJsonString(id);
            return msgID.getType().equals(Constants.PeerMsgType.TYPE_FILE) || msgID.getType().equals(Constants.PeerMsgType.TYPE_STR);
        }
    }


    private final static PeerListener peerMsgManagerListener = new PeerListenerAdapter() {


        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
            super.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);

            //处理api、file、string类型的msg
            handleIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
        }

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            super.onIncomingConfirmMsg(peer, id, soFar, total);

            if (isThisMsgTypeNeedCallback(id)) {
                connectedPeerEventListenerGroup.onSendedMsgChunkConfirmed(peer, id, soFar, total);
            }
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            super.onMsgSendFailed(peer, id, msg, exception);
            connectedPeerEventListenerGroup.onMsgSendFailed(peer, id);
        }

        @Override
        public void onIncomingMsgReadSucceeded(final Peer peer, final String id) {
            super.onIncomingMsgReadSucceeded(peer, id);

            if (isThisMsgTypeNeedCallback(id)) {
                connectedPeerEventListenerGroup.onIncomingMsgReadSucceeded(peer, id);
                //if it's fileMsg, the merge all received file chunk into the completed final file, and callback the merge result
                MsgID msgID = MsgID.buildWithJsonString(id);
                if (msgID.getType().equals(Constants.PeerMsgType.TYPE_FILE)) {
                    PlatformManagerHolder.get().getAppManager().getFileManager().mergeAllFileChunks(PeerUtils.getDeviceIDFromPeer(peer), id, new File(msgID.getData()).getName(), new IFileManager.FileMergeListener() {
                        @Override
                        public void onSuccess(File finalFile) {
                            connectedPeerEventListenerGroup.onIncomingFileChunkMerged(peer, id, finalFile);
                        }

                        @Override
                        public void onFailed() {
                            connectedPeerEventListenerGroup.onIncomingFileChunkMergeFailed(peer, id);
                        }
                    });
                }
            }
        }

        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            super.onIncomingMsgReadFailed(peer, id, total, soFar);
            connectedPeerEventListenerGroup.onIncomingMsgReadFailed(peer, id, soFar, total);
        }

        @Override
        public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {
            super.onSomeMsgChunkSendSucceededButNotConfirmedByPeer(peer, msgID);

            connectedPeerEventListenerGroup.onNotAllMsgChunkSendedConfirmed(peer, msgID);
        }

    };


    private static void callbackFileChunkSaved(Peer peer, String msgID, File chunkFile, int soFar, int chunkSize) {
        connectedPeerEventListenerGroup.onIncomingFileChunkSaved(peer, msgID, soFar, chunkSize, chunkFile);
    }

    private static void callbackFileChunkSaveFailed(Peer peer, String msgID, int soFar, int chunkSize) {
        connectedPeerEventListenerGroup.onIncomingFileChunkSaveFailed(peer, msgID, soFar, chunkSize);
    }

    private static void callbackIncomingFileChunk(Peer peer, String msgID, int chunkSize, int soFar, int available, byte[] chunkBytes) {
        connectedPeerEventListenerGroup.onIncomingFileChunk(peer, msgID, soFar, chunkSize, available, chunkBytes);
    }


    private static void callbackIncomingStringMsg(Peer peer, String msgID, String msg) {
        connectedPeerEventListenerGroup.onIncomingStringMsg(peer, msgID, msg);
    }


    private final static void handleIncomingMsgChunkReadSucceeded(final Peer peer, final String id, final int chunkSize, final int soFar, int available, byte[] chunkBytes) {
        Logger.i(TAG, "handle incoming msg chunk, peer: " + peer + ", id: " + id + ", chunkSize: " + chunkSize + ", sofar: " + soFar + ", available: " + available);
        MsgID msgID = MsgID.buildWithJsonString(id);
        Logger.i(TAG, "incoming msg id: " + msgID);

        String type = msgID.getType();

        switch (type) {
            case Constants.PeerMsgType.TYPE_API_REQUEST:
                String api = msgID.getData();
                APIResponserManager.getResponser(api).response(peer, id);
                break;

            //file
            case Constants.PeerMsgType.TYPE_FILE:
                final String deviceID = PeerUtils.getDeviceIDFromPeer(peer);

                callbackIncomingFileChunk(peer, id, chunkSize, soFar, available, chunkBytes);
                PlatformManagerHolder.get().getAppManager().getFileManager().saveFileChunk(deviceID, id, chunkSize, soFar, available, chunkBytes, new IFileManager.FileChunkSaveListener() {
                    @Override
                    public void onSuccess(File chunkFile) {
                        callbackFileChunkSaved(peer, id, chunkFile, soFar, chunkSize);
                    }

                    @Override
                    public void onFailed() {
                        callbackFileChunkSaveFailed(peer, id, soFar, chunkSize);
                    }
                });
                break;
            //string type msg always only have one msg chunk, so one msg chunk means msg read completed
            case Constants.PeerMsgType.TYPE_STR:
                callbackIncomingStringMsg(peer, id, new String(chunkBytes, 0, chunkSize));
                break;

            case Constants.PeerMsgType.TYPE_PING:
            case Constants.PeerMsgType.TYPE_API_RESPONSE:
                Logger.i(TAG, "incoming ping/api_response msg, just drop it and do nothing.");
                break;
        }
    }


    private final static void afterPeerIncoming(Peer peer) {
        Logger.i(TAG, "incoming a new peer, send a ping msg to retrieve its detail device info.");
        peer.sendMessage(MsgCreator.createPingMsg("ping_get_peer_detail"));
    }

    private static boolean isPeerInMap(Peer peer) {
        if (peer == null || peer.getTag() == null) return false;
        synchronized (peerMapLock) {
            Set<String> keys = connectedPeersMap.keySet();
            for (String key : keys) {
                Set<Peer> peers = connectedPeersMap.get(key);
                if (peers != null && peers.contains(peer)) {
                    return true;
                }
            }
        }

        return false;
    }


    private static void destroyStalePeersIfExistsAfterIncomingNewPeer(Peer incomingPeer) {
        if (incomingPeer == null || incomingPeer.getTag() == null || !incomingPeer.getLocalNode().isServerNode())
            return;

        synchronized (peerMapLock) {
            Set<Peer> sameTagPeersSet = connectedPeersMap.get(incomingPeer.getTag());
            if (sameTagPeersSet == null) {
                return;
            }

            for (Peer peer : sameTagPeersSet) {
                if (peer == incomingPeer) continue;
                if (peer.getTag().equals(incomingPeer.getTag()) && peer.getLocalNode().isServerNode()) {
                    Logger.i(TAG, "peer's connection must has already lost, but not be detected by system yet, destroy it immediatelly: " + peer.toString());
                    peer.destroy();
                }
            }
        }

    }

    private static void addPeerToMap(Peer peer) {
        if (peer == null || peer.getTag() == null) return;
        synchronized (peerMapLock) {
            String tag = peer.getTag();
            Set<Peer> peersSet = connectedPeersMap.get(tag);
            if (peersSet == null) {
                peersSet = new HashSet<>();
                connectedPeersMap.put(tag, peersSet);
            }
            peersSet.add(peer);
        }
    }

    private final static void afterPeerDetailGot(Peer peer, String id) {
        if (!isPeerInMap(peer)) {
            MsgID msgID = MsgID.buildWithJsonString(id);
            if (msgID != null && msgID.getDevice() != null && !msgID.getDevice().isEmpty()) {
                peer.setTag(msgID.getDevice());
                destroyStalePeersIfExistsAfterIncomingNewPeer(peer);
                addPeerToMap(peer);
                connectedPeerEventListenerGroup.onIncomingPeer(peer);
            }
        }
    }


    private static void removePeerFromMap(Peer peer) {
        if (peer != null && peer.getTag() != null) {
            synchronized (peerMapLock) {
                Set<Peer> peers = connectedPeersMap.get(peer.getTag());
                if (peers != null) {
                    peers.remove(peer);
                }
            }
        }
    }

    public static Map<String, Set<Peer>> getConnectedPeers() {
        synchronized (peerMapLock) {
            return connectedPeersMap;
        }
    }

    private final static void afterPeerDestroyed(Peer peer) {
        removePeerFromMap(peer);
        callbackPeerLost(peer);
        triggerReconnectActionAfterPeerDropped(peer);
    }

    private static void callbackPeerLost(Peer peer) {
        connectedPeerEventListenerGroup.onPeerLost(peer);
    }


    private static void triggerReconnectActionAfterPeerDropped(Peer peer) {
        if (peer != null && PeerUtils.getDeviceIDFromPeer(peer) != null) {
            String deviceID = PeerUtils.getDeviceIDFromPeer(peer);
            NsdServiceHandler nsdServiceHandler = ConnectedClientsManager.getInUsingServiceHandler();

            if (nsdServiceHandler != null) {
                NsdNode nsdNode = nsdServiceHandler.getNsdNode();
                if (nsdNode != null) {
                    Logger.i(TAG, "try to retrieve lost peer's info");
                    nsdNode.triggerServiceInfoResolve(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(deviceID, PreferenceManagerWrapper.getInstance().getServiceName()));
                }
            }
        }
    }


    public static void init() {
        Logger.i(TAG, "init");
        if (!inited) {
            synchronized (ConnectedPeersManager.class) {
                if (!inited) {
                    inited = true;
                    doInit();
                }
            }
        }
    }


    private static void doInit() {
        Logger.i(TAG, "do init");
        globalPeerListener.add(peerMapManagerListener);
        globalPeerListener.add(peerMsgManagerListener);

        Peer.setGlobalPeerListener(globalPeerListener);

        connectedPeerEventListenerGroup.add(globalConnectedPeerEventListener);
        connectedPeerEventListenerGroup.add(monitoredListeners);
    }


    public static void monitorConnectedPeersEvent(ConnectedPeerEventListener listener) {
        monitoredListeners.add(listener);
    }


    public static void unmonitorConnectedPeersEvent(ConnectedPeerEventListener listener) {
        monitoredListeners.remove(listener);
    }


    public static void setGlobalConnectedPeerEventListener(ConnectedPeerEventListener listener) {
        globalConnectedPeerEventListener.clear();
        globalConnectedPeerEventListener.add(listener);
    }


}
