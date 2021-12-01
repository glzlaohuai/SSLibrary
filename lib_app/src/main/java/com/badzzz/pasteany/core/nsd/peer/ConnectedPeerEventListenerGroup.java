package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectedPeerEventListenerGroup implements ConnectedPeerEventListener {

    private Queue<ConnectedPeerEventListener> queue = new ConcurrentLinkedQueue<>();

    public synchronized void add(ConnectedPeerEventListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }


    public synchronized void remove(ConnectedPeerEventListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }


    public synchronized void clear() {
        queue.clear();
    }


    @Override
    public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingPeer(handler, peer);
        }
    }

    @Override
    public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerDropped(handler, peer);
        }
    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerDetailedInfoGot(handler, peer);
        }
    }

    @Override
    public void onFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileChunkSaved(handler, peer, deviceID, msgID, soFar, chunkSize, file);
        }
    }

    @Override
    public void onFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileChunkSaveFailed(handler, peer, deviceID, msgID, soFar, chunkSize);
        }
    }

    @Override
    public void onFileMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileMergeFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onFileMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileMerged(handler, peer, deviceID, msgID, finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingStringMsg(handler, peer, deviceID, msgID, msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingMsgReadFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileChunkMsgSendConfirmed(handler, peer, id, soFar, total);
        }
    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onStringMsgSendConfirmed(handler, peer, id, soFar, total);
        }
    }
}
