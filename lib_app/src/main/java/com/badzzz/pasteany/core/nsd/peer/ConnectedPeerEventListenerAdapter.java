package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsHandler;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerAdapter implements ConnectedPeerEventListener {
    @Override
    public void onIncomingPeer(ConnectedClientsHandler handler, Peer peer) {

    }

    @Override
    public void onPeerDropped(ConnectedClientsHandler handler, Peer peer) {

    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedClientsHandler handler, Peer peer) {

    }

    @Override
    public void onIncomingFileChunkSaved(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {

    }

    @Override
    public void onIncomingFileChunkSaveFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {

    }

    @Override
    public void onIncomingFileChunkMergeFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onIncomingFileChunkMerged(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {

    }

    @Override
    public void onIncomingStringMsg(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, String msg) {

    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID) {

    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total) {

    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total) {

    }
}
