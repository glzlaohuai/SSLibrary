package com.badzzz.pasteany.core;

public interface IAppManager {

    IDeviceInfoManager getDeviceInfoManager();

    INSDServiceManager getNsdServiceManager();

    IPreferenceManager getPreferenceManager();


}
