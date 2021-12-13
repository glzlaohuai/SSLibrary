package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerWrapper implements ConnectedPeerEventListener {
    private ConnectedPeerEventListener base;
    private boolean printLog;

    private static final String TAG = "ConnectedPeerEventListenerWrapper";

    public ConnectedPeerEventListenerWrapper(ConnectedPeerEventListener base) {
        this(base, false);
    }

    public ConnectedPeerEventListenerWrapper(ConnectedPeerEventListener base, boolean printLog) {
        this.base = base;
        this.printLog = printLog;
    }


    @Override
    public void onPeerLost(Peer peer) {
        base.onPeerLost(peer);

        if (printLog) {
            Logger.i(TAG, "onPeerLost, peer: " + peer);
        }

    }

    @Override
    public void onIncomingPeer(Peer peer) {

        base.onIncomingPeer(peer);

        if (printLog) {
            Logger.i(TAG, "onIncomingPeer, peer: " + peer);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file) {
        base.onIncomingFileChunkSaved(peer, id, soFar, chunkSize, file);

        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaved, peer: " + peer + ", id: " + id + ", sofar: " + soFar + ", chunkSize: " + chunkSize + ", file: " + file);
        }
    }

    @Override
    public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {
        base.onIncomingFileChunk(peer, id, soFar, chunkSize, available, bytes);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunk, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", chunkSize: " + chunkSize + ", available: " + available + ", bytes: " + bytes);
        }
    }


    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize) {
        base.onIncomingFileChunkSaveFailed(peer, id, soFar, chunkSize);

        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaveFailed, peer: " + peer + ", id: " + id + ", sofar: " + soFar + ", chunkSize: " + chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String id) {
        base.onIncomingFileChunkMergeFailed(peer, id);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMergeFailed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String id, File finalFile) {
        base.onIncomingFileChunkMerged(peer, id, finalFile);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMerged, peer: " + peer + ", id: " + id + ", file: " + finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(Peer peer, String id, String msg) {

        base.onIncomingStringMsg(peer, id, msg);

        if (printLog) {
            Logger.i(TAG, "onIncomingStringMsg, peer: " + peer + ", id: " + id + ",msg: " + msg);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {

        base.onIncomingMsgReadSucceeded(peer, id);

        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {
        base.onIncomingMsgReadFailed(peer, id, soFar, total);

        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadFailed, peer: " + peer + ", id: " + id + ", total: " + total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id) {
        base.onMsgSendFailed(peer, id);

        if (printLog) {
            Logger.i(TAG, "onMsgSendFailed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onNotAllMsgChunkSendedConfirmed(Peer peer, String id) {

        base.onNotAllMsgChunkSendedConfirmed(peer, id);

        if (printLog) {
            Logger.i(TAG, "onNotAllMsgChunkSendedConfirmed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total) {
        base.onSendedMsgChunkConfirmed(peer, id, soFar, total);

        if (printLog) {
            Logger.i(TAG, "onSendedMsgChunkConfirmed, peer: " + peer + ", id: " + id + ", SoFar: " + soFar + ", total: " + total);
        }
    }

}
