package com.badzzz.pasteany.core.utils;

import com.imob.lib.sslib.peer.Peer;

import org.json.JSONObject;

public class PeerUtils {


    public static String getDeviceIDFromPeer(Peer peer) {
        if (peer != null && peer.getTag() != null) {
            String jsonString = peer.getTag();

            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.optString(Constants.Device.KEY_DEVICEID);
        }
        return null;
    }


}
