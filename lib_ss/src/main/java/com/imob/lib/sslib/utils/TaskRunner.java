package com.imob.lib.sslib.utils;

import com.imob.lib.lib_common.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TaskRunner {
    private Timer timer = new Timer();
    private Map<Runnable, TimerTask> runnables = new HashMap<>();

    private boolean isDestroyed = false;

    public synchronized void postDelayed(final Runnable runnable, long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay time must not be negative.");
        }

        if (runnables.containsKey(runnable)) {
            runnables.get(runnable).cancel();
        }

        runnables.put(runnable, new TimerTask() {
            @Override
            public void run() {
                runnable.run();
                runnables.remove(runnable);
            }
        });
        try {
            timer.schedule(runnables.get(runnable), delay);
        } catch (Throwable e) {
            Logger.e(e);
        }
    }


    public synchronized void destroy() {
        timer.cancel();
        this.isDestroyed = true;
    }


    public synchronized boolean isDestroyed() {
        return isDestroyed;
    }

}
