package com.imob.lib.sslib.client;

import com.imob.lib.sslib.peer.Peer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientListenerGroup implements ClientListener {

    private Queue<ClientListener> queue = new ConcurrentLinkedQueue<>();

    public void add(ClientListener listener) {
        if (listener != null && !queue.contains(listener)) {
            queue.add(listener);
        }
    }

    public void remove(ClientListener listener) {
        if (listener != null) {
            queue.remove(listener);
        }
    }


    public void clear() {
        queue.clear();
    }


    @Override
    public void onClientDestroyed(ClientNode clientNode) {
        for (ClientListener listener : queue) {
            listener.onClientDestroyed(clientNode);
        }
    }

    @Override
    public void onClientCreated(ClientNode clientNode) {
        for (ClientListener listener : queue) {
            listener.onClientCreated(clientNode);
        }
    }

    @Override
    public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
        for (ClientListener listener : queue) {
            listener.onClientCreateFailed(clientNode, msg, exception);
        }
    }

    @Override
    public void onMsgIntoQueue(Peer peer, String id) {
        for (ClientListener listener : queue) {
            listener.onMsgIntoQueue(peer, id);
        }
    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : queue) {
            listener.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {
        for (ClientListener listener : queue) {
            listener.onMsgSendStart(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : queue) {
            listener.onConfirmMsgSendStart(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {
        for (ClientListener listener : queue) {
            listener.onMsgSendSucceeded(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : queue) {
            listener.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
        for (ClientListener listener : queue) {
            listener.onMsgSendFailed(peer, id, msg, exception);
        }
    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
        for (ClientListener listener : queue) {
            listener.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        }
    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
        for (ClientListener listener : queue) {
            listener.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }
    }

    @Override
    public void onIOStreamOpened(Peer peer) {
        for (ClientListener listener : queue) {
            listener.onIOStreamOpened(peer);
        }
    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
        for (ClientListener listener : queue) {
            listener.onIOStreamOpenFailed(peer, errorMsg, exception);
        }
    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {
        for (ClientListener listener : queue) {
            listener.onCorrupted(peer, msg, e);
        }
    }

    @Override
    public void onDestroy(Peer peer) {
        for (ClientListener listener : queue) {
            listener.onDestroy(peer);
        }

    }

    @Override
    public void onTimeoutOccured(Peer peer) {
        for (ClientListener listener : queue) {
            listener.onTimeoutOccured(peer);
        }
    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {
        for (ClientListener listener : queue) {
            listener.onIncomingMsg(peer, id, available);
        }
    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
        for (ClientListener listener : queue) {
            listener.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
        }
    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
        for (ClientListener listener : queue) {
            listener.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id,int available) {
        for (ClientListener listener : queue) {
            listener.onIncomingMsgReadSucceeded(peer, id,available);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
        for (ClientListener listener : queue) {
            listener.onIncomingMsgReadFailed(peer, id, total, soFar);
        }
    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : queue) {
            listener.onIncomingConfirmMsg(peer, id, soFar, total);
        }
    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : queue) {
            listener.onConfirmMsgSendPending(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {
        for (ClientListener listener : queue) {
            listener.onMsgSendPending(peer, id);
        }
    }

    @Override
    public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {
        for (ClientListener listener : queue) {
            listener.onSomeMsgChunkSendSucceededButNotConfirmedByPeer(peer, msgID);
        }
    }
}
