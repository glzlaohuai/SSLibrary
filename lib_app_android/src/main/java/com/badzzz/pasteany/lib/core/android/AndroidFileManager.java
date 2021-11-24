package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.IFileManager;
import com.badzzz.pasteany.core.utils.Constants;

import java.io.File;

public class AndroidFileManager extends IFileManager {

    private Context context;

    public AndroidFileManager(Context context) {
        this.context = context;
    }

    @Override
    protected File abstractRealGetRootDir() {
        File appDir = context.getDir(Constants.Others.APP_ROOT_DIR_NAME, Context.MODE_PRIVATE);
        return appDir;
    }
}
