package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;

import org.json.JSONObject;

public abstract class INSDServiceManager {
    public abstract INsdExtraActionPerformer getExtraActionPerformer();

    /**
     * create servicename that used for watch or register
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


}
