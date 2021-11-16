package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;


/**
 * basically a wrapper class of {@link IPreferenceManager}
 */
public class PreferenceManagerWrapper {
    public static final String PREFERENCE_FILE_NAME = "pasteany";

    private static final String KEY_DEVICEID = "device_id";
    private static final String KEY_DEVICE_NAME = "device_name";
    private static final String KEY_SERVICE_NAME = "service_name";

    private static PreferenceManagerWrapper instance = new PreferenceManagerWrapper();

    private IPreferenceManager manager;

    public static PreferenceManagerWrapper getInstance() {
        return instance;
    }

    private PreferenceManagerWrapper() {
        manager = PlatformManagerHolder.get().getAppManager().getPreferenceManager();
    }


    public String getDeviceID() {
        return manager.getString(KEY_DEVICEID, null);
    }


    public String getDeviceName() {
        return manager.getString(KEY_DEVICE_NAME, null);
    }


    public String getServiceName() {
        return manager.getString(KEY_SERVICE_NAME, null);
    }


    public void saveDeviceID(String deviceID) {
        manager.saveString(KEY_DEVICEID, deviceID);
    }


    public void saveDeviceName(String deviceName) {
        manager.saveString(KEY_DEVICE_NAME, deviceName);

    }

    public void saveServiceName(String serviceName) {
        manager.saveString(KEY_SERVICE_NAME, serviceName);
    }


    public boolean hasSavedDeviceID() {
        return getDeviceID() != null && !getDeviceID().isEmpty();
    }

    public boolean hasSavedDeviceName() {
        return getDeviceName() != null && !getDeviceName().isEmpty();
    }


    public boolean hasSavedServiceName() {
        return getServiceName() != null && !getServiceName().isEmpty();
    }


}
