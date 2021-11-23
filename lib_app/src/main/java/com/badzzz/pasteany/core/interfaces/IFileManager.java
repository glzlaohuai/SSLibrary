package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Md5;

import java.io.File;

public abstract class IFileManager {

    protected abstract File abstractRealGetRootDir();

    public File getRootDir() {
        File rootDir = abstractRealGetRootDir();
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        return rootDir;
    }


    public File getDirWithDeviceID(String deviceID) {
        if (deviceID == null || deviceID.isEmpty()) {
            return null;
        } else {
            File rootDir = getRootDir();
            File deviceDir = new File(rootDir, Md5.md5(deviceID));

            if (!deviceDir.exists()) {
                deviceDir.mkdirs();
            }
            return deviceDir;
        }
    }


    public File getDirWithDeviceIDAndMsgID(String deviceID, String msgID) {

        File deviceDir = getDirWithDeviceID(deviceID);
        File msgDir = new File(deviceDir, Md5.md5(msgID));

        if (!msgDir.exists()) {
            msgDir.mkdirs();
        }

        return msgDir;
    }


 

}
