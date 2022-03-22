package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class INSDServiceManager {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public abstract INsdExtraActionPerformer getExtraActionPerformer();

    /**
     * create servicename that used for info retrieving or register
     * @param deviceID
     * @param definiedServiceName
     * @return
     */
    public final static String buildServiceName(String deviceID, String definiedServiceName) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(Constants.Device.KEY_DEVICEID, deviceID);
        jsonObject.put(Constants.NSD.Key.SERVICE_NAME, definiedServiceName);

        return jsonObject.toString();
    }

    /**
     * create serviceText that used for register
     * @param deviceName
     * @param platform
     * @return
     */
    public final static String buildServiceText(String deviceName, String platform) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(Constants.Device.KEY_PLATFORM, platform);
        jsonObject.put(Constants.Device.KEY_DEVICE_NAME, deviceName);
        jsonObject.put(Constants.Device.KEY_DEVICE_NAME, deviceName);
        jsonObject.put(Constants.NSD.Key.SERVICE_CREATE_TIME, getServiceCreateTime());

        return jsonObject.toString();
    }

    private final static String getServiceCreateTime() {
        return dateFormat.format(new Date());
    }

}
