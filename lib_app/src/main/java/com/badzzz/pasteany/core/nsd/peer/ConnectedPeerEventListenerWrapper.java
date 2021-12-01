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
    public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
        base.onIncomingPeer(handler, peer);
        if (printLog) {
            Logger.i(TAG, "onIncomingPeer, handler: " + handler + ", peer: " + peer);
        }
    }

    @Override
    public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {

        base.onPeerDropped(handler, peer);
        if (printLog) {
            Logger.i(TAG, "onPeerDropped, handler: " + handler + ", peer: " + peer);
        }
    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {
        base.onPeerDetailedInfoGot(handler, peer);
        if (printLog) {
            Logger.i(TAG, "onPeerDetailedInfoGot, handler: " + handler + ", peer: " + peer);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        base.onIncomingFileChunkSaved(handler, peer, deviceID, msgID, soFar, chunkSize, file);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaved, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", soFar: " + soFar + ", chunkSize: " + chunkSize + ", file: " + file);
        }

    }

    @Override
    public void onIncomingFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        base.onIncomingFileChunkSaveFailed(handler, peer, deviceID, msgID, soFar, chunkSize);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkSaveFailed, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", soFar: " + soFar + ", chunkSize: " + chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        base.onIncomingFileChunkMergeFailed(handler, peer, deviceID, msgID);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMergeFailed, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {
        base.onIncomingFileChunkMerged(handler, peer, deviceID, msgID, finalFile);
        if (printLog) {
            Logger.i(TAG, "onIncomingFileChunkMerged, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", finalFile: " + finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg) {
        base.onIncomingStringMsg(handler, peer, deviceID, msgID, msg);
        if (printLog) {
            Logger.i(TAG, "onIncomingStringMsg, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID + ", msg: " + msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        base.onIncomingMsgReadFailed(handler, peer, deviceID, msgID);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadFailed, handler: " + handler + ", peer: " + peer + ", deviceID: " + deviceID + ", msgID: " + msgID);
        }
    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total) {
        base.onFileChunkMsgSendConfirmed(handler, peer, msgID, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onFileChunkMsgSendConfirmed, handler: " + handler + ", peer: " + peer + ", msgID: " + msgID + ", soFar: " + soFar + ", total: " + total);
        }
    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String msgID, int soFar, int total) {
        base.onStringMsgSendConfirmed(handler, peer, msgID, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onStringMsgSendConfirmed, handler: " + handler + ", peer: " + peer + ", msgID: " + msgID + ", soFar: " + soFar + ", total: " + total);
        }
    }
}
