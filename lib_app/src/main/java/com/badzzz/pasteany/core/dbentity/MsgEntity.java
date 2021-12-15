package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.utils.ArrayUtils;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.MapUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.imob.lib.lib_common.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            String[] toIds = toDeviceID.split(Constants.DB.SPLIT_CHAR);
            String[] states = state.split(Constants.DB.SPLIT_CHAR);

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


    public final static MsgEntity buildMsgEntity(String msgID, String msgType, String msgData, String fromDeviceID, String... toDeviceIds, int msgLen) {
        Map<String, String> msgSendStates = null;
        if (toDeviceIds != null) {
            msgSendStates = MapUtils.buildMap(toDeviceIds, ArrayUtils.createAndFill(toDeviceIds.length, Constants.DB.MSG_TYPE_STATE_MANAGING));
        }
        return new MsgEntity(-1, msgID, msgType, msgData, fromDeviceID, System.currentTimeMillis(), msgLen, msgSendStates);
    }


    public static class MsgSendStateInDBFormate {
        private String deviceIDs;
        private String sendStates;

        private MsgSendStateInDBFormate(String deviceIDs, String sendStates) {
            this.deviceIDs = deviceIDs;
            this.sendStates = sendStates;
        }


        public String getDeviceIDs() {
            return deviceIDs;
        }

        public String getSendStates() {
            return sendStates;
        }

        public static MsgSendStateInDBFormate buildWithMsgSendStateMap(Map<String, String> sendStateMap) {
            if (sendStateMap == null || sendStateMap.isEmpty()) {
                return null;
            } else {
                Set<String> keySet = sendStateMap.keySet();
                StringBuilder idStringBuilder = new StringBuilder();
                StringBuilder stateStringBuilder = new StringBuilder();

                for (String id : keySet) {
                    String state = sendStateMap.get(id);

                    idStringBuilder.append(id);
                    idStringBuilder.append(Constants.DB.SPLIT_CHAR);

                    stateStringBuilder.append(state);
                    stateStringBuilder.append(Constants.DB.SPLIT_CHAR);
                }


                if (idStringBuilder.length() > 0) {
                    idStringBuilder.deleteCharAt(idStringBuilder.length() - 1);
                }
                if (stateStringBuilder.length() > 0) {
                    stateStringBuilder.deleteCharAt(stateStringBuilder.length() - 1);
                }

                return new MsgSendStateInDBFormate(idStringBuilder.toString(), stateStringBuilder.toString());

            }
        }
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


    public void insertIntoMsgSendingTable(final DBManagerWrapper.IDBActionFinishListener listener) {
        DBManagerWrapper.getInstance().addSendingMsg(this, listener);
    }


    public void insertIntoMsgTable(DBManagerWrapper.IDBActionFinishListener listener) {
        DBManagerWrapper.getInstance().addMsg(this, listener);
    }


}
