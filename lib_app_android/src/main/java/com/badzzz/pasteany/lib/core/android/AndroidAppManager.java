package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

public class AndroidAppManager implements IAppManager {

    private IDeviceInfoManager deviceInfoManager;
    private INSDServiceManager nsdServiceManager;
    private IPreferenceManager preferenceManager;
    private INetworkManager networkManager;
    private IFileManager fileManager;
    private IDBManager dbManager;

    public AndroidAppManager(Context context) {
        deviceInfoManager = new AndroidDeviceInfoManager(context);
        nsdServiceManager = new AndroidNsdServiceManager(context);
        preferenceManager = new AndroidPreferenceManager(context);
        fileManager = new AndroidFileManager(context);
        networkManager = new AndroidNetworkManager(context);
        dbManager = new AndroidDBManager(context);
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
    public IFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public IDBManager getDBManager() {
        return dbManager;
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
