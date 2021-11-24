package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IPlatformManager;

public class PlatformManagerHolder {

    private static IPlatformManager platformManager;

    public final static void hold(IPlatformManager platformManager) {
        PlatformManagerHolder.platformManager = platformManager;
    }

    public final static IPlatformManager get() {
        return platformManager;
    }
}
