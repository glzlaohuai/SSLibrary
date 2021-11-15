package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;

public class ServerManager {

    private static ServerNode serverNode;

    private static final String TAG = "ServerManager";

    /**
     * @return true - create a new server node instance, false - has a running server node already, create failed
     */
    public synchronized static boolean createServerNode(ServerListener serverListener, PeerListener peerListener, long timeout) {
        if (serverNode != null && serverNode.isInUsing()) {
            return false;
        } else {
            serverNode = new ServerNode(new ServerListenerWrapper(serverListener), new PeerListenerWrapper(peerListener));
            return serverNode.create(timeout);
        }
    }


    /**
     * @param id
     * @param msg
     * @return true - serverNode is not null and {@link ServerNode#broadcastStringMsg(String, String)} return true | false - the opposite
     */
    public static boolean broadcastStringMsg(String id, String msg) {
        if (getManagedServerNode() != null) {
            return getManagedServerNode().broadcastStringMsg(id, msg);
        } else {
            return false;
        }
    }

    /**
     * @param id
     * @param filePath
     * @return true - serverNode is not null and {@link ServerNode#broadcastFileMsg(String, String)}} return true | false - the opposite
     */
    public static boolean broadcastFileMsg(String id, String filePath) {
        if (getManagedServerNode() != null) {
            return getManagedServerNode().broadcastFileMsg(id, filePath);
        } else {
            return false;
        }
    }


    private synchronized static void nullServerNodeAfterDestroyCallback() {
        serverNode = null;
    }

    public static synchronized void destroyServer() {
        if (getManagedServerNode() != null) {
            getManagedServerNode().destroy();
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
            Logger.i(TAG, "onMsgIntoQueue, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgIntoQueue(peer, id);
        }

        @Override
        public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgIntoQueue, peer: " + peer.getTag() + ",id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendStart(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendStart, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgSendStart(peer, id);
        }

        @Override
        public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendStart, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendStart(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgSendSucceeded(peer, id);
        }

        @Override
        public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            Logger.i(TAG, "onMsgSendFailed: peer: " + peer.getTag() + ", id: " + id + ", msg: " + msg + ", exception: " + exception);
            base.onMsgSendFailed(peer, id, msg, exception);
        }

        @Override
        public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
            Logger.i(TAG, "onConfirmMsgSendFailed, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total + ", msg: " + msg + ", exception: " + exception);
            base.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        }

        @Override
        public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
            Logger.i(TAG, "onMsgChunkSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize);
            base.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }

        @Override
        public void onIOStreamOpened(Peer peer) {
            Logger.i(TAG, "onIOStreamOpened, peer: " + peer.getTag());
            base.onIOStreamOpened(peer);
        }

        @Override
        public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
            Logger.i(TAG, "onIOStreamOpenFailed, peer: " + peer.getTag() + ", msg: " + errorMsg + ", exception: " + exception);
            base.onIOStreamOpenFailed(peer, errorMsg, exception);

        }

        @Override
        public void onCorrupted(Peer peer, String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, peer: " + peer.getTag() + ", msg: " + msg + ", exception: " + e);
            base.onCorrupted(peer, msg, e);
        }

        @Override
        public void onDestroy(Peer peer) {
            Logger.i(TAG, "onDestroy， peer：" + peer.getTag());
            base.onDestroy(peer);
        }

        @Override
        public void onTimeoutOccured(Peer peer) {
            Logger.i(TAG, "onTimeoutOccured, peer: " + peer.getTag());

            base.onTimeoutOccured(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            Logger.i(TAG, "onIncomingMsg, peer: " + peer.getTag() + ", id: " + id + ", available: " + available);

            base.onIncomingMsg(peer, id, available);
        }

        @Override
        public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailed, peer:" + peer.getTag() + ",id: " + id + ", errorMsg: " + errorMsg);
            base.onIncomingMsgChunkReadFailed(peer, id, errorMsg);

        }

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", chunkBytes: " + chunkBytes);

            base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, peer: " + peer.getTag() + ", id: " + id);
            base.onIncomingMsgReadSucceeded(peer, id);
        }

        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            Logger.i(TAG, "onIncomingMsgReadFailed, peer: " + peer.getTag() + ", id: " + id + ", total: " + total + ", soFar: " + soFar);
            base.onIncomingMsgReadFailed(peer, id, total, soFar);
        }

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onIncomingConfirmMsg, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onIncomingConfirmMsg(peer, id, soFar, total);
        }

        @Override
        public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendPending, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendPending(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendPending(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendPending, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgSendPending(peer, id);
        }
    }

    public synchronized static ServerNode getManagedServerNode() {
        return serverNode;
    }

}
