package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MsgEntity {

    private int autoID;
    private String msgID;
    private String msgType;
    private String msgData;
    private String fromDeviceID;
    private List<String> toDeviceIDList;
    private int msgLen;
    private List<String> stateList;
    private long msgTime;


    private MsgEntity(int autoID, String msgID, String msgType, String msgData, String fromDeviceID, List<String> toDeviceIDList, int msgLen, List<String> stateList, long time) {
        this.autoID = autoID;
        this.msgID = msgID;
        this.msgType = msgType;
        this.msgData = msgData;
        this.fromDeviceID = fromDeviceID;
        this.toDeviceIDList = toDeviceIDList;
        this.msgLen = msgLen;
        this.stateList = stateList;
        this.msgTime = time;
    }

    public static final String buildSegmentsToSingleDBFormate(String... segments) {

        if (segments == null || segments.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            sb.append(segments[i]);
            if (i != segments.length - 1) {
                sb.append(Constants.DB.MSG_CHAR_SPLIT);
            }
        }

        return sb.toString();
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

            String[] toDeviceIDs = toDeviceID.split(Constants.DB.MSG_CHAR_SPLIT);
            String[] states = state.split(Constants.DB.MSG_CHAR_SPLIT);

            return new MsgEntity(autoID, msgID, msgType, msgData, fromDeviceID, Arrays.asList(toDeviceIDs), msgLen, Arrays.asList(states), msgTime);
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

    private boolean setStateForDeviceID(String toDeviceID, String state) {
        if (toDeviceID == null || state == null) return false;
        int index = toDeviceIDList.indexOf(toDeviceID);
        if (index == -1) return false;

        stateList.remove(index);
        stateList.add(index, state);

        return true;
    }


    public synchronized boolean markMsgStateForDeviceID(String toDeviceID, String state) {
        return setStateForDeviceID(toDeviceID, state);
    }


    public final static MsgEntity buildSendingOrReceivingMessage(String msgID, String msgType, String msgData, String fromDeviceID, List<String> toDeviceIDList, int msgLen) {
        List<String> stateList = new ArrayList<>();
        if (toDeviceIDList != null && toDeviceIDList.size() > 0) {
            for (int i = 0; i < toDeviceIDList.size(); i++) {
                stateList.add(Constants.DB.MSG_TYPE_STATE_SENDING);
            }
        } else {
            return null;
        }

        return new MsgEntity(-1, msgID, msgType, msgData, fromDeviceID, toDeviceIDList, msgLen, stateList, System.currentTimeMillis());
    }


    public String getDBFormateMsgState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stateList.size(); i++) {
            sb.append(stateList.get(i));

            if (i != stateList.size() - 1) {
                sb.append(Constants.DB.MSG_CHAR_SPLIT);
            }
        }

        return sb.toString();
    }


}
