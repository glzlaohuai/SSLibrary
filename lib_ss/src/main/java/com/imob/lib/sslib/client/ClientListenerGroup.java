package com.imob.lib.sslib.client;

import com.imob.lib.sslib.peer.Peer;

import java.util.HashSet;
import java.util.Set;

public class ClientListenerGroup implements ClientListener {

    private Set<ClientListener> set = new HashSet<>();

    public void add(ClientListener listener) {
        if (listener != null) {
            set.add(listener);
        }
    }

    public void remove(ClientListener listener) {
        if (listener != null) {
            set.remove(listener);
        }
    }


    public void clear() {
        set.clear();
    }


    @Override
    public void onClientDestroyed(ClientNode clientNode) {
        for (ClientListener listener : set) {
            listener.onClientDestroyed(clientNode);
        }
    }

    @Override
    public void onClientCreated(ClientNode clientNode) {
        for (ClientListener listener : set) {
            listener.onClientCreated(clientNode);
        }
    }

    @Override
    public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
        for (ClientListener listener : set) {
            listener.onClientCreateFailed(clientNode, msg, exception);
        }
    }

    @Override
    public void onMsgIntoQueue(Peer peer, String id) {
        for (ClientListener listener : set) {
            listener.onMsgIntoQueue(peer, id);
        }
    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : set) {
            listener.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {
        for (ClientListener listener : set) {
            listener.onMsgSendStart(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : set) {
            listener.onConfirmMsgSendStart(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {
        for (ClientListener listener : set) {
            listener.onMsgSendSucceeded(peer, id);
        }
    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : set) {
            listener.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
        for (ClientListener listener : set) {
            listener.onMsgSendFailed(peer, id, msg, exception);
        }
    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
        for (ClientListener listener : set) {
            listener.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        }
    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
        for (ClientListener listener : set) {
            listener.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }
    }

    @Override
    public void onIOStreamOpened(Peer peer) {
        for (ClientListener listener : set) {
            listener.onIOStreamOpened(peer);
        }
    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
        for (ClientListener listener : set) {
            listener.onIOStreamOpenFailed(peer, errorMsg, exception);
        }
    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {
        for (ClientListener listener : set) {
            listener.onCorrupted(peer, msg, e);
        }
    }

    @Override
    public void onDestroy(Peer peer) {
        for (ClientListener listener : set) {
            listener.onDestroy(peer);
        }

    }

    @Override
    public void onTimeoutOccured(Peer peer) {
        for (ClientListener listener : set) {
            listener.onTimeoutOccured(peer);
        }
    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {
        for (ClientListener listener : set) {
            listener.onIncomingMsg(peer, id, available);
        }
    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
        for (ClientListener listener : set) {
            listener.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
        }
    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
        for (ClientListener listener : set) {
            listener.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {
        for (ClientListener listener : set) {
            listener.onIncomingMsgReadSucceeded(peer, id);
        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
        for (ClientListener listener : set) {
            listener.onIncomingMsgReadFailed(peer, id, total, soFar);
        }
    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : set) {
            listener.onIncomingConfirmMsg(peer, id, soFar, total);
        }
    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
        for (ClientListener listener : set) {
            listener.onConfirmMsgSendPending(peer, id, soFar, total);
        }
    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {
        for (ClientListener listener : set) {
            listener.onMsgSendPending(peer, id);
        }
    }
}
