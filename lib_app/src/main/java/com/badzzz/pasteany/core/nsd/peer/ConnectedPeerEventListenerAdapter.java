package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerAdapter implements ConnectedPeerEventListener {
    @Override
    public void onPeerLost(Peer peer) {

    }

    @Override
    public void onIncomingPeer(Peer peer) {

    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file) {

    }

    @Override
    public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {

    }

    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize) {

    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String id) {

    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String id, File finalFile) {

    }

    @Override
    public void onIncomingStringMsg(Peer peer, String id, String msg) {

    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {

    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {

    }

    @Override
    public void onMsgSendFailed(Peer peer, String id) {

    }

    @Override
    public void onNotAllMsgChunkSendedConfirmed(Peer peer, String id) {

    }

    @Override
    public void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total) {

    }
}
