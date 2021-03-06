package com.badzzz.pasteany.core.utils;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FileUtils {

    /**
     * @param file
     * @param bytes
     * @param offset
     * @param len
     * @return true - successfully | false - the opposite
     */
    public static boolean writeBytesToFile(File file, byte[] bytes, int offset, int len) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, false);
            fileOutputStream.write(bytes, offset, len);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closer.close(fileOutputStream);
        }
        return false;
    }

    /**
     * merge all files into one final file, and during the merging process, the file segments will be deleted after be merged.
     * @param fileList
     * @param finalFile
     * @return
     */
    public static boolean mergeFiles(List<File> fileList, File finalFile) {
        if (fileList == null || fileList.isEmpty()) {
            return false;
        }
        if (!finalFile.exists()) {
            try {
                finalFile.createNewFile();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(finalFile, false);

            byte[] chunk = new byte[1024];
            for (int i = 0; i < fileList.size(); i++) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(fileList.get(i));
                    int len = 0;
                    while ((len = fis.read(chunk)) != -1) {
                        fos.write(chunk, 0, len);
                    }
                    fileList.get(i).delete();
                } catch (Exception e) {
                    Logger.e(e);
                    return false;
                } finally {
                    Closer.close(fis);
                }

            }
        } catch (Throwable e) {
            Logger.e(e);
            return false;
        } finally {
            Closer.close(fos);
        }
        return true;
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
