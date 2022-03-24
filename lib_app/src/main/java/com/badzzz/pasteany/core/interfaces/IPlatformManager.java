package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.manager.MsgEntitiesManager;
import com.badzzz.pasteany.core.manager.TotalEverDiscoveredDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.NsdServiceStarter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.SettingsManager;


/**
 * 相当于初始化的入口处，在这里做几乎所有的模块的初始化操作
 */
public abstract class IPlatformManager {

    private boolean hasInited = false;

    public abstract IAppManager getAppManager();

    public abstract String getPlatformName();

    public IPlatformManager() {
        PlatformManagerHolder.hold(this);
    }

    public synchronized void initPlatform() {
        if (!hasInited) {
            initDefaultPreferenceValuesIfNotSet();
            kickOffNsdServiceIfServiceNameSet();
            ConnectedPeersManager.init();
            TotalEverDiscoveredDeviceInfoManager.init();
            MsgEntitiesManager.init();
            hasInited = true;
        }
    }

    private void kickOffNsdServiceIfServiceNameSet() {
        if (SettingsManager.getInstance().hasSavedServiceName()) {
            NsdServiceStarter.init();
        }
    }

    private void initDefaultPreferenceValuesIfNotSet() {
        SettingsManager preference = SettingsManager.getInstance();

        if (!preference.hasSavedDeviceID()) {
            preference.saveDeviceID(getAppManager().getDeviceInfoManager().getDeviceID());
        }
    }

}
