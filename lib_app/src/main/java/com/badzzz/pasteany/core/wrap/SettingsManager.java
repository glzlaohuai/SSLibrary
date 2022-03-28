package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;
import com.badzzz.pasteany.core.nsd.NsdServiceStarter;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;

import org.json.JSONObject;


/**
 * basically a wrapper class of {@link IPreferenceManager}
 */
public class SettingsManager {
    private static final String TAG = "SettingsManager";


    private static SettingsManager instance = new SettingsManager();

    private IPreferenceManager manager;

    public static SettingsManager getInstance() {
        return instance;
    }

    private SettingsManager() {
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

    public boolean isPingCheckEnabled() {
        return manager.getBoolean(Constants.Preference.KEY_PING_CHECK_ENABLED, false) && getPingCheckInterval() > 0;
    }

    public long getPingCheckInterval() {
        return manager.getLong(Constants.Preference.KEY_PING_CHECK_INTERVAL, -1);
    }

    public void setPingCheckEnabled(boolean enabled, long time) {
        manager.saveBoolean(Constants.Preference.KEY_PING_CHECK_ENABLED, enabled);
        manager.saveLong(Constants.Preference.KEY_PING_CHECK_INTERVAL, time);
    }

    public boolean useLastKnownNsdInfo() {
        return manager.getBoolean(Constants.Preference.KEY_USE_LAST_KNOWN_NSD_INFO, false);
    }

    public void setUseLastKnownNsdInfo(boolean shouldUseLastKnownInfo) {
        manager.saveBoolean(Constants.Preference.KEY_USE_LAST_KNOWN_NSD_INFO, shouldUseLastKnownInfo);
    }

    public void saveRecentlyDiscoveredNsdInfo(String did, String ip, int port) {
        Logger.i(TAG, "save recently discovered nsd info: " + did + ", " + ip + ", " + port);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.Device.KEY_DEVICEID, did);
        jsonObject.put(Constants.Preference.KEY_IP, ip);
        jsonObject.put(Constants.Preference.KEY_PORT, port);

        manager.saveString(Constants.Preference.KEY_LAST_KNOWN_NSD_INFO_PREFIX + did, jsonObject.toString());
    }

    public String getRecentlyDiscoveredNsdInfo(String did) {
        return manager.getString(Constants.Preference.KEY_LAST_KNOWN_NSD_INFO_PREFIX + did, "");
    }

}
