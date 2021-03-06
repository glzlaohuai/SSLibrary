package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;

import org.json.JSONObject;

import java.util.Objects;

public abstract class IDeviceInfoManager {

    public abstract String getDeviceID();

    public abstract String getDeviceName();


    public String getDeviceDetailInfo() {
        String deviceID = getDeviceID();
        String deviceName = getDeviceName();
        String platform = PlatformManagerHolder.get().getPlatformName();

        DeviceInfo dev = new DeviceInfo(deviceID, deviceName, platform);
        return dev.toJson();
    }

    public abstract void setDeviceName(String deviceName);

    public static class DeviceInfo implements Comparable<DeviceInfo> {

        private String id;
        private String name;
        private String platform;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPlatform() {
            return platform;
        }


        public boolean isValid() {
            return id != null && !id.isEmpty() && name != null && !name.isEmpty() && platform != null && !platform.isEmpty();
        }

        public static DeviceInfo buildFromJsonString(String jsonString) {
            if (jsonString == null || jsonString.isEmpty()) {
                return null;
            }

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                String deviceID = jsonObject.getString(Constants.Device.KEY_DEVICEID);
                String deviceName = jsonObject.getString(Constants.Device.KEY_DEVICE_NAME);
                String platform = jsonObject.getString(Constants.Device.KEY_PLATFORM);

                return new DeviceInfo(deviceID, deviceName, platform);
            } catch (Throwable e) {
                Logger.e(e);
            }
            return null;
        }


        public DeviceInfo(String id, String name, String platform) {
            this.id = id;
            this.name = name;
            this.platform = platform;
        }

        public String toJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.Device.KEY_DEVICEID, id);
                jsonObject.put(Constants.Device.KEY_DEVICE_NAME, name);
                jsonObject.put(Constants.Device.KEY_PLATFORM, platform);
            } catch (Throwable e) {
                Logger.e(e);
            }
            return jsonObject.toString();
        }


        @Override
        public String toString() {
            return toJson();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceInfo that = (DeviceInfo) o;
            return id.equals(that.id) && name.equals(that.name) && platform.equals(that.platform);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, platform);
        }

        @Override
        public int compareTo(DeviceInfo o) {
            return this.id.compareTo(o.id);
        }
    }


}
