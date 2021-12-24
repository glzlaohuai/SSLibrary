package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;

public class MacDeviceInfoManager extends IDeviceInfoManager {

    @Override
    public String getDeviceID() {
        return PreferenceManagerWrapper.getInstance().getDeviceID();
    }

    @Override
    public String getDeviceName() {
        return PreferenceManagerWrapper.getInstance().getDeviceName();
    }

    @Override
    public void setDeviceName(String deviceName) {
        PreferenceManagerWrapper.getInstance().saveDeviceName(deviceName);
    }
}
