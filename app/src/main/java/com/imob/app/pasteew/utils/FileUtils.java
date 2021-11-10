package com.imob.app.pasteew.utils;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static void deleteAllFiles(File file) {
        if (file != null && file.exists()) {
            if (!file.isFile()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null && subFiles.length > 0) {
                    for (File subFile : subFiles) {
                        if (subFile.isDirectory()) {
                            deleteAllFiles(subFile);
                        } else {
                            try {
                                subFile.delete();
                            } catch (Throwable th2) {
                            }
                        }
                    }
                }
            }
            try {
                file.delete();
            } catch (Throwable th) {
            }
        }
    }


    public static boolean inputToOutput(InputStream inputStream, OutputStream outputStream) {
        byte[] bytes = new byte[1024];

        try {
            int readed;
            while ((readed = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, readed);

                if (readed < bytes.length) {
                    break;
                }
            }
            outputStream.flush();
        } catch (Exception e) {
            Logger.e(e);
            return false;
        } finally {
            Closer.close(inputStream);
            Closer.close(outputStream);
        }
        return true;
    }


}
