package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;

import java.util.UUID;

public class MacDeviceInfoManager extends IDeviceInfoManager {

    @Override
    public String getDeviceID() {
        String deviceID = PreferenceManagerWrapper.getInstance().getDeviceID();
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = UUID.randomUUID().toString();
            PreferenceManagerWrapper.getInstance().saveDeviceID(deviceID);
        }
        return deviceID;
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
