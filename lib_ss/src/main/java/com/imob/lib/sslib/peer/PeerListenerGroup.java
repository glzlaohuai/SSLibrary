package com.imob.lib.sslib.peer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeerListenerGroup implements PeerListener {

    private Queue<PeerListener> queue = new ConcurrentLinkedQueue<>();

    public void add(PeerListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void remove(PeerListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }

    public void clear() {
        queue.clear();
    }

    @Override
    public void onMsgIntoQueue(Peer peer, String id) {
        for (PeerListener listener : queue) {
            listener.onMsgIntoQueue(peer, id);
        }
    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
        for (PeerListener listener : queue) {
            listener.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }

    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {
        for (PeerListener listener : queue) {
            listener.onMsgSendStart(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
        for (PeerListener listener : queue) {
            listener.onConfirmMsgSendStart(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {
        for (PeerListener listener : queue) {
            listener.onMsgSendSucceeded(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
        for (PeerListener listener : queue) {
            listener.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
        for (PeerListener listener : queue) {
            listener.onMsgSendFailed(peer, id, msg, exception);
        }
    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
        for (PeerListener listener : queue) {
            listener.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        }
    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
        for (PeerListener listener : queue) {
            listener.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }
    }

    @Override
    public void onIOStreamOpened(Peer peer) {
        for (PeerListener listener : queue) {
            listener.onIOStreamOpened(peer);
        }
    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
        for (PeerListener listener : queue) {
            listener.onIOStreamOpenFailed(peer, errorMsg, exception);
        }
    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {
        for (PeerListener listener : queue) {
            listener.onCorrupted(peer, msg, e);
        }
    }

    @Override
    public void onDestroy(Peer peer) {
        for (PeerListener listener : queue) {
            listener.onDestroy(peer);
        }
    }

    @Override
    public void onTimeoutOccured(Peer peer) {
        for (PeerListener listener : queue) {
            listener.onTimeoutOccured(peer);
        }
    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {
        for (PeerListener listener : queue) {
            listener.onIncomingMsg(peer, id, available);
        }
    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
        for (PeerListener listener : queue) {
            listener.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
        }
    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
        for (PeerListener listener : queue) {
            listener.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id, int available) {
        for (PeerListener listener : queue) {
            listener.onIncomingMsgReadSucceeded(peer, id, available);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
        for (PeerListener listener : queue) {
            listener.onIncomingMsgReadFailed(peer, id, total, soFar);
        }
    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
        for (PeerListener listener : queue) {
            listener.onIncomingConfirmMsg(peer, id, total, soFar);
        }
    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
        for (PeerListener listener : queue) {
            listener.onConfirmMsgSendPending(peer, id, total, soFar);
        }
    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {
        for (PeerListener listener : queue) {
            listener.onMsgSendPending(peer, id);
        }
    }

    @Override
    public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {
        for (PeerListener listener : queue) {
            listener.onSomeMsgChunkSendSucceededButNotConfirmedByPeer(peer, msgID);
        }
    }
}
