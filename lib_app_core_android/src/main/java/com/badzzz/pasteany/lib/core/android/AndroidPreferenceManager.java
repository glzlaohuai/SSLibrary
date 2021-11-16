package com.badzzz.pasteany.lib.core.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.badzzz.pasteany.core.interfaces.IPreferenceManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;

import androidx.annotation.NonNull;

public class AndroidPreferenceManager implements IPreferenceManager {

    private Context context;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public AndroidPreferenceManager(@NonNull Context context) {
        this.context = context;
    }

    public SharedPreferences getSp() {
        if (sp == null) {
            synchronized (this) {
                if (sp == null) {
                    sp = context.getSharedPreferences(PreferenceManagerWrapper.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return sp;
    }

    public SharedPreferences.Editor getEditor() {
        if (editor == null) {
            synchronized (this) {
                editor = getSp().edit();
            }
        }
        return editor;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getSp().getString(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getSp().getInt(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getSp().getFloat(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getSp().getLong(key, defaultValue);
    }


    @Override
    public void saveString(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    @Override
    public void saveInt(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    @Override
    public void saveFloat(String key, float value) {
        getEditor().putFloat(key, value).apply();
    }

    @Override
    public void saveLong(String key, long value) {
        getEditor().putLong(key, value).apply();
    }
}
