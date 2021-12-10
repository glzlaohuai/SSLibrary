package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsHandler;
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
    public void onIncomingPeer(ConnectedClientsHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingPeer(handler, peer);
        }
    }

    @Override
    public void onPeerDropped(ConnectedClientsHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerDropped(handler, peer);
        }
    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedClientsHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onPeerDetailedInfoGot(handler, peer);
        }
    }

    @Override
    public void onIncomingFileChunkSaved(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaved(handler, peer, deviceID, msgID, soFar, chunkSize, file);
        }
    }

    @Override
    public void onIncomingFileChunkSaveFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkSaveFailed(handler, peer, deviceID, msgID, soFar, chunkSize);
        }
    }

    @Override
    public void onIncomingFileChunkMergeFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMergeFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onIncomingFileChunkMerged(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingFileChunkMerged(handler, peer, deviceID, msgID, finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID, String msg) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingStringMsg(handler, peer, deviceID, msgID, msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedClientsHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onIncomingMsgReadFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onFileChunkMsgSendConfirmed(handler, peer, msgID, soFar, total);
        }
    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedClientsHandler handler, Peer peer, String msgID, int soFar, int total) {
        for (ConnectedPeerEventListener listener : queue) {
            listener.onStringMsgSendConfirmed(handler, peer, msgID, soFar, total);
        }
    }
}
