package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.utils.Logger;

public class ServerManager {
    private static ServerNode serverNode;
    private static final String TAG = "ServerManager";

    /**
     *
     * @return true - create a new server node instance, false - has a running server node already, create failed
     */
    public static boolean createServerNode(ServerListener serverListener, PeerListener peerListener) {
        if (serverNode != null && serverNode.isInUsing()) {
            return false;
        } else {
            serverNode = new ServerNode(new ServerListenerWrapper(serverListener), new PeerListenerWrapper(peerListener));
            return serverNode.create();
        }
    }

    private static class ServerListenerWrapper implements ServerListener {

        private ServerListener base;

        public ServerListenerWrapper(ServerListener base) {
            this.base = base;
        }

        @Override
        public void onCreated() {
            Logger.i(TAG, "onCreated");

            base.onCreated();
        }

        @Override
        public void onCreateFailed(Exception exception) {
            Logger.i(TAG, "onCreateFailed, " + exception);

            base.onCreateFailed(exception);
        }

        @Override
        public void onDestroyed() {
            Logger.i(TAG, "onDestroyed");

            base.onDestroyed();
        }

        @Override
        public void onCorrupted(String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, msg: " + msg + ", exception: " + e);

            base.onCorrupted(msg, e);
        }
    }

    private static class PeerListenerWrapper implements PeerListener {
        private PeerListener base;

        public PeerListenerWrapper(PeerListener base) {
            this.base = base;
        }

        @Override
        public void onMsgIntoQueue(Peer peer, String id) {
            Logger.i(TAG, "onMsgIntoQueue: " + id);
            base.onMsgIntoQueue(peer, id);
        }

        @Override
        public void onMsgSendStart(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendStart: " + id);
            base.onMsgSendStart(peer, id);
        }

        @Override
        public void onMsgSendSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendSucceeded: " + id);
            base.onMsgSendSucceeded(peer, id);
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            Logger.i(TAG, "onMsgSendFailed: " + id + ", msg: " + msg + ", exception: " + exception);
            base.onMsgSendFailed(peer, id, msg, exception);
        }

        @Override
        public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
            Logger.i(TAG, "onMsgChunkSendSucceeded: " + id + ", chunkSize: " + chunkSize);
            base.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }

        @Override
        public void onIOStreamOpened(Peer peer) {
            Logger.i(TAG, "onIOStreamOpened");
            base.onIOStreamOpened(peer);
        }

        @Override
        public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
            Logger.i(TAG, "onIOStreamOpenFailed, msg: " + errorMsg + ", exception: " + exception);
            base.onIOStreamOpenFailed(peer, errorMsg, exception);

        }

        @Override
        public void onCorrupted(Peer peer, String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, msg: " + msg + ", exception: " + e);
            base.onCorrupted(peer, msg, e);
        }

        @Override
        public void onDestroy(Peer peer) {
            Logger.i(TAG, "onDestroy");
            base.onDestroy(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            Logger.i(TAG, "onIncomingMsg, id: " + id + ", available: " + available);

            base.onIncomingMsg(peer, id, available);
        }

        @Override
        public void onIncomingMsgChunkReadFailed(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailed, id: " + id);
            base.onIncomingMsgChunkReadFailed(peer, id);

        }

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar);
            base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, id: " + id);

            base.onIncomingMsgReadSucceeded(peer, id);
        }

        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            Logger.i(TAG, "onIncomingMsgReadFailed, id: " + id + ", total: " + total + ", soFar: " + soFar);
            base.onIncomingMsgReadFailed(peer, id, total, soFar);
        }
    }

    public static ServerNode getManagedServerNode() {
        return serverNode;
    }
}
