package com.imob.lib.sslib.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TaskRunner {
    private Timer timer = new Timer();
    private Map<Runnable, TimerTask> runnables = new HashMap<>();

    public void postDelayed(final Runnable runnable, long delay) {
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
        timer.schedule(runnables.get(runnable), delay);
    }


    public void destroy() {
        timer.cancel();
    }

}
