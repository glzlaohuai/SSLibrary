package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IPlatformManager;


/**
 * 一个工具类，用于方便的获取{@link IPlatformManager}的实例，该类的{{@link #hold(IPlatformManager)}}方法在{@link IPlatformManager#IPlatformManager()}中调用
 * 所以，任何继承了{@link IPlatformManager}的类的构造方法都需要调用其super方法
 */
public class PlatformManagerHolder {

    private static IPlatformManager platformManager;

    public final static void hold(IPlatformManager platformManager) {
        PlatformManagerHolder.platformManager = platformManager;
    }

    public final static IPlatformManager get() {
        return platformManager;
    }
}
