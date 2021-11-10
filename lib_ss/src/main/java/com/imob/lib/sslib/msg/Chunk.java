package com.imob.lib.sslib.msg;

public class Chunk {


    private byte[] bytes;
    private int size;

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

    public int getSize() {
        return size;
    }
}
