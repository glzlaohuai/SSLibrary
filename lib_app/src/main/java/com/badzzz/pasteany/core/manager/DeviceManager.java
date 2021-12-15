package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.dbentity.DeviceEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.imob.lib.sslib.peer.Peer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceManager {

    private final static Map<String, DeviceEntity> connectedDevices = new HashMap();
    private final static Map<String, DeviceEntity> totalKnownDevices = new HashMap<>();

    private static boolean hasInited = false;


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
                        DeviceEntity deviceEntityInConnected = connectedDevices.get(entity.getId());

                        if (deviceEntityInConnected != null) {
                            totalKnownDevices.put(entity.getId(), deviceEntityInConnected);
                        } else {
                            totalKnownDevices.put(entity.getId(), entity);
                        }
                    }
                }
                totalKnownDevices.putAll(connectedDevices);
            }
        });

        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerAdapter() {
            @Override
            public void onIncomingPeer(Peer peer) {
                super.onIncomingPeer(peer);
                afterPeerIncoming(peer);
            }

            @Override
            public void onPeerLost(Peer peer) {
                super.onPeerLost(peer);
                afterPeerLost(peer);
            }
        });


    }


    public final static void afterPeerIncoming(Peer peer) {
        IDeviceInfoManager.DeviceInfo deviceInfo = PeerUtils.generateDeviceInfoFromPeer(peer);

        if (deviceInfo != null) {
            DeviceEntity deviceEntity = DeviceEntity.buildWithDeviceInfo(deviceInfo);

            connectedDevices.put(deviceEntity.getId(), deviceEntity);
            totalKnownDevices.put(deviceEntity.getId(), deviceEntity);

            DBManagerWrapper.getInstance().addDeviceInfo(deviceEntity, new DBManagerWrapper.IDBActionListenerWrapper());
        }
    }


    public final static void afterPeerLost(Peer peer) {

    }


}
