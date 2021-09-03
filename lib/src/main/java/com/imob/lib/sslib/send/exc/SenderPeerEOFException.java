package com.imob.lib.sslib.send.exc;

import java.io.IOException;

public class SenderPeerEOFException extends IOException {

    private byte[] tailBytes;
    private int size;

    public SenderPeerEOFException() {
    }

    public SenderPeerEOFException(byte[] tailBytes, int size) {
        this.tailBytes = tailBytes;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public byte[] getTailBytes() {
        return tailBytes;
    }
}
