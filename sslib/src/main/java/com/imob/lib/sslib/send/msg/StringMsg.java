package com.imob.lib.sslib.send.msg;

import android.text.TextUtils;

import com.imob.lib.sslib.send.exc.SenderPeerEOFException;
import com.imob.lib.sslib.send.exc.SenderPeerFetchBytesFailedException;

public class StringMsg implements IMsg {

    private String string;

    public StringMsg(String msg) {
        this.string = msg;
    }

    @Override
    public String id() {
        return string.hashCode() + "";
    }

    @Override
    public void seekTo(long position) throws UnsupportedOperationException {

    }

    @Override
    public byte[] readChunk() throws SenderPeerFetchBytesFailedException, SenderPeerEOFException {
        byte[] bytes = string.getBytes();
        throw new SenderPeerEOFException(bytes, bytes.length);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(string);
    }

    @Override
    public byte getType() {
        return TYPE_STR;
    }

    @Override
    public byte getUserDefiniedType() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
