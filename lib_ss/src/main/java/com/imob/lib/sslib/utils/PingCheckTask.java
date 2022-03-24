package com.imob.lib.sslib.utils;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.PingMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.util.UUID;

public class PingCheckTask {

    private static final String TAG = "PingCheckTask";

    private String logTag = TAG + " # " + hashCode();

    private TaskRunner taskRunner;
    private Peer peer;
    private Runnable runnable;
    private long interval;
    private PeerListener peerListener = new PeerListenerAdapter() {
        @Override
        public void onMsgSendStart(Peer peer, String id) {
            super.onMsgSendStart(peer, id);
            if (taskRunner != null && runnable != null && !taskRunner.isDestroyed()) {
                taskRunner.postDelayed(runnable, interval);
            }
        }
    };

    public PingCheckTask(final Peer peer) {
        this.peer = peer;
        runnable = new Runnable() {
            @Override
            public void run() {
                Logger.i(logTag, "ping check.");
                if (peer.isDestroyed()) {
                    Logger.i(logTag, "ping check invoked, but find out peer is alread destroyed, this should not happen, just call destroy on this instance");
                    destroy();
                } else {
                    peer.sendMessage(PingMsg.build(UUID.randomUUID().toString()));
                }
            }
        };
    }

    public synchronized void enable(long interval) {
        if (peer.isDestroyed()) return;

        if (taskRunner != null) {
            taskRunner.destroy();
        }
        taskRunner = new TaskRunner();
        this.interval = interval;

        taskRunner.postDelayed(runnable, interval);
        peer.registerListener(peerListener);
    }

    public synchronized void disbale() {
        if (taskRunner != null) {
            taskRunner.destroy();
        }
        peer.unregisterListener(peerListener);
    }

    public synchronized void destroy() {
        disbale();
    }


}
