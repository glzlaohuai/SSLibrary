package com.imob.app.pasteew.utils;

import android.text.TextUtils;

public class SPWrapper {

    private static final String KEY_SERVICE_NAME = "service_name";


    public static boolean hasSetServiceName() {
        return !TextUtils.isEmpty(getServiceName());
    }

    public static String getServiceName() {
        return SPUtils.getString(KEY_SERVICE_NAME, null);
    }

    public static void setServiceName(String serviceName) {
        SPUtils.saveString(KEY_SERVICE_NAME, serviceName);
    }


}
