package com.imob.lib.sslib.msg;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.IOException;
import java.io.InputStream;

public class Msg {
    private String id;
    private InputStream inputStream;


    public Msg(String id, InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }


    public boolean isValid() {
        return id != null && !id.equals("") && inputStream != null;
    }


    public void destroy() {
        Closer.close(inputStream);
    }

    public String getId() {
        return id;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


    public Chunk readChunk(byte[] bytes) {
        int readed = 0;

        try {
            readed = inputStream.read(bytes);
        } catch (IOException e) {
            Logger.e(e);
        }

        return new Chunk(bytes, readed);
    }


    public int getAvailable() {

        try {
            return inputStream.available();
        } catch (IOException e) {
            Logger.e(e);
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "id='" + id + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
