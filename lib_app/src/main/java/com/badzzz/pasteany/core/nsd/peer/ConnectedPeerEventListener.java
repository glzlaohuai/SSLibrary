package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public interface ConnectedPeerEventListener {

    void onPeerLost(Peer peer);

    void onIncomingPeer(Peer peer);

    void onIncomingFileChunkSaved(Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file);

    void onIncomingFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize);

    void onIncomingFileChunkMergeFailed(Peer peer, String deviceID, String msgID);

    void onIncomingFileChunkMerged(Peer peer, String deviceID, String msgID, File finalFile);

    void onIncomingStringMsg(Peer peer, String deviceID, String msgID, String msg);

    void onIncomingMsgReadSucceeded(Peer peer, String msgID);

    void onIncomingMsgReadFailed(Peer peer, String msgID, int soFar, int total);

    void onMsgSendFailed(Peer peer, String msgID);

    void onNotAllMsgChunkSendedConfirmed(Peer peer, String msgID);

    void onSendedMsgChunkConfirmed(Peer peer, String msgID, int soFar, int total);

}
