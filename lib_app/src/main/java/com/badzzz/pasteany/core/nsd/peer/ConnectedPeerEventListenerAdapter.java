package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerAdapter implements ConnectedPeerEventListener {
    @Override
    public void onIncomingPeer(Peer peer) {

    }

    @Override
    public void onPeerLost(Peer peer) {

    }

    @Override
    public void onPeerDetailedInfoGot(Peer peer) {

    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {

    }

    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {

    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String deviceID, String msgID, File finalFile) {

    }

    @Override
    public void onIncomingStringMsg(Peer peer, String deviceID, String msgID, String msg) {

    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onFileChunkMsgSendConfirmed(Peer peer, String msgID, int soFar, int total) {

    }

    @Override
    public void onStringMsgSendConfirmed(Peer peer, String msgID, int soFar, int total) {

    }
}
