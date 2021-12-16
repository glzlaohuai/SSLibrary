package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.dbentity.DeviceEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TotalEverConnectedDeviceInfoManager {

    private static final String TAG = "TotalEverConnectedDeviceInfoManager";

    private final static Map<String, IDeviceInfoManager.DeviceInfo> totalKnownDevices = new HashMap<>();

    private static boolean hasInited = false;
    private static boolean fetched = false;
    private static ITotalEverConnectedDeviceInfoListenerGroup deviceInfoListener = new ITotalEverConnectedDeviceInfoListenerGroup();


    public interface ITotalEverConnectedDeviceInfoListener {
        void onUpdated(Map<String, IDeviceInfoManager.DeviceInfo> all);
    }


    public static class ITotalEverConnectedDeviceInfoListenerGroup implements ITotalEverConnectedDeviceInfoListener {

        private Set<ITotalEverConnectedDeviceInfoListener> set = new HashSet<>();

        public void add(ITotalEverConnectedDeviceInfoListener listener) {
            if (listener != null) {
                set.add(listener);
            }
        }

        public void remove(ITotalEverConnectedDeviceInfoListener listener) {
            if (listener != null) {
                set.remove(listener);
            }
        }

        @Override
        public void onUpdated(Map<String, IDeviceInfoManager.DeviceInfo> all) {
            for (ITotalEverConnectedDeviceInfoListener listener : set) {
                listener.onUpdated(all);
            }
        }
    }


    private static void getDeviceInfoFromConnectedPeersAndAddToMap() {
        Set<String> tagSet = ConnectedPeersManager.getConnectedPeersTagSet();
        if (tagSet != null && tagSet.size() > 0) {
            tagSet = new HashSet<>(tagSet);
            for (String tag : tagSet) {
                IDeviceInfoManager.DeviceInfo connectedPeerDeviceInfo = PeerUtils.generateDeviceInfoFromPeerTag(tag);
                if (connectedPeerDeviceInfo != null && connectedPeerDeviceInfo.isValid()) {
                    totalKnownDevices.put(connectedPeerDeviceInfo.getId(), connectedPeerDeviceInfo);
                }
            }
        }
    }

    public synchronized final static void init() {
        if (hasInited) return;
        hasInited = true;
        //get all known devices from db
        DBManagerWrapper.getInstance().queryAllDevices(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {
                List<Map<String, String>> queryList = getResultList();
                List<DeviceEntity> deviceEntities = DeviceEntity.buildWithDBQueryList(queryList);
                if (deviceEntities != null) {
                    for (DeviceEntity entity : deviceEntities) {
                        totalKnownDevices.put(entity.getId(), entity);
                    }
                }
                getDeviceInfoFromConnectedPeersAndAddToMap();
                callbackDeviceInfoUpdated();
                fetched = true;
            }
        });

        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerAdapter() {
            @Override
            public void onIncomingPeer(Peer peer) {
                super.onIncomingPeer(peer);
                afterPeerIncoming(peer);
            }
        });
    }

    //read from db finished or not
    public static boolean isFetchedAlready() {
        return fetched;
    }


    public final static void afterPeerIncoming(Peer peer) {
        IDeviceInfoManager.DeviceInfo deviceInfo = PeerUtils.generateDeviceInfoFromPeer(peer);

        if (deviceInfo != null) {
            DeviceEntity deviceEntity = DeviceEntity.buildWithDeviceInfo(deviceInfo);

            if (!isIncomingDeviceAlreadyKnownAndNoInfoChanged(deviceEntity)) {
                Logger.i(TAG, "incoming device is not in db or info changed, update/insert it: " + deviceEntity);
                DBManagerWrapper.getInstance().addDeviceInfo(deviceEntity, new DBManagerWrapper.IDBActionListenerWrapper());
            }

            totalKnownDevices.put(deviceEntity.getId(), deviceEntity);
            callbackDeviceInfoUpdated();
        }
    }


    private static void callbackDeviceInfoUpdated() {
        if (deviceInfoListener != null) {
            deviceInfoListener.onUpdated(totalKnownDevices);
        }
    }


    private final static boolean isIncomingDeviceAlreadyKnownAndNoInfoChanged(IDeviceInfoManager.DeviceInfo deviceInfo) {
        if (deviceInfo == null && deviceInfo.isValid()) return false;

        IDeviceInfoManager.DeviceInfo oldDeviceInfo = totalKnownDevices.get(deviceInfo.getId());
        return oldDeviceInfo != null && oldDeviceInfo.getName().equals(deviceInfo.getName());
    }


    public static Map<String, IDeviceInfoManager.DeviceInfo> getTotalKnownDevices() {
        return totalKnownDevices;
    }

    public static String getDeviceNameById(String id) {
        if (id == null || !totalKnownDevices.containsKey(id)) return null;
        return totalKnownDevices.get(id).getName();
    }

    public static String getPlatformNameById(String id) {
        if (id == null || !totalKnownDevices.containsKey(id)) return null;
        return totalKnownDevices.get(id).getPlatform();
    }


    public static void monitorTotalEverConnectedDeviceListUpdate(ITotalEverConnectedDeviceInfoListener listener) {
        deviceInfoListener.add(listener);
    }

    public static void unmonitorTotalEventConnectedDeviceListUpdate(ITotalEverConnectedDeviceInfoListener listener) {
        deviceInfoListener.remove(listener);
    }
}
