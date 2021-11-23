package com.badzzz.pasteany.core.interfaces;

public interface IAppManager {

    IDeviceInfoManager getDeviceInfoManager();

    IPreferenceManager getPreferenceManager();

    INetworkManager getNetworkManager();

    INSDServiceManager getNsdServiceManager();

    IFileManager getFileManager();

}
