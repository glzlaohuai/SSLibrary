package com.imob.lib.sslib.peer;

public class PeerListenerAdapter implements PeerListener {
    @Override
    public void onMsgIntoQueue(Peer peer, String id) {

    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {

    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {

    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {

    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {

    }

    @Override
    public void onIOStreamOpened(Peer peer) {

    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {

    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {

    }

    @Override
    public void onDestroy(Peer peer) {

    }

    @Override
    public void onTimeoutOccured(Peer peer) {

    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {

    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {

    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {

    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id, int available) {

    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {

    }

    @Override
    public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {

    }
}
