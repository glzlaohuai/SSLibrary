package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;
import com.badzzz.pasteany.core.utils.Constants;

public class MacPlatformManager extends IPlatformManager {

    private IAppManager appManager = new MacAppManager();

    @Override
    public IAppManager getAppManager() {
        return appManager;
    }

    @Override
    public String getPlatformName() {
        return Constants.Platforms.MAC;
    }
}
