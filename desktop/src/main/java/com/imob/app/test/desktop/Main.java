package com.imob.app.test.desktop;

import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.badzzz.pasteany.lib.core.desktop.mac.MacPlatformManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MacPlatformManager platformManager = new MacPlatformManager();
        // TODO: 2021/11/30 just for tests, should be removed later
        PreferenceManagerWrapper.getInstance().saveDeviceName("llf#macbookpro");
        PreferenceManagerWrapper.getInstance().saveServiceName("a_test_service_name");
        platformManager.initPlatform();
    }

}