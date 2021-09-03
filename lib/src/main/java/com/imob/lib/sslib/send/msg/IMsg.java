package com.imob.lib.sslib.send.msg;

import com.imob.lib.sslib.send.exc.SenderPeerEOFException;
import com.imob.lib.sslib.send.exc.SenderPeerFetchBytesFailedException;

public interface IMsg {

    byte TYPE_FILE = 0x0;
    byte TYPE_STR = 0x1;

    String id();

    void seekTo(long position) throws UnsupportedOperationException;

    byte[] readChunk() throws SenderPeerFetchBytesFailedException, SenderPeerEOFException;

    void close();

    boolean isValid();

    byte getType();

    byte getUserDefiniedType();

    String getName();

}
