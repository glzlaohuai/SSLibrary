package com.badzzz.pasteany.core.utils;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.imob.lib.sslib.peer.Peer;

public class PeerUtils {


    public static IDeviceInfoManager.DeviceInfo generateDeviceInfoFromPeer(Peer peer) {
        if (peer == null || peer.getTag() == null) return null;
        return IDeviceInfoManager.DeviceInfo.buildFromJsonString(peer.getTag());
    }


    public static IDeviceInfoManager.DeviceInfo generateDeviceInfoFromPeerTag(String tag) {
        return IDeviceInfoManager.DeviceInfo.buildFromJsonString(tag);
    }

    public static String getDeviceIDFromPeer(Peer peer) {
        IDeviceInfoManager.DeviceInfo deviceEntity = generateDeviceInfoFromPeer(peer);
        return deviceEntity == null ? null : deviceEntity.getId();
    }


    public static String getPlatformFromPeer(Peer peer) {
        IDeviceInfoManager.DeviceInfo deviceEntity = generateDeviceInfoFromPeer(peer);
        return deviceEntity == null ? null : deviceEntity.getPlatform();
    }

    public static String getPlatformFromPeerTag(String peerTag) {
        IDeviceInfoManager.DeviceInfo deviceEntity = generateDeviceInfoFromPeerTag(peerTag);
        return deviceEntity == null ? null : deviceEntity.getPlatform();
    }

    public static String getDeviceIDFromPeerTag(String tag) {
        IDeviceInfoManager.DeviceInfo deviceInfo = IDeviceInfoManager.DeviceInfo.buildFromJsonString(tag);
        return deviceInfo == null ? null : deviceInfo.getId();
    }

    public static String getDeviceNameFromPeerTag(String tag) {
        IDeviceInfoManager.DeviceInfo deviceInfo = IDeviceInfoManager.DeviceInfo.buildFromJsonString(tag);
        return deviceInfo == null ? null : deviceInfo.getName();
    }

}
