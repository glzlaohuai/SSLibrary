package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;

import java.util.Properties;

public class MacPreferenceManager implements IPreferenceManager {

    private Properties properties = new Properties();

    @Override
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)));
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return Float.parseFloat(properties.getProperty(key, Float.toString(defaultValue)));
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return Long.parseLong(properties.getProperty(key, Long.toString(defaultValue)));

    }

    @Override
    public void saveString(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    public void saveInt(String key, int value) {
        properties.setProperty(key, Integer.toString(value));
    }

    @Override
    public void saveFloat(String key, float value) {
        properties.setProperty(key, Float.toString(value));
    }

    @Override
    public void saveLong(String key, long value) {
        properties.setProperty(key, Long.toString(value));
    }
}
