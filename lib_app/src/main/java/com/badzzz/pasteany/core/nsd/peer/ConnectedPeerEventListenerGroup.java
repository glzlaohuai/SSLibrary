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
    public void onIncomingPeer(Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingPeer(peer);
        }
    }

    @Override
    public void onPeerLost(Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerLost(peer);
        }
    }


    @Override
    public void onIncomingFileChunkSaved(Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaved(peer, deviceID, msgID, soFar, chunkSize, file);
        }
    }

    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaveFailed(peer, deviceID, msgID, soFar, chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMergeFailed(peer, deviceID, msgID);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String deviceID, String msgID, File finalFile) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMerged(peer, deviceID, msgID, finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(Peer peer, String deviceID, String msgID, String msg) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingStringMsg(peer, deviceID, msgID, msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingMsgReadFailed(peer, deviceID, msgID);
        }
    }




}
