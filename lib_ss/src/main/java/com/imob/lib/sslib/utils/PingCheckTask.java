package com.imob.lib.sslib.utils;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.PingMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PingCheckTask {

    private static final String TAG = "PingCheckTask";

    private String logTag = TAG + " # " + hashCode();

    private Peer peer;
    private Runnable runnable;
    private long interval;
    private ScheduledThreadPoolExecutor executor;


    private PeerListener peerListener = new PeerListenerAdapter() {
        @Override
        public void onDestroy(Peer peer) {
            super.onDestroy(peer);
            disbale();
        }
    };

    public PingCheckTask(final Peer peer) {
        this.peer = peer;
        runnable = new Runnable() {
            @Override
            public void run() {
                Logger.i(logTag, "ping check loop entered");
                if (peer.isDestroyed()) {
                    Logger.i(logTag, "ping check invoked, but find out peer is alread destroyed, this should not happen, just call destroy on this instance");
                    destroy();
                } else {
                    if (peer.isMsgQueueEmpty()) {
                        Logger.i(logTag, "msg queue is empty, send out a ping msg immediately.");
                        peer.sendMessage(PingMsg.build(UUID.randomUUID().toString()));
                    } else {
                        Logger.i(logTag, "has msg in queue, wait for next check loop");
                    }
                }
            }
        };
    }

    public synchronized void enable(long interval) {
        if (peer.isDestroyed()) return;

        disbale();
        executor = new ScheduledThreadPoolExecutor(1, new SSThreadFactory("pcheck"));
        this.interval = interval;

        executor.scheduleWithFixedDelay(runnable, 0, interval, TimeUnit.MILLISECONDS);
        peer.registerListener(peerListener);
    }

    public synchronized void disbale() {
        peer.unregisterListener(peerListener);
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    public synchronized void destroy() {
        disbale();
    }


}
