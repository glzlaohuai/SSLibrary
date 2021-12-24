package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

public class MacAppManager implements IAppManager {

    private IDeviceInfoManager deviceInfoManager = new MacDeviceInfoManager();
    private IPreferenceManager preferenceManager = new MacPreferenceManager();
    private INetworkManager networkManager = new MacNetworkManager();
    private INSDServiceManager nsdServiceManager = new MacNsdServiceManager();
    private IFileManager fileManager = new MacFileManager();
    private IDBManager dbManager = new MacDBManager();


    @Override
    public IDeviceInfoManager getDeviceInfoManager() {
        return deviceInfoManager;
    }

    @Override
    public IPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    @Override
    public INetworkManager getNetworkManager() {
        return networkManager;
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
}
