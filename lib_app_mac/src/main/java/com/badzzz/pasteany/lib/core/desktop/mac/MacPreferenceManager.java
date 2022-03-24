package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

import java.util.prefs.Preferences;

public class MacPreferenceManager implements IPreferenceManager {

    private Preferences preferences = Preferences.systemRoot();

    @Override
    public String getString(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);

    }

    @Override
    public void saveString(String key, String value) {
        preferences.put(key, value);
    }

    @Override
    public void saveInt(String key, int value) {
        preferences.putInt(key, value);
    }

    @Override
    public void saveFloat(String key, float value) {
        preferences.putFloat(key, value);
    }

    @Override
    public void saveLong(String key, long value) {
        preferences.putLong(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public void saveBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }
}
