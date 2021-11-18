package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.nsd.NsdServiceStarter;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;

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
            hasInited = true;
        }
    }

    private void kickOffNsdServiceIfServiceNameSet() {
        if (PreferenceManagerWrapper.getInstance().hasSavedServiceName()) {
            NsdServiceStarter.init();
        }
    }

    private void initDefaultPreferenceValuesIfNotSet() {
        PreferenceManagerWrapper preference = PreferenceManagerWrapper.getInstance();

        if (!preference.hasSavedDeviceID()) {
            preference.saveDeviceID(getAppManager().getDeviceInfoManager().getDeviceID());
        }
    }

}