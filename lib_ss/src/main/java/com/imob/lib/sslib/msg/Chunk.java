package com.imob.lib.sslib.msg;

public class Chunk {

    public static final int STATE_OK = 0x0;
    public static final int STATE_EOF = 0x1;
    public static final int STATE_CANCELED = 0x2;
    public static final int STATE_ERROR = 0x3;

    private byte[] bytes;
    private int size;

    public static final int SIZE_EOF = -1;
    public static final int SIZE_ERROR = 0;
    public static final int SIZE_CANCELED = -2;

    private Exception exception;

    public Chunk(byte[] bytes, int size, Exception exception) {
        this.bytes = bytes;
        this.size = size;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public byte[] getBytes() {
        return bytes;
    }


    /**
     * real byte size or read state flag
     * @return
     */
    public int getSize() {
        return size;
    }


    public int getState() {
        int state;
        switch (size) {
            //error
            case SIZE_ERROR:
                state = STATE_ERROR;
                break;
            //eof
            case SIZE_EOF:
                state = STATE_EOF;
                break;
            //canceled
            case SIZE_CANCELED:
                state = STATE_CANCELED;
                break;
            //others
            default:
                state = size > 0 ? STATE_OK : STATE_ERROR;
                break;
        }
        return state;
    }

}
