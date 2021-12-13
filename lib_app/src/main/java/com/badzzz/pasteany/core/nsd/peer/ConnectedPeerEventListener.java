package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public interface ConnectedPeerEventListener {

    void onPeerLost(Peer peer);

    void onIncomingPeer(Peer peer);

    void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file);

    void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes);

    void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize);

    void onIncomingFileChunkMergeFailed(Peer peer, String id);

    void onIncomingFileChunkMerged(Peer peer, String id, File finalFile);

    void onIncomingStringMsg(Peer peer, String id, String msg);

    void onIncomingMsgReadSucceeded(Peer peer, String id);

    void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total);

    void onMsgSendFailed(Peer peer, String id);

    void onMsgSendStarted(Peer peer, String id);

    void onNotAllMsgChunkSendedConfirmed(Peer peer, String id);

    void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total);

}
