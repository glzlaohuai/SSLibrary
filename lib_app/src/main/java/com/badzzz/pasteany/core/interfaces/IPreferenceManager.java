package com.badzzz.pasteany.core.interfaces;

public interface IPreferenceManager {

    String getString(String key, String defaultValue);

    int getInt(String key, int defaultValue);

    float getFloat(String key, float defaultValue);

    long getLong(String key, long defaultValue);


    void saveString(String key, String value);

    void saveInt(String key, int value);

    void saveFloat(String key, float value);


    void saveLong(String key, long value);

    boolean getBoolean(String key, boolean defaultValue);

    void saveBoolean(String key, boolean value);

}
