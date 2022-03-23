package com.badzzz.pasteany.core.utils;

import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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

    public static Set<String> generateDeviceIDSetFromPeerTagSet(Set<String> tagSet) {
        if (tagSet == null) return null;
        Set<String> idSet = new HashSet<>();
        for (String tag : tagSet) {
            String id = getDeviceIDFromPeerTag(tag);
            idSet.add(id);
        }
        return idSet;
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


    public static File getReceivedFileInLocalFromFileTypeMsgSendedByPeer(Peer peer, String originalMsgID) {
        String fromDeviceID = PeerUtils.getDeviceIDFromPeer(peer);
        MsgID msgID = MsgID.buildWithJsonString(originalMsgID);

        if (fromDeviceID == null || msgID == null) return null;

        File dir = PlatformManagerHolder.get().getAppManager().getFileManager().getFinalFileSaveDirWithDeviceIDAndMsgID(fromDeviceID, originalMsgID);
        String fileName = new File(msgID.getData()).getName();

        File finalFile = new File(dir, fileName);
        return finalFile;
    }

}
