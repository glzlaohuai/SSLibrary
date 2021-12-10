package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsHandler;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public interface ConnectedPeerEventListener {
    void onIncomingPeer(ConnectedClientsHandler handler, Peer peer);

    void onPeerDropped(ConnectedClientsHandler handler, Peer peer);

    void onPeerDetailedInfoGot(ConnectedClientsHandler handler, Peer peer);

    void onIncomingFileChunkSaved(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file);

    void onIncomingFileChunkSaveFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize);

    void onIncomingFileChunkMergeFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID);

    void onIncomingFileChunkMerged(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, File finalFile);

    void onIncomingStringMsg(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, String msg);

    void onIncomingMsgReadFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID);

    void onFileChunkMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total);

    void onStringMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total);
}
