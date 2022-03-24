package com.badzzz.pasteany.core.utils;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.imob.lib.lib_common.Logger;

import org.json.JSONObject;

import javax.jmdns.ServiceInfo;

public class NsdServiceInfoUtils {
    private static final String TAG = "NsdServiceInfoUtils";


    public final static IDeviceInfoManager.DeviceInfo buildFromServiceInfo(ServiceInfo serviceInfo) {
        if (serviceInfo == null) return null;

        String name = serviceInfo.getName();
        String textString = serviceInfo.getTextString();

        IDeviceInfoManager.DeviceInfo deviceInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(name);
            String deviceID = jsonObject.getString(Constants.Device.KEY_DEVICEID);
            jsonObject = new JSONObject(textString);
            String deviceName = jsonObject.getString(Constants.Device.KEY_DEVICE_NAME);
            String platform = jsonObject.getString(Constants.Device.KEY_PLATFORM);


            deviceInfo = new IDeviceInfoManager.DeviceInfo(deviceID, deviceName, platform);
        } catch (Throwable e) {
            Logger.e(TAG, "retrieve device info from nsd service info failed", e);
        }
        return deviceInfo;

    }


}
