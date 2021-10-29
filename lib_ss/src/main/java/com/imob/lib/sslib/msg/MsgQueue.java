package com.imob.lib.sslib.msg;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MsgQueue {

    private Queue<Msg> queue = new LinkedBlockingQueue<>();

    public void add(Msg msg) {
        if (msg != null && msg.isValid()) {
            queue.add(msg);

        }
    }

    public Msg poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }


}
