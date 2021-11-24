package com.badzzz.pasteany.lib.core.android;

import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;

public class AndroidPlatformManagerHolder {

    public static AndroidPlatformManager get() {
        return (AndroidPlatformManager) (PlatformManagerHolder.get());
    }


}
