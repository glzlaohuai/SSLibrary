package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.NSDServiceManager;

public interface IAppManager {

    IDeviceInfoManager getDeviceInfoManager();

    NSDServiceManager getNsdServiceManager();

    IPreferenceManager getPreferenceManager();

    INetworkManager getNetworkManager();
}
