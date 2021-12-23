package com.imob.app.pasteew.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;

public class FileUtils {

    public static class FileInfo {
        private String name;
        private int size;

        public boolean isValid() {
            return !name.isEmpty() && size > 0;
        }

        public FileInfo(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }
    }

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

    public static FileInfo retrieveFileInfoFromContentUri(Context context, Uri uri) {
        if (context == null || uri == null) return null;
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                int nameCursorIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeCursorIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                String name = cursor.getString(nameCursorIndex);
                int fileSize = cursor.getInt(sizeCursorIndex);

                return new FileInfo(name, fileSize);
            }
        }
        return null;
    }


}
