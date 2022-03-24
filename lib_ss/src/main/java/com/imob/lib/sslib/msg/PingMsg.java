package com.imob.lib.sslib.msg;

public class PingMsg extends Msg {

    public PingMsg(String id) {
        super(id, null);
    }

    @Override
    public byte getMsgType() {
        return Msg.TYPE_PING;
    }

    public static PingMsg build(String id) {
        return new PingMsg(id);
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("unable to cancel ping msg");
    }
}
