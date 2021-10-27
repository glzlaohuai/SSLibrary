package com.imob.lib.sslib.peer;

public interface PeerListener {
    void onMsgIntoQueue(Peer peer, String id);

    void onMsgSendStart(Peer peer, String id);

    void onMsgSendSucceeded(Peer peer, String id);

    void onMsgSendFailed(Peer peer, String id, String msg, Exception exception);

    void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize);

    void onIOStreamOpened(Peer peer);

    void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception);

    void onCorrupted(Peer peer, String msg, Exception e);

    void onDestroy(Peer peer);

    void onIncomingMsg(Peer peer, String id, int available);

    void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id);

    void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar);

    void onIncomingMsgReadSucceeded(Peer peer, String id);

    void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar);


}
