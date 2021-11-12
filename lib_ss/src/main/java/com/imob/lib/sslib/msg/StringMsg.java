package com.imob.lib.sslib.msg;

import java.io.ByteArrayInputStream;

public class StringMsg extends Msg {

    private StringMsg(String id, String content) {
        super(id, new ByteArrayInputStream(content.getBytes()));
    }

    public static StringMsg create(String id, String content) {
        if (id == null || id.equals("") || content == null || content.equals("")) {
            return null;
        } else {
            return new StringMsg(id, content);
        }
    }


    @Override
    public byte getMsgType() {
        return Msg.TYPE_NORMAL;
    }
}
