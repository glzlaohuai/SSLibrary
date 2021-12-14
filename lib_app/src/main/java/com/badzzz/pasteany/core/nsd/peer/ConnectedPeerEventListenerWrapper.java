package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class ConnectedPeerEventListenerWrapper implements ConnectedPeerEventListener {
    private ConnectedPeerEventListener base;
    private boolean printLog;

    private static final String S_TAG = "ConnectedPeerEventListenerWrapper";
    private String tag = S_TAG + " # " + hashCode();

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
            Logger.i(tag, "onPeerLost, peer: " + peer);
        }

    }

    @Override
    public void onIncomingPeer(Peer peer) {

        base.onIncomingPeer(peer);

        if (printLog) {
            Logger.i(tag, "onIncomingPeer, peer: " + peer);
        }
    }

    @Override
    public void onIncomingMsgChunk(Peer peer, String id, int soFar, int chunkSize, int available) {
        base.onIncomingMsgChunk(peer, id, soFar, chunkSize, available);
        if (printLog) {
            Logger.i(tag, "onIncomingMsgChunk, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", chunkSize: " + chunkSize + ", available: " + available);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file) {
        base.onIncomingFileChunkSaved(peer, id, soFar, chunkSize, file);

        if (printLog) {
            Logger.i(tag, "onIncomingFileChunkSaved, peer: " + peer + ", id: " + id + ", sofar: " + soFar + ", chunkSize: " + chunkSize + ", file: " + file);
        }
    }

    @Override
    public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {
        base.onIncomingFileChunk(peer, id, soFar, chunkSize, available, bytes);
        if (printLog) {
            Logger.i(tag, "onIncomingFileChunk, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", chunkSize: " + chunkSize + ", available: " + available + ", bytes: " + bytes);
        }
    }


    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize) {
        base.onIncomingFileChunkSaveFailed(peer, id, soFar, chunkSize);

        if (printLog) {
            Logger.i(tag, "onIncomingFileChunkSaveFailed, peer: " + peer + ", id: " + id + ", sofar: " + soFar + ", chunkSize: " + chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String id) {
        base.onIncomingFileChunkMergeFailed(peer, id);
        if (printLog) {
            Logger.i(tag, "onIncomingFileChunkMergeFailed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String id, File finalFile) {
        base.onIncomingFileChunkMerged(peer, id, finalFile);
        if (printLog) {
            Logger.i(tag, "onIncomingFileChunkMerged, peer: " + peer + ", id: " + id + ", file: " + finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(Peer peer, String id, String msg) {

        base.onIncomingStringMsg(peer, id, msg);

        if (printLog) {
            Logger.i(tag, "onIncomingStringMsg, peer: " + peer + ", id: " + id + ",msg: " + msg);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {

        base.onIncomingMsgReadSucceeded(peer, id);

        if (printLog) {
            Logger.i(tag, "onIncomingMsgReadSucceeded, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {
        base.onIncomingMsgReadFailed(peer, id, soFar, total);

        if (printLog) {
            Logger.i(tag, "onIncomingMsgReadFailed, peer: " + peer + ", id: " + id + ", total: " + total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id) {
        base.onMsgSendFailed(peer, id);

        if (printLog) {
            Logger.i(tag, "onMsgSendFailed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onMsgSendStarted(Peer peer, String id) {
        base.onMsgSendStarted(peer, id);
        if (printLog) {
            Logger.i(tag, "onMsgSendStarted, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onNotAllMsgChunkSendedConfirmed(Peer peer, String id) {

        base.onNotAllMsgChunkSendedConfirmed(peer, id);

        if (printLog) {
            Logger.i(tag, "onNotAllMsgChunkSendedConfirmed, peer: " + peer + ", id: " + id);
        }
    }

    @Override
    public void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total) {
        base.onSendedMsgChunkConfirmed(peer, id, soFar, total);

        if (printLog) {
            Logger.i(tag, "onSendedMsgChunkConfirmed, peer: " + peer + ", id: " + id + ", SoFar: " + soFar + ", total: " + total);
        }
    }

}
