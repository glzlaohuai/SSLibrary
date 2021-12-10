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
    public void onIncomingPeer(Peer peer) {
        base.onIncomingPeer(peer);
        if (printLog) {
            Logger.i(TAG, "onIncomingPeer, peer: " + peer);
        }
    }

    @Override
    public void onPeerLost(Peer peer) {

        base.onPeerLost(peer);
        if (printLog) {
            Logger.i(TAG, "onPeerDropped, peer: " + peer);
        }
    }

    @Override
    public void onPeerDetailedInfoGot(Peer peer) {
        base.onPeerDetailedInfoGot(peer);
        if (printLog) {
            Logger.i(TAG, "onPeerDetailedInfoGot, peer: " + peer);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        base.onIncomingFileChunkSaved(peer, deviceID, msgID, soFar, chunkSize, file);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaved, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", soFar: " + soFar + ", chunkSize: " + chunkSize + ", file: " + file);
        }

    }

    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        base.onIncomingFileChunkSaveFailed(peer, deviceID, msgID, soFar, chunkSize);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaveFailed, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", soFar: " + soFar + ", chunkSize: " + chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String deviceID, String msgID) {
        base.onIncomingFileChunkMergeFailed(peer, deviceID, msgID);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMergeFailed, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String deviceID, String msgID, File finalFile) {
        base.onIncomingFileChunkMerged(peer, deviceID, msgID, finalFile);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMerged, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", finalFile: " + finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(Peer peer, String deviceID, String msgID, String msg) {
        base.onIncomingStringMsg(peer, deviceID, msgID, msg);
        if (printLog) {
            Logger.i(TAG, "onIncomingStringMsg, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", msg: " + msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String deviceID, String msgID) {
        base.onIncomingMsgReadFailed(peer, deviceID, msgID);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadFailed, peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID);
        }
    }

    @Override
    public void onFileChunkMsgSendConfirmed(Peer peer, String msgID, int soFar, int total) {
        base.onFileChunkMsgSendConfirmed(peer, msgID, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onFileChunkMsgSendConfirmed, peer: " + peer + ", msgID: " + msgID + ", soFar: " + soFar + ", total: " + total);
        }
    }

    @Override
    public void onStringMsgSendConfirmed(Peer peer, String msgID, int soFar, int total) {
        base.onStringMsgSendConfirmed(peer, msgID, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onStringMsgSendConfirmed, peer: " + peer + ", msgID: " + msgID + ", soFar: " + soFar + ", total: " + total);
        }
    }
}
