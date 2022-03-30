package com.imob.lib.sslib.msg;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.IOException;
import java.io.InputStream;

public abstract class Msg {

    public static final byte TYPE_NORMAL = 0x0;
    public static final byte TYPE_CONFIRM = 0x1;
    public static final byte TYPE_PING = 0x2;

    public static final String MSG_ID_PREFIX = "ss_msgid_";

    private String id;
    private InputStream inputStream;
    private Exception exception;

    private boolean canceled = false;
    private boolean isPingRelatedMsg = false;


    public boolean isPingRelatedMsg() {
        return isPingRelatedMsg;
    }

    public void setPingRelatedMsg(boolean pingRelatedMsg) {
        isPingRelatedMsg = pingRelatedMsg;
    }

    public Msg(String id, InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isValid() {
        return id != null && !id.equals("") && ((getMsgType() == TYPE_NORMAL && inputStream != null) || getMsgType() != TYPE_NORMAL);
    }

    public abstract byte getMsgType();

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
        int readed;
        Exception exception = null;
        if (canceled) {
            return new Chunk(null, Chunk.SIZE_CANCELED, null);
        }

        try {
            readed = inputStream.read(bytes);
        } catch (IOException e) {
            exception = e;
            Logger.e(e);
            readed = Chunk.SIZE_ERROR;
        }

        return new Chunk(bytes, readed, exception);
    }


    public int getAvailable() {
        if (inputStream != null) {
            try {
                return inputStream.available();
            } catch (IOException e) {
                Logger.e(e);
                this.exception = e;
            }
        }
        return 0;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "Msg{" + "id='" + id + '\'' + ", inputStream=" + inputStream + '}';
    }
}
