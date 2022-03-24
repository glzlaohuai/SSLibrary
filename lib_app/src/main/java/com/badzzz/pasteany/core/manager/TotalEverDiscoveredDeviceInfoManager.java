package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.dbentity.DeviceEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.NsdServiceInfoUtils;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

public class TotalEverDiscoveredDeviceInfoManager {

    private static final String TAG = "TotalEverDiscoveredDeviceInfoManager";

    private final static Map<String, IDeviceInfoManager.DeviceInfo> totalKnownDevices = new HashMap<>();

    private static boolean hasInited = false;
    private static boolean fetched = false;
    private static ITotalEverConnectedDeviceInfoListenerGroup deviceInfoListener = new ITotalEverConnectedDeviceInfoListenerGroup();
    private final static NsdEventListener nsdEventListener = new NsdEventListenerAdapter() {
        @Override
        public void onServiceDiscovered(NsdNode nsdNode, ServiceInfo event) {
            super.onServiceDiscovered(nsdNode, event);
            afterNsdServiceDiscovered(event);
        }
    };


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

    private static void addSelfDeviceInfoToTotalKnownDevice() {
        IDeviceInfoManager deviceInfoManager = PlatformManagerHolder.get().getAppManager().getDeviceInfoManager();
        IDeviceInfoManager.DeviceInfo deviceInfo = new IDeviceInfoManager.DeviceInfo(deviceInfoManager.getDeviceID(), deviceInfoManager.getDeviceName(), PlatformManagerHolder.get().getPlatformName());
        totalKnownDevices.put(deviceInfo.getId(), deviceInfo);
    }

    public synchronized final static void init() {
        if (hasInited) return;
        hasInited = true;
        addSelfDeviceInfoToTotalKnownDevice();
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
                Logger.i(TAG, "total known devices from db: " + totalKnownDevices);
                fetched = true;
            }
        });

        NsdNode.monitorListener(nsdEventListener);
    }

    //read from db finished or not
    public static boolean isFetchedAlready() {
        return fetched;
    }

    public final static void afterNsdServiceDiscovered(ServiceInfo event) {

        IDeviceInfoManager.DeviceInfo deviceInfo = NsdServiceInfoUtils.buildFromServiceInfo(event);

        if (deviceInfo != null) {
            DeviceEntity deviceEntity = DeviceEntity.buildWithDeviceInfo(deviceInfo);

            if (!isIncomingDeviceAlreadyKnownAndNoInfoChanged(deviceEntity)) {
                Logger.i(TAG, "incoming device is not in db or info changed, update/insert it: " + deviceEntity);
                DBManagerWrapper.getInstance().addDeviceInfo(deviceEntity, new DBManagerWrapper.IDBActionListenerAdapter());
            }

            totalKnownDevices.put(deviceEntity.getId(), deviceEntity);
            callbackDeviceInfoUpdated();
        }
    }


    private static void callbackDeviceInfoUpdated() {
        if (deviceInfoListener != null) {
            deviceInfoListener.onUpdated(new HashMap<String, IDeviceInfoManager.DeviceInfo>(totalKnownDevices));
        }
    }


    private final static boolean isIncomingDeviceAlreadyKnownAndNoInfoChanged(IDeviceInfoManager.DeviceInfo deviceInfo) {
        if (deviceInfo == null && deviceInfo.isValid()) return false;

        IDeviceInfoManager.DeviceInfo oldDeviceInfo = totalKnownDevices.get(deviceInfo.getId());
        return oldDeviceInfo != null && oldDeviceInfo.getName().equals(deviceInfo.getName());
    }


    public static Map<String, IDeviceInfoManager.DeviceInfo> getTotalKnownDevices() {
        return new HashMap<>(totalKnownDevices);
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

    public static void removeSelfDevice(Map<String, IDeviceInfoManager.DeviceInfo> map) {
        if (map == null) return;
        map.remove(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID());
    }


}
