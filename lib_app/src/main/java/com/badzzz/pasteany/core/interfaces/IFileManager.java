package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Md5;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class IFileManager {

    private final static ExecutorService saveService = Executors.newCachedThreadPool();

    //用于在incomingMsgReadSuccess之后，判断看是否还有未生成完的
    private final static Map<String, Set<String>> savingChunkNameMap = new HashMap<>();

    public interface FileChunkSaveListener {
        void onSuccess(File chunkFile);

        void onFailed();
    }

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


    private final static void addToSavingChunkFileNameMap(String msgID, String fileName) {
        if (!savingChunkNameMap.containsKey(msgID)) {
            savingChunkNameMap.put(msgID, new HashSet<String>());
        }
        savingChunkNameMap.get(msgID).add(fileName);
    }


    private final static void removeFromSavingChunkFileNameMap(String msgID, String fileName) {
        if (savingChunkNameMap.containsKey(msgID)) {
            savingChunkNameMap.get(msgID).remove(fileName);
        }
    }


    public void saveFileChunk(String deviceID, final String msgID, final int chunkSize, int soFar, int available, final byte[] bytes, final FileChunkSaveListener listener) {

        int chunkFrom = soFar - chunkSize;
        int chunkTo = soFar;

        final String fileName = chunkFrom + "-" + chunkTo;

        final File chunkFile = new File(getDirWithDeviceIDAndMsgID(deviceID, msgID), fileName);

        IFileManager.addToSavingChunkFileNameMap(msgID, fileName);

        saveService.execute(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(chunkFile, false);
                    fos.write(bytes, 0, chunkSize);

                    listener.onSuccess(chunkFile);

                } catch (IOException e) {
                    Logger.e(e);

                    listener.onFailed();
                } finally {
                    Closer.close(fos);
                    IFileManager.removeFromSavingChunkFileNameMap(msgID, fileName);
                }
            }
        });
    }


}
