package com.badzzz.pasteany.core.api;

import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;

import java.io.File;
import java.util.UUID;

public class MsgCreator {

    private static String createMsgID(String type, String data) {

    }


    public static StringMsg createAPIMsg(String api) {
        StringMsg.create(UUID.randomUUID().toString(), )
    }

    public static StringMsg createNormalStringMsg(String content) {

    }


    public static FileMsg createFileMsg(File file) {

    }


}
