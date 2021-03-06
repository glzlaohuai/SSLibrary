package com.imob.lib.sslib.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileMsg extends Msg {

    public FileMsg(String id, InputStream inputStream) {
        super(id, inputStream);
    }

    private FileMsg(String id, String filePath) throws FileNotFoundException {
        this(id, new FileInputStream(new File(filePath)));
    }


    public static FileMsg create(String id, String filePath) throws FileNotFoundException {
        if (id == null || id.equals("") || filePath == null || filePath.equals("") || !new File(filePath).exists()) {
            return null;
        }

        return new FileMsg(id, filePath);
    }


    @Override
    public byte getMsgType() {
        return Msg.TYPE_NORMAL;
    }
}
