package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerAdapter implements ConnectedPeerEventListener {
    @Override
    public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {

    }

    @Override
    public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {

    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {

    }

    @Override
    public void onIncomingFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {

    }

    @Override
    public void onIncomingFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {

    }

    @Override
    public void onIncomingFileChunkMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onIncomingFileChunkMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {

    }

    @Override
    public void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg) {

    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total) {

    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total) {

    }
}
