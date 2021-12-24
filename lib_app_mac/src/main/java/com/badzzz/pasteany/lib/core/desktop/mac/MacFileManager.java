package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IFileManager;

import java.io.File;

public class MacFileManager extends IFileManager {

    @Override
    protected File abstractRealGetRootDir() {
        return new File(MacConstants.FILE_ROOT_DIR);
    }
}
