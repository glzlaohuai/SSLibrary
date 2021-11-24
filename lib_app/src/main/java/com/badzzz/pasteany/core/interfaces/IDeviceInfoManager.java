package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;

import org.json.JSONObject;

public abstract class IDeviceInfoManager {

    public abstract String getDeviceID();

    public abstract String getDeviceName();

    public String getDeviceDetailInfo() {
        String deviceID = getDeviceID();
        String deviceName = getDeviceName();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.Preference.KEY_DEVICEID, deviceID);
        jsonObject.put(Constants.Preference.KEY_DEVICE_NAME, deviceName);

        return jsonObject.toString();
    }

    public abstract void setDeviceName(String deviceName);
}
