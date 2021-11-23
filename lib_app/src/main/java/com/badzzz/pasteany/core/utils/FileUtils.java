package com.badzzz.pasteany.core.utils;

import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileUtils {

    private final static ExecutorService writeService=new 

    public static interface FileWriteListener {
        void onSucceeded(File file);

        void onFailed();
    }


    /**
     *
     *write or overwrite bytes to a file
     * @param file
     * @param bytes
     * @param offset
     * @param size
     */
    public static void writeBytesToFile(File file, byte[] bytes, int offset, int size, FileWriteListener listener) {
        boolean exists = true;
        if (!file.exists()) {
            exists = false;
            try {
                exists = file.createNewFile();
            } catch (IOException e) {
                Logger.e(e);
                exists = false;
            }
        }


        if (exists) {





        } else {
            //due to file create failed
            listener.onFailed();
        }


    }


}
