package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.FileUtils;
import com.badzzz.pasteany.core.utils.Md5;
import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class IFileManager {


    public interface FileChunkSaveListener {
        void onSuccess(File chunkFile);

        void onFailed();
    }


    public interface FileMergeListener {
        void onSuccess(File finalFile);

        void onFailed();
    }


    private final static class ChunkFileComparator implements Comparator<File> {

        private int getRangeStart(File file) {
            if (file == null) {
                return 0;
            }
            try {
                return Integer.parseInt(file.getName().split("-")[0]);
            } catch (Throwable e) {
                Logger.e(e);
            }

            return 0;
        }


        @Override
        public int compare(File file, File t1) {
            return getRangeStart(t1) - getRangeStart(file);
        }
    }


    private final static ChunkFileComparator chunkFileComparator = new ChunkFileComparator();

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

    public void saveFileChunk(String deviceID, final String msgID, final int chunkSize, int soFar, int available, final byte[] bytes, final FileChunkSaveListener listener) {

        int chunkFrom = soFar - chunkSize;
        int chunkTo = soFar;

        final String fileName = chunkFrom + "-" + chunkTo;

        final File chunkFile = new File(getDirWithDeviceIDAndMsgID(deviceID, msgID), fileName);
        if (FileUtils.writeBytesToFile(chunkFile, bytes, 0, chunkSize)) {
            listener.onSuccess(chunkFile);
        } else {
            listener.onFailed();
        }
    }

    public void mergeAllFileChunks(String deviceID, String msgID, String fileName, FileMergeListener listener) {
        File dir = getDirWithDeviceIDAndMsgID(deviceID, msgID);

        File[] files = dir.listFiles();
        if (files == null) {
            listener.onFailed();
        } else {
            List<File> fileList = Arrays.asList(files);
            Collections.sort(fileList, chunkFileComparator);

            if (FileUtils.mergeFiles(fileList, new File(dir, fileName))) {
                listener.onSuccess(new File(dir, fileName));
            } else {
                listener.onFailed();
            }
        }
    }


}
