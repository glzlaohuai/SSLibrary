package com.badzzz.pasteany.core.nsd.peer;

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
import java.util.LinkedList;
import java.util.List;

import javax.jmdns.ServiceEvent;

public class ConnectedPeersHandler {

    private static final String S_TAG = "ConnectedPeersHandler";

    private boolean destroyed = false;

    private List<Peer> totalConnectedPeers = new LinkedList<>();
    private List<Peer> detailedInfoPeers = new LinkedList<>();
    private List<ClientNode> clientNodeSet = new LinkedList<>();

    private String tag = S_TAG + " # " + hashCode();

    private static ConnectedPeerEventListener eventListener;

    public static void setEventListener(ConnectedPeerEventListener eventListener) {
        ConnectedPeersHandler.eventListener = eventListener;
    }

    public interface ConnectedPeerEventListener {
        void onIncomingPeer(ConnectedPeersHandler handler, Peer peer);

        void onPeerDropped(ConnectedPeersHandler handler, Peer peer);

        void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer);

        void onFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file);

        void onFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize);

        void onFileMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID);

        void onFileMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile);

        void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg);

        void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID);

        void onFileChunkMsgSendConfirmed(Peer peer, String id, int soFar, int total);

        void onStringMsgSendConfirmed(Peer peer, String id, int soFar, int total);
    }


    private PeerListener peerNameRetrieveListeenr = new PeerListenerAdapter() {
        @Override
        public void onIOStreamOpened(Peer peer) {
            Logger.i(tag, "incoming a new peer, send msg to retrieve its detail device info.");
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

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            super.onIncomingConfirmMsg(peer, id, soFar, total);


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


    private void callbackFileChunkSendConfirmed(Peer peer, String id, int soFar, int total) {
        eventListener.onFileChunkMsgSendConfirmed(peer, id, soFar, total);
    }


    private void callbackStringMsgSendConfirmed(Peer peer, String id, int soFar, int total) {
        eventListener.onStringMsgSendConfirmed(peer, id, soFar, total);
    }


    private void handleIncomingMsgReadFailed(Peer peer, String id) {
        Logger.i(tag, "handle incoming msg read failed, id: " + id);
        callbackMsgReadFailed(peer, PeerUtils.getDeviceIDFromPeer(peer), id);
    }

    private void callbackMsgReadFailed(Peer peer, String deviceID, String id) {
        eventListener.onIncomingMsgReadFailed(this, peer, deviceID, id);
    }

    private void callbackFileMergeFailed(Peer peer, String deviceID, String msgID) {
        eventListener.onFileMergeFailed(this, peer, deviceID, msgID);
    }


    private void callbackFileMergeSucceeded(Peer peer, String deviceID, String msgID, File finalFile) {
        eventListener.onFileMerged(this, peer, deviceID, msgID, finalFile);
    }


    private void callbackFileChunkSaved(Peer peer, String deviceID, String msgID, File chunkFile, int soFar, int chunkSize) {
        eventListener.onFileChunkSaved(this, peer, deviceID, msgID, soFar, chunkSize, chunkFile);
    }


    private void callbackFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        eventListener.onFileChunkSaveFailed(this, peer, deviceID, msgID, soFar, chunkSize);
    }

    private void callbackPeerDropped(Peer peer) {
        eventListener.onPeerDropped(this, peer);


        if (PeerUtils.getDeviceIDFromPeer(peer) != null) {
            String deviceID = PeerUtils.getDeviceIDFromPeer(peer);

            // TODO: 2021/11/25 triggerServiceInfo
            if (ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler() == this) {
                NsdNode nsdNode = ConnectedPeersManager.getInUsingServiceHandler().getNsdNode();
                if (nsdNode != null) {
                    Logger.i(tag, "try to retrieve losed peer's info");
                    nsdNode.triggerServiceInfoResolve(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(deviceID, PreferenceManagerWrapper.getInstance().getServiceName()));
                }
            }
        }
    }

    private void callbackIncomingNewPeer(Peer peer) {
        eventListener.onIncomingPeer(this, peer);
    }

    private void callbackPeerDetailInfoGot(Peer peer) {
        eventListener.onPeerDetailedInfoGot(this, peer);
    }

    private void callbackStringMsgReaded(Peer peer, String deviceIDFromPeer, String id, String content) {
        eventListener.onIncomingStringMsg(this, peer, deviceIDFromPeer, id, content);
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
        }
    }


    public ConnectedPeersHandler() {
        globalListener.add(peerNameRetrieveListeenr);
        globalListener.add(mainGlobalPeerListener);

        Peer.setGlobalPeerListener(globalListener);
    }

    public List<Peer> getTotalConnectedPeers() {
        return totalConnectedPeers;
    }

    public List<Peer> getDetailedInfoPeers() {
        return detailedInfoPeers;
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
        //        Peer.setGlobalPeerListener(null);
    }

    public synchronized void afterServiceDiscoveryed(ServiceEvent event) {
        if (event != null) {

            String deviceID = null;
            String serviceName = null;

            try {
                JSONObject serviceJsonObject = new JSONObject(event.getName());
                deviceID = serviceJsonObject.getString(Constants.Device.KEY_DEVICEID);
                serviceName = serviceJsonObject.getString(Constants.NSD.Key.SERVICE_NAME);
            } catch (JSONException e) {
                Logger.e(e);
            }

            if (deviceID != null && !deviceID.equals(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID()) && serviceName != null && serviceName.equals(PreferenceManagerWrapper.getInstance().getServiceName())) {
                Logger.i(tag, "discoveryed nsd service's name match, and not the one created from localhost, so connect to it.");
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
                    } else {
                        Logger.i(tag, "there already has a peer connected to the nsd service, so no need to connect to it again.");
                    }
                }
            } else {
                Logger.i(tag, "discoveryed nsd service's name mismatch or it's a localhost nsd service, ignore it.");
            }
        }
    }
}
