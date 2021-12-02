package com.imob.lib.sslib.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SSThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    SSThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = name + "-p-" + poolNumber.getAndIncrement() + "-t-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != 5) {
            t.setPriority(5);
        }

        return t;
    }


    public final static ThreadFactory build(String name) {
        if (name == null) return null;
        return new SSThreadFactory(name);
    }

}
