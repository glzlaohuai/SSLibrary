package com.badzzz.pasteany.core.api.msg;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class MsgID {

    private String id;
    private String type;
    private String data;
    private String device;


    public final static MsgID buildWithJsonString(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String id = jsonObject.getString(Constants.PeerMsgKey.id);
            String type = jsonObject.getString(Constants.PeerMsgKey.type);
            String data = jsonObject.getString(Constants.PeerMsgKey.data);
            String device = jsonObject.getString(Constants.PeerMsgKey.device);

            return new MsgID(id, type, data, device);
        } catch (JSONException e) {
            Logger.e(e);
        }
        return null;
    }


    private MsgID(String id, String type, String data, String device) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.device = device;
    }

    public String getDevice() {
        return device;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }


    @Override
    public String toString() {
        return "MsgID{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
