package com.imob.app.pasteew.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

public class SPUtils {

    private static Context context;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private static String DEFAULT_SP_NAME;

    public final static void setup(Context context, String defaultSPName) {
        SPUtils.context = context;
        defaultSPName = defaultSPName;
        sp = context.getSharedPreferences(DEFAULT_SP_NAME, Context.MODE_PRIVATE);

        editor = sp.edit();
    }

    public static void saveString(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            editor.putString(key, value).apply();
        }
    }


    public static String getString(String key, String opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getString(key, opt);
        }
        return null;
    }

    public static void saveString(String sp, String key, String value) {
        if (!TextUtils.isEmpty(sp) && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                sharedPreferences.edit().putString(key, value).apply();
            }
        }
    }


    public static String getString(String sp, String key, String opt) {
        if (!TextUtils.isEmpty(sp) && !TextUtils.isEmpty(key)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getString(key, opt);
            }
        }
        return null;
    }


    public static void saveInt(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putInt(key, value).apply();
        }
    }


    public static int getInt(String key, int opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getInt(key, opt);
        }
        return -1;
    }


    public static void saveInt(String sp, String key, int value) {
        if (!TextUtils.isEmpty(sp) && !TextUtils.isEmpty(key)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                sharedPreferences.edit().putInt(key, value).apply();
            }
        }
    }


    public static int getInt(String sp, String key, int opt) {
        if (!TextUtils.isEmpty(sp) && !TextUtils.isEmpty(key)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                return sharedPreferences.getInt(key, opt);
            }
        }
        return -1;
    }


    public static void saveFloat(String key, float value) {
        if (!TextUtils.isEmpty(key)) {
            sp.edit().putFloat(key, value).apply();
        }
    }


    public static float getFloat(String key, float opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getFloat(key, opt);
        }

        return -1;
    }


    public static void saveLong(String key, long value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putLong(key, value).apply();
        }
    }


    public static long getLong(String key, long opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getLong(key, opt);
        }

        return -1;
    }


    public static void saveBoolean(String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putBoolean(key, value);
        }
    }


    public static boolean getBoolean(String key, boolean opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getBoolean(key, opt);
        }
        return false;
    }


    public static void saveSet(String key, Set<String> value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            editor.putStringSet(key, value).apply();
        }
    }


    public static Set<String> getSet(String key, Set<String> opt) {
        if (!TextUtils.isEmpty(key)) {
            return sp.getStringSet(key, opt);
        }
        return null;
    }


    public static void remove(String key) {
        if (!TextUtils.isEmpty(key)) {
            editor.remove(key).apply();
        }
    }


    public static void clearAll() {
        editor.clear().apply();
    }

    public static Map<String, ?> readAll() {
        return sp.getAll();
    }


}
