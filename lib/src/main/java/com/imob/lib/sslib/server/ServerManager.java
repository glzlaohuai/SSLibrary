package com.imob.lib.sslib.server;

import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.utils.Logger;

import java.util.Arrays;

public class ServerManager {

    private static ServerNode serverNode;


    private static final String TAG = "ServerManager";

    /**
     *
     * @return true - create a new server node instance, false - has a running server node already, create failed
     */
    public synchronized static boolean createServerNode(ServerListener serverListener, PeerListener peerListener) {
        if (serverNode != null && serverNode.isInUsing()) {
            return false;
        } else {
            serverNode = new ServerNode(new ServerListenerWrapper(serverListener), new PeerListenerWrapper(peerListener));
            return serverNode.create();
        }
    }


    private synchronized static void nullServerNodeAfterDestroyCallback() {
        serverNode = null;
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
            ServerManager.nullServerNodeAfterDestroyCallback();
        }

        @Override
        public void onCorrupted(String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, msg: " + msg + ", exception: " + e);

            base.onCorrupted(msg, e);
            ServerManager.nullServerNodeAfterDestroyCallback();
        }

        @Override
        public void onIncomingClient(Peer peer) {
            Logger.i(TAG, "onIncomingClient: " + peer.getSocket());

            base.onIncomingClient(peer);
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
        public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgIntoQueue: id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendStart(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendStart: " + id);
            base.onMsgSendStart(peer, id);
        }

        @Override
        public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendStart: id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendStart(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendSucceeded: " + id);
            base.onMsgSendSucceeded(peer, id);
        }

        @Override
        public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendSucceeded: id: " + id + ", soFar: " + soFar + ", total: " + total);

            base.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            Logger.i(TAG, "onMsgSendFailed: " + id + ", msg: " + msg + ", exception: " + exception);
            base.onMsgSendFailed(peer, id, msg, exception);
        }

        @Override
        public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
            Logger.i(TAG, "onConfirmMsgSendFailed: " + id + ", soFar: " + soFar + ", total: " + total + ", msg: " + msg + ", exception: " + exception);
            base.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
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
        public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailedDueToPeerIOFailed, id: " + id);
            base.onIncomingMsgChunkReadFailedDueToPeerIOFailed(peer, id);

        }

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", chunkBytes: " + Arrays.toString(Arrays.copyOfRange(chunkBytes, 0, chunkSize)));

            base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
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

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onIncomingConfirmMsg, id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onIncomingConfirmMsg(peer, id, soFar, total);
        }
    }

    public static ServerNode getManagedServerNode() {
        return serverNode;
    }
}
