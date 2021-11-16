package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.NSDServiceManager;
import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

public class AndroidAppManager implements IAppManager {

    private IDeviceInfoManager deviceInfoManager;
    private NSDServiceManager nsdServiceManager;
    private IPreferenceManager preferenceManager;

    public AndroidAppManager(Context context) {
        deviceInfoManager = new AndroidDeviceInfoManager();
        nsdServiceManager = new AndroidNsdServiceManager();
        preferenceManager = new AndroidPreferenceManager(context);
    }

    @Override
    public IDeviceInfoManager getDeviceInfoManager() {
        return deviceInfoManager;
    }

    @Override
    public NSDServiceManager getNsdServiceManager() {
        return nsdServiceManager;
    }

    @Override
    public IPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
