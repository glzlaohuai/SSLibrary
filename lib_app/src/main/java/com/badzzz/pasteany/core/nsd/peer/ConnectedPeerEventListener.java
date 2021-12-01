package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public interface ConnectedPeerEventListener {
    void onIncomingPeer(ConnectedPeersHandler handler, Peer peer);

    void onPeerDropped(ConnectedPeersHandler handler, Peer peer);

    void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer);

    void onIncomingFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file);

    void onIncomingFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize);

    void onIncomingFileChunkMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID);

    void onIncomingFileChunkMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile);

    void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg);

    void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID);

    void onFileChunkMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total);

    void onStringMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total);
}
