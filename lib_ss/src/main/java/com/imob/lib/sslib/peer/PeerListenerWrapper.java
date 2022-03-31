package com.imob.lib.sslib.peer;

import com.imob.lib.lib_common.Logger;

public class PeerListenerWrapper implements PeerListener {
    private static final String S_TAG = "PeerListenerWrapper";

    private PeerListener base;
    private boolean printLog;

    private String tag = S_TAG + " # " + hashCode();

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
            Logger.i(tag, "onMsgIntoQueue, peer: " + peer + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

        base.onConfirmMsgIntoQueue(peer, id, soFar, total);
        if (printLog) {
            Logger.i(tag, "onConfirmMsgIntoQueue, peer: " + peer + ",id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendStart(Peer peer, String id) {

        base.onMsgSendStart(peer, id);
        if (printLog) {
            Logger.i(tag, "onMsgSendStart, peer: " + peer + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

        base.onConfirmMsgSendStart(peer, id, soFar, total);
        if (printLog) {
            Logger.i(tag, "onConfirmMsgSendStart, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendSucceeded(Peer peer, String id) {

        base.onMsgSendSucceeded(peer, id);
        if (printLog) {
            Logger.i(tag, "onMsgSendSucceeded, peer: " + peer + ", id: " + id);

        }
    }

    @Override
    public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
        base.onConfirmMsgSendSucceeded(peer, id, soFar, total);

        if (printLog) {
            Logger.i(tag, "onConfirmMsgSendSucceeded, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
        base.onMsgSendFailed(peer, id, msg, exception);
        if (printLog) {
            Logger.i(tag, "onMsgSendFailed: peer: " + (peer == null ? "null" : peer) + ", id: " + id + ", msg: " + msg + ", exception: " + exception);
        }
    }

    @Override
    public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
        base.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        if (printLog) {
            Logger.i(tag, "onConfirmMsgSendFailed, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", total: " + total + ", msg: " + msg + ", exception: " + exception);

        }
    }

    @Override
    public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize, int round, int needRound) {
        base.onMsgChunkSendSucceeded(peer, id, chunkSize, round, needRound);
        if (printLog) {
            Logger.i(tag, "onMsgChunkSendSucceeded, peer: " + peer + ", id: " + id + ", chunkSize: " + chunkSize + ", round: " + round + ", needRound: " + needRound);
        }
    }

    @Override
    public void onIOStreamOpened(Peer peer) {
        base.onIOStreamOpened(peer);
        if (printLog) {
            Logger.i(tag, "onIOStreamOpened, peer: " + peer);
        }

    }

    @Override
    public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
        base.onIOStreamOpenFailed(peer, errorMsg, exception);
        if (printLog) {
            Logger.i(tag, "onIOStreamOpenFailed, peer: " + peer + ", msg: " + errorMsg + ", exception: " + exception);
        }

    }

    @Override
    public void onCorrupted(Peer peer, String msg, Exception e) {
        base.onCorrupted(peer, msg, e);
        if (printLog) {
            Logger.i(tag, "onCorrupted, peer: " + peer + ", msg: " + msg + ", exception: " + e);
        }

    }

    @Override
    public void onDestroy(Peer peer) {
        base.onDestroy(peer);
        if (printLog) {
            Logger.i(tag, "onDestroy， peer：" + peer);
        }

    }

    @Override
    public void onTimeoutOccured(Peer peer) {
        base.onTimeoutOccured(peer);
        if (printLog) {
            Logger.i(tag, "onTimeoutOccured, peer: " + peer);
        }
    }

    @Override
    public void onIncomingMsg(Peer peer, String id, int available) {

        base.onIncomingMsg(peer, id, available);
        if (printLog) {
            Logger.i(tag, "onIncomingMsg, peer: " + peer + ", id: " + id + ", available: " + available);

        }

    }

    @Override
    public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
        base.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
        if (printLog) {
            Logger.i(tag, "onIncomingMsgChunkReadFailed, peer:" + peer + ",id: " + id + ", errorMsg: " + errorMsg);

        }

    }

    @Override
    public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {

        base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
        if (printLog) {
            Logger.i(tag, "onIncomingMsgChunkReadSucceeded, peer: " + peer + ", id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", available: " + available + ", chunkBytes: " + chunkBytes);

        }
    }

    @Override
    public void onIncomingMsgReadSucceeded(Peer peer, String id, int available) {
        base.onIncomingMsgReadSucceeded(peer, id, available);
        if (printLog) {
            Logger.i(tag, "onIncomingMsgReadSucceeded, peer: " + peer + ", id: " + id);

        }
    }

    @Override
    public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
        base.onIncomingMsgReadFailed(peer, id, total, soFar);
        if (printLog) {
            Logger.i(tag, "onIncomingMsgReadFailed, peer: " + peer + ", id: " + id + ", total: " + total + ", soFar: " + soFar);

        }
    }

    @Override
    public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
        base.onIncomingConfirmMsg(peer, id, soFar, total);
        if (printLog) {
            Logger.i(tag, "onIncomingConfirmMsg, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
        base.onConfirmMsgSendPending(peer, id, soFar, total);
        if (printLog) {
            Logger.i(tag, "onConfirmMsgSendPending, peer: " + peer + ", id: " + id + ", soFar: " + soFar + ", total: " + total);

        }
    }

    @Override
    public void onMsgSendPending(Peer peer, String id) {
        base.onMsgSendPending(peer, id);
        if (printLog) {
            Logger.i(tag, "onMsgSendPending, peer: " + peer + ", id: " + id);

        }
    }

    @Override
    public void onSomeMsgChunkSendSucceededButNotConfirmedByPeer(Peer peer, String msgID) {
        base.onMsgSendPending(peer, msgID);
        if (printLog) {
            Logger.i(tag, "onSomeMsgChunkSendSucceededButNotConfirmedByPeer, peer: " + peer + ", id: " + msgID);

        }
    }
}
