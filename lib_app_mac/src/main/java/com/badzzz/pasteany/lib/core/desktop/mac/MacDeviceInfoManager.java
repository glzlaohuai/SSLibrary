package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.wrap.SettingsManager;

import java.util.UUID;

public class MacDeviceInfoManager extends IDeviceInfoManager {

    @Override
    public String getDeviceID() {
        String deviceID = SettingsManager.getInstance().getDeviceID();
        if (deviceID == null || deviceID.isEmpty()) {
            deviceID = UUID.randomUUID().toString();
            SettingsManager.getInstance().saveDeviceID(deviceID);
        }
        return deviceID;
    }

    @Override
    public String getDeviceName() {
        return SettingsManager.getInstance().getDeviceName();
    }

    @Override
    public void setDeviceName(String deviceName) {
        SettingsManager.getInstance().saveDeviceName(deviceName);
    }
}
