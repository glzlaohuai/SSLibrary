package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

public class AndroidAppManager implements IAppManager {

    private IDeviceInfoManager deviceInfoManager;
    private INSDServiceManager nsdServiceManager;
    private IPreferenceManager preferenceManager;
    private INetworkManager networkManager;


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
    public INSDServiceManager getNsdServiceManager() {
        return nsdServiceManager;
    }

    @Override
    public IPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    @Override
    public INetworkManager getNetworkManager() {
        return networkManager;
    }
}
