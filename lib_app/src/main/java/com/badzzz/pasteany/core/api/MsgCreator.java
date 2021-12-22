package com.badzzz.pasteany.core.api;

import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.Md5;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class MsgCreator {

    /**
     * @param id uniqueID
     * @param type refer to {@link Constants.PeerMsgType} for details
     * @param data has different meanings depending on its type, they are: api、 filePath、 msgMd5
     * @return
     */
    private static String createMsgID(String id, String type, String data) {

        if (id == null || id.isEmpty() || type == null || data == null || type.isEmpty() || data.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.PeerMsgKey.id, id);
        jsonObject.put(Constants.PeerMsgKey.type, type);
        jsonObject.put(Constants.PeerMsgKey.data, data);

        jsonObject.put(Constants.PeerMsgKey.device, PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceDetailInfo());

        return jsonObject.toString();
    }


    private static String createMsgID(String type, String data) {
        return createMsgID(UUID.randomUUID().toString(), type, data);
    }


    public static StringMsg createAPIRequestMsg(String api) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_API_REQUEST, api);
        return StringMsg.create(msgID, "unused");
    }

    public static StringMsg createAPIResponseMsg(String originalRequestMsgID, String response) {
        MsgID msgID = MsgID.buildWithJsonString(originalRequestMsgID);

        String id = msgID.getId();
        String data = msgID.getData();

        String responseMsgID = createMsgID(id, Constants.PeerMsgType.TYPE_API_RESPONSE, data);
        return StringMsg.create(responseMsgID, response);
    }


    public static StringMsg createNormalStringMsg(String content) {
        return createNormalStringMsg(UUID.randomUUID().toString(), content);
    }

    public static StringMsg createNormalStringMsg(String id, String content) {
        String msgID = createMsgID(id, Constants.PeerMsgType.TYPE_STR, Md5.md5(content));
        return StringMsg.create(msgID, content);
    }


    public static FileMsg createFileMsg(String id, String absoluteFilePath, InputStream inputStream) {
        String msgID = createMsgID(id, Constants.PeerMsgType.TYPE_FILE, absoluteFilePath);
        return new FileMsg(msgID, inputStream);


    }

    public static StringMsg createPingMsg(String data) {
        String msgID = createMsgID(Constants.PeerMsgType.TYPE_PING, data);
        return StringMsg.create(msgID, String.valueOf(System.currentTimeMillis()));
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
