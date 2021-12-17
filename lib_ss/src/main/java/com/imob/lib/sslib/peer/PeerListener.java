package com.imob.lib.sslib.peer;

public interface PeerListener {
    void onMsgIntoQueue(Peer peer, String id);

    void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total);

    void onMsgSendStart(Peer peer, String id);

    void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total);

    void onMsgSendSucceeded(Peer peer, String id);

    void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total);

    void onMsgSendFailed(Peer peer, String id, String msg, Exception exception);

    void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception);

    void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize);

    void onIOStreamOpened(Peer peer);

    void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception);

    void onCorrupted(Peer peer, String msg, Exception e);

    void onDestroy(Peer peer);

    void onTimeoutOccured(Peer peer);

    void onIncomingMsg(Peer peer, String id, int available);

    void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg);

    void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes);

    void onIncomingMsgReadSucceeded(Peer peer, String id, int total);

    void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar);

    void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total);

    void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total);

    void onMsgSendPending(Peer peer, String id);

    void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID);
}
