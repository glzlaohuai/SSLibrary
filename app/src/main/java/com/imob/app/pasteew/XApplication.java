package com.imob.app.pasteew;

import android.app.Application;

import com.imob.app.pasteew.utils.Constants;
import com.imob.app.pasteew.utils.SPUtils;

public class XApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SPUtils.setup(this, Constants.DEFAULT_SP_NAME);
    }
}
