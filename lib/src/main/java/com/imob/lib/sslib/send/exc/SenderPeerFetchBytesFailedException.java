package com.imob.lib.sslib.send.exc;

import java.io.IOException;

public class SenderPeerFetchBytesFailedException extends IOException {

    public SenderPeerFetchBytesFailedException() {
    }

    public SenderPeerFetchBytesFailedException(String message) {
        super(message);
    }

    public SenderPeerFetchBytesFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SenderPeerFetchBytesFailedException(Throwable cause) {
        super(cause);
    }
}
