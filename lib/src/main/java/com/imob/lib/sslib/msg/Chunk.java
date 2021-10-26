package com.imob.lib.sslib.msg;

public class Chunk {


    private byte[] bytes;
    private int size;

    public Chunk(byte[] bytes, int size) {
        this.bytes = bytes;
        this.size = size;
    }


    public byte[] getBytes() {
        return bytes;
    }

    public int getSize() {
        return size;
    }
}
