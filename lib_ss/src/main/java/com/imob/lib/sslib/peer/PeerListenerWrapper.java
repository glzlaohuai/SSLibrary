package com.imob.lib.sslib.peer;

import com.imob.lib.lib_common.Logger;

public class PeerListenerWrapper implements PeerListener {
    private static final String TAG = "PeerListenerWrapper";
    private PeerListener base;
    private boolean printLog;

    public PeerListenerWrapper(PeerListener base) {
        this(base, true);
    }


    public PeerListenerWrapper(PeerListener base, boolean printLog) {
        this.base = base;
        this.printLog = printLog;
    }

    @Override
    public void onMsgIntoQueue(Peer peer, String id) {

        base.onMsgIntoQueue(peer, id);
        if (printLog) {
            Logger.i(TAG, "onMsgIntoQueue, peer: " + peer.getTag() + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

        base.onConfirmMsgIntoQueue(peer, id, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onConfirmMsgIntoQueue, peer: " + peer.getTag() + ",id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {

        base.onMsgSendStart(peer, id);
        if (printLog) {
            Logger.i(TAG, "onMsgSendStart, peer: " + peer.getTag() + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

        base.onConfirmMsgSendStart(peer, id, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onConfirmMsgSendStart, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {

        base.onMsgSendSucceeded(peer, id);
        if (printLog) {
            Logger.i(TAG, "onMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
        base.onConfirmMsgSendSucceeded(peer, id, soFar, total);

        if (printLog) {
            Logger.i(TAG, "onConfirmMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
        base.onMsgSendFailed(peer, id, msg, exception);
        if (printLog) {
            Logger.i(TAG, "onMsgSendFailed: peer: " + (peer == null ? "null" : peer.getTag()) + ", id: " + id + ", msg: " + msg + ", exception: " + exception);
        }
    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
        base.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        if (printLog) {
            Logger.i(TAG, "onConfirmMsgSendFailed, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total + ", msg: " + msg + ", exception: " + exception);

        }
    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
        base.onMsgChunkSendSucceeded(peer, id, chunkSize);
        if (printLog) {
            Logger.i(TAG, "onMsgChunkSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize);

        }
    }

    @Override
    public void onIOStreamOpened(Peer peer) {
        base.onIOStreamOpened(peer);
        if (printLog) {
            Logger.i(TAG, "onIOStreamOpened, peer: " + peer.getTag());
        }

    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
        base.onIOStreamOpenFailed(peer, errorMsg, exception);
        if (printLog) {
            Logger.i(TAG, "onIOStreamOpenFailed, peer: " + peer.getTag() + ", msg: " + errorMsg + ", exception: " + exception);
        }

    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {
        base.onCorrupted(peer, msg, e);
        if (printLog) {
            Logger.i(TAG, "onCorrupted, peer: " + peer.getTag() + ", msg: " + msg + ", exception: " + e);
        }

    }

    @Override
    public void onDestroy(Peer peer) {
        base.onDestroy(peer);
        if (printLog) {
            Logger.i(TAG, "onDestroy， peer：" + peer.getTag());
        }

    }

    @Override
    public void onTimeoutOccured(Peer peer) {
        base.onTimeoutOccured(peer);
        if (printLog) {
            Logger.i(TAG, "onTimeoutOccured, peer: " + peer.getTag());
        }
    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {

        base.onIncomingMsg(peer, id, available);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsg, peer: " + peer.getTag() + ", id: " + id + ", available: " + available);

        }

    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
        base.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailed, peer:" + peer.getTag() + ",id: " + id + ", errorMsg: " + errorMsg);

        }

    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {

        base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", available: " + available + ", chunkBytes: " + chunkBytes);

        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id) {
        base.onIncomingMsgReadSucceeded(peer, id);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, peer: " + peer.getTag() + ", id: " + id);

        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
        base.onIncomingMsgReadFailed(peer, id, total, soFar);
        if (printLog) {
            Logger.i(TAG, "onIncomingMsgReadFailed, peer: " + peer.getTag() + ", id: " + id + ", total: " + total + ", soFar: " + soFar);

        }
    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
        base.onIncomingConfirmMsg(peer, id, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onIncomingConfirmMsg, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
        base.onConfirmMsgSendPending(peer, id, soFar, total);
        if (printLog) {
            Logger.i(TAG, "onConfirmMsgSendPending, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {
        base.onMsgSendPending(peer, id);
        if (printLog) {
            Logger.i(TAG, "onMsgSendPending, peer: " + peer.getTag() + ", id: " + id);

        }
    }

    @Override
    public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {
        base.onMsgSendPending(peer, msgID);
        if (printLog) {
            Logger.i(TAG, "onSomeMsgChunkSendSucceededButNotConfirmedByPeer, peer: " + peer.getTag() + ", id: " + msgID);

        }
    }
}
