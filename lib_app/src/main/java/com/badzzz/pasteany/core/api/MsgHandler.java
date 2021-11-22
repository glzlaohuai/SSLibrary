package com.badzzz.pasteany.core.api;

import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.Md5;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

public class MsgHandler {








    /**
     *
     * @param type refer to {@link Constants.PeerMsgType} for details
     * @param data has different meanings depending on its type, they are: api、 filePath、 msgMd5
     * @return
     */
    private static String createMsgID(String type, String data) {

        if (type == null || data == null || type.isEmpty() || data.isEmpty()) return null;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.PeerMsgKey.id, UUID.randomUUID().toString());
        jsonObject.put(Constants.PeerMsgKey.type, type);
        jsonObject.put(Constants.PeerMsgKey.data, data);

        return jsonObject.toString();
    }


    public static StringMsg createAPIMsg(String api) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_API, api);
        return StringMsg.create(msgID, "unused");
    }

    public static StringMsg createNormalStringMsg(String content) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_STR, Md5.md5(content));
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
