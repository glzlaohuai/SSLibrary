package com.badzzz.pasteany.lib.core.android;

import android.content.Context;
import android.provider.Settings;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;

public class AndroidDeviceInfoManager extends IDeviceInfoManager {

    private Context context;

    public AndroidDeviceInfoManager(Context context) {
        this.context = context;
    }

    @Override
    public String getDeviceID() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public String getDeviceName() {
        return PreferenceManagerWrapper.getInstance().getDeviceName();
    }

    @Override
    public void setDeviceName(String deviceName) {
        PreferenceManagerWrapper.getInstance().saveDeviceName(deviceName);
    }

}
