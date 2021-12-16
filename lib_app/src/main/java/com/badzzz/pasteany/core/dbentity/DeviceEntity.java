package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceEntity extends IDeviceInfoManager.DeviceInfo {

    public DeviceEntity(String id, String name, String platform) {
        super(id, name, platform);
    }

    public static DeviceEntity buildWithDeviceInfo(IDeviceInfoManager.DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            return new DeviceEntity(deviceInfo.getId(), deviceInfo.getName(), deviceInfo.getPlatform());
        }
        return null;
    }

    public static DeviceEntity buildWithDBItem(Map<String, String> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        String id = item.get(Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_ID);
        String name = item.get(Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_NAME);
        String platform = item.get(Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_PLATFORM);

        return new DeviceEntity(id, name, platform);
    }


    public static List<DeviceEntity> buildWithDBQueryList(List<Map<String, String>> dbList) {
        if (dbList == null) {
            return null;
        }

        List<DeviceEntity> list = new ArrayList<>();
        for (Map<String, String> item : dbList) {
            DeviceEntity deviceEntity = buildWithDBItem(item);
            if (deviceEntity != null && deviceEntity.isValid()) {
                list.add(deviceEntity);
            }
        }

        return list;
    }


}
