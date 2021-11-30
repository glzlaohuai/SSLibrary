package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;
import com.badzzz.pasteany.core.nsd.NsdServiceStarter;
import com.badzzz.pasteany.core.utils.Constants;


/**
 * basically a wrapper class of {@link IPreferenceManager}
 */
public class PreferenceManagerWrapper {


    private static PreferenceManagerWrapper instance = new PreferenceManagerWrapper();

    private IPreferenceManager manager;

    public static PreferenceManagerWrapper getInstance() {
        return instance;
    }

    private PreferenceManagerWrapper() {
        manager = PlatformManagerHolder.get().getAppManager().getPreferenceManager();
    }


    public String getDeviceID() {
        return manager.getString(Constants.Preference.KEY_DEVICEID, null);
    }


    public String getDeviceName() {
        return manager.getString(Constants.Preference.KEY_DEVICE_NAME, null);
    }


    public String getServiceName() {
        return manager.getString(Constants.Preference.KEY_SERVICE_NAME, null);
    }


    public void saveDeviceID(String deviceID) {
        manager.saveString(Constants.Preference.KEY_DEVICEID, deviceID);
    }


    public void saveDeviceName(String deviceName) {
        manager.saveString(Constants.Preference.KEY_DEVICE_NAME, deviceName);

    }

    public void saveServiceName(String serviceName) {
        manager.saveString(Constants.Preference.KEY_SERVICE_NAME, serviceName);

        NsdServiceStarter.stuffAfterServiceNameSetted();
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
