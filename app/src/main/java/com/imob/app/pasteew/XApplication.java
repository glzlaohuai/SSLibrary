package com.imob.app.pasteew;

import android.app.Application;
import android.content.Context;

import com.imob.app.pasteew.utils.Constants;
import com.imob.app.pasteew.utils.SPWrapper;
import com.imob.app.pasteew.utils.ServiceRegister;
import com.imob.lib.common.android.SPUtils;

public class XApplication extends Application {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SPUtils.setup(this, Constants.DEFAULT_SP_NAME);

        registerServiceIfHasSetName();
    }


    private void registerServiceIfHasSetName() {
        if (SPWrapper.hasSetServiceName()) {
            ServiceRegister.startServiceRegisterStuff();
        }
    }
}
