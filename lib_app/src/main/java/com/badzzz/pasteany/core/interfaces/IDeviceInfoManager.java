package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;

import org.json.JSONObject;

public abstract class IDeviceInfoManager {

    public abstract String getDeviceID();

    public abstract String getDeviceName();

    public String getDeviceDetailInfo() {
        String deviceID = getDeviceID();
        String deviceName = getDeviceName();
        String platform = PlatformManagerHolder.get().getPlatformName();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.Device.KEY_DEVICEID, deviceID);
        jsonObject.put(Constants.Device.KEY_DEVICE_NAME, deviceName);
        jsonObject.put(Constants.Device.KEY_PLATFORM, platform);

        return jsonObject.toString();
    }

    public abstract void setDeviceName(String deviceName);
}
