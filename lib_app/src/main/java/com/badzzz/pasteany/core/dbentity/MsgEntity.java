package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.utils.ArrayUtils;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.MapUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.imob.lib.lib_common.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsgEntity {

    private int autoID;
    private String msgID;
    private String msgType;
    //file or normal string content
    private String msgData;
    private String fromDeviceID;
    private int msgLen;
    private long msgTime;

    private Map<String, String> msgSendStates;

    private MsgEntity(int autoID, String msgID, String msgType, String msgData, String fromDeviceID, long time, int msgLen, Map<String, String> msgSendStates) {
        this.autoID = autoID;
        this.msgID = msgID;
        this.msgType = msgType;
        this.msgData = msgData;
        this.fromDeviceID = fromDeviceID;
        this.msgLen = msgLen;
        this.msgTime = time;
        this.msgSendStates = msgSendStates;
    }

    public final static MsgEntity dbQueryItemToEntity(Map<String, String> item) {
        if (item == null) return null;
        try {

            int autoID = Integer.parseInt(item.get(Constants.DB.AUTO_INCREAMENT_ID));

            String msgID = item.get(Constants.DB.KEY.MSGS.MSG_ID);
            String msgType = item.get(Constants.DB.KEY.MSGS.MSG_TYPE);
            String msgData = item.get(Constants.DB.KEY.MSGS.MSG_DATA);
            String fromDeviceID = item.get(Constants.DB.KEY.MSGS.MSG_FROM);
            String toDeviceID = item.get(Constants.DB.KEY.MSGS.MSG_TO);
            int msgLen = Integer.parseInt(item.get(Constants.DB.KEY.MSGS.MSG_LEN));
            String state = item.get(Constants.DB.KEY.MSGS.MSG_STATE);
            long msgTime = Long.parseLong(item.get(Constants.DB.KEY.MSGS.MSG_TIME));

            String[] toIds = toDeviceID.split(Constants.DB.MSG_CHAR_SPLIT);
            String[] states = state.split(Constants.DB.MSG_CHAR_SPLIT);

            return new MsgEntity(autoID, msgID, msgType, msgData, fromDeviceID, msgTime, msgLen, MapUtils.buildMap(toIds, states));
        } catch (Throwable throwable) {
            Logger.e(throwable);
            return null;
        }
    }


    public final static List<MsgEntity> dbQueryListToEntityList(List<Map<String, String>> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<MsgEntity> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            MsgEntity msgEntity = dbQueryItemToEntity(list.get(i));
            if (msgEntity != null) {
                result.add(msgEntity);
            }
        }
        return result;
    }

    private boolean updateState(String toDeviceId, String state) {
        if (toDeviceId == null || toDeviceId.isEmpty() || !msgSendStates.containsKey(toDeviceId) || state == null || state.isEmpty()) {
            return false;
        }
        msgSendStates.put(toDeviceId, state);
        return true;
    }


    private final static Map<String, String> buildInProgressMsgSendStatesWithToDeviceIds(String... toIds) {
        if (toIds == null || toIds.length == 0) {
            return null;
        } else {
            return MapUtils.buildMap(toIds, ArrayUtils.createAndFill(toIds.length, Constants.DB.MSG_TYPE_STATE_MANAGING));
        }
    }


    public final static MsgEntity createMsgEntity(String msgID, String msgType, String msgData, String fromDeviceID, String... toDeviceIds, int msgLen) {
        Map<String, String> msgSendStates = MsgEntity.buildInProgressMsgSendStatesWithToDeviceIds(toDeviceIds);
        return new MsgEntity(-1, msgID, msgType, msgData, fromDeviceID, System.currentTimeMillis(), msgLen, msgSendStates);
    }


    public int getAutoID() {
        return autoID;
    }

    public String getMsgID() {
        return msgID;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getMsgData() {
        return msgData;
    }

    public String getFromDeviceID() {
        return fromDeviceID;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public Map<String, String> getMsgSendStates() {
        return msgSendStates;
    }

    public long getMsgTime() {
        return msgTime;
    }


    public boolean isValid() {
        return msgID != null && !msgID.isEmpty() && msgType != null && !msgType.isEmpty() && msgData != null && !msgData.isEmpty() && fromDeviceID != null && !fromDeviceID.isEmpty() && msgLen > 0 && msgTime > 0 && msgSendStates != null && !msgSendStates.isEmpty();
    }


    public void insertIntoMsgSendingTable(DBManagerWrapper.IDBActionFinishListener listener) {
    }
}
