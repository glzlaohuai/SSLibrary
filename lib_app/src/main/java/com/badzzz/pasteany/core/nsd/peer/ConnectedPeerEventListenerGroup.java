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
    public void onPeerLost(Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerLost(peer);
        }
    }

    @Override
    public void onIncomingPeer(Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingPeer(peer);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaved(peer, id, soFar, chunkSize, file);
        }
    }

    @Override
    public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunk(peer, id, soFar, chunkSize, available, bytes);
        }
    }


    @Override
    public void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaveFailed(peer, id, soFar, chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(Peer peer, String id) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMergeFailed(peer, id);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(Peer peer, String id, File finalFile) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMerged(peer, id, finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(Peer peer, String id, String msg) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingStringMsg(peer, id, msg);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingMsgReadSucceeded(peer, id);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingMsgReadFailed(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onMsgSendFailed(peer, id);
        }
    }

    @Override
    public void onNotAllMsgChunkSendedConfirmed(Peer peer, String id) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onNotAllMsgChunkSendedConfirmed(peer, id);
        }
    }

    @Override
    public void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onSendedMsgChunkConfirmed(peer, id, soFar, total);
        }
    }
}
