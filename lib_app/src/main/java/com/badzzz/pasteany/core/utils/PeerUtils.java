package com.badzzz.pasteany.core.utils;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import org.json.JSONObject;

public class PeerUtils {

    public static String getDeviceIDFromPeer(Peer peer) {
        if (peer != null && peer.getTag() != null) {
            return getDeviceIDFromPeerTag(peer.getTag());
        }
        return null;
    }


    public static String getPlatformFromPeer(Peer peer) {
        if (peer != null && peer.getTag() != null) {
            return getPlatformFromPeerTag(peer.getTag());
        } else {
            return null;
        }
    }

    public static String getPlatformFromPeerTag(String peerTag) {
        try {
            JSONObject jsonObject = new JSONObject(peerTag);
            return jsonObject.optString(Constants.Device.KEY_PLATFORM);
        } catch (Throwable e) {
            Logger.e(e);
            return null;
        }
    }

    public static String getDeviceNameFromPeer(Peer peer) {
        if (peer != null && peer.getTag() != null) {
            return getDeviceNameFromPeerTag(peer.getTag());
        }
        return null;
    }


    public static String getDeviceIDFromPeerTag(String tag) {
        try {
            JSONObject jsonObject = new JSONObject(tag);
            return jsonObject.optString(Constants.Device.KEY_DEVICEID);
        } catch (Throwable e) {
            Logger.e(e);
            return null;
        }
    }

    public static String getDeviceNameFromPeerTag(String tag) {
        try {
            String jsonString = tag;

            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.optString(Constants.Device.KEY_DEVICE_NAME);
        } catch (Throwable e) {
            Logger.e(e);
        }
        return null;
    }


}
