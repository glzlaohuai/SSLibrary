package com.badzzz.pasteany.core.api;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

public class MsgCreator {

    private static String createMsgID(String type, String data) {

        if (type == null || data == null || type.isEmpty() || data.isEmpty()) return null;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.PeerMsgKey.id, UUID.randomUUID().toString());
        jsonObject.put(Constants.PeerMsgKey.type, type);
        jsonObject.put(Constants.PeerMsgKey.data, data);

        return jsonObject.toJSONString();
    }


    public static StringMsg createAPIMsg(String api) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_API, api);
        return StringMsg.create(msgID, "unused");
    }

    public static StringMsg createNormalStringMsg(String content) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_STR, content);
        return StringMsg.create(msgID, content);
    }

    public static FileMsg createFileMsg(File file) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_FILE, file.getAbsolutePath());
        try {
            return FileMsg.create(msgID, file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            Logger.e(e);
            return null;
        }
    }


}
