package com.badzzz.pasteany.core.nsd.peer;

import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ConnectedPeerEventListenerGroup implements ConnectedPeerEventListener {

    private Set<ConnectedPeerEventListener> set = new HashSet<>();

    public synchronized void add(ConnectedPeerEventListener listener) {
        if (listener != null) {
            set.add(listener);
        }
    }


    public synchronized void remove(ConnectedPeerEventListener listener) {
        if (listener != null) {
            set.remove(listener);
        }
    }


    public synchronized void clear() {
        set.clear();
    }


    @Override
    public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onIncomingPeer(handler, peer);
        }
    }

    @Override
    public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onPeerDropped(handler, peer);
        }
    }

    @Override
    public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onPeerDetailedInfoGot(handler, peer);
        }
    }

    @Override
    public void onFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onFileChunkSaved(handler, peer, deviceID, msgID, soFar, chunkSize, file);
        }
    }

    @Override
    public void onFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onFileChunkSaveFailed(handler, peer, deviceID, msgID, soFar, chunkSize);
        }
    }

    @Override
    public void onFileMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onFileMergeFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onFileMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onFileMerged(handler, peer, deviceID, msgID, finalFile);
        }
    }

    @Override
    public void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onIncomingStringMsg(handler, peer, deviceID, msgID, msg);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onIncomingMsgReadFailed(handler, peer, deviceID, msgID);
        }
    }

    @Override
    public void onFileChunkMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onFileChunkMsgSendConfirmed(handler, peer, id, soFar, total);
        }
    }

    @Override
    public void onStringMsgSendConfirmed(ConnectedPeersHandler handler, Peer peer, String id, int soFar, int total) {
        for (ConnectedPeerEventListener listener : set) {
            listener.onStringMsgSendConfirmed(handler, peer, id, soFar, total);
        }
    }
}
