package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.utils.ArrayUtils;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.MapUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MsgEntity {

    private static final String TAG = "MsgEntity";

    private int autoID;
    private String msgID;
    private String msgType;
    //file or normal string content
    private String msgData;
    private String fromDeviceID;
    private int msgLen;
    private long msgTime;
    private String extra;

    private Map<String, Integer> msgSendProgress;
    private Map<String, String> msgSendStates;

    private MsgEntity(int autoID, String msgID, String msgType, String msgData, String fromDeviceID, long time, int msgLen, Map<String, String> msgSendStates, String extra) {
        this.autoID = autoID;
        this.msgID = msgID;
        this.msgType = msgType;
        this.msgData = msgData;
        this.fromDeviceID = fromDeviceID;
        this.msgLen = msgLen;
        this.msgTime = time;
        this.msgSendStates = msgSendStates;
        this.extra = extra;
        if (msgSendStates != null) {
            msgSendProgress = new HashMap<>();
            Set<String> keySet = msgSendStates.keySet();
            for (String key : keySet) {
                msgSendProgress.put(key, 0);
            }
        }
    }

    public void markMsgSendStatesAsFailedIfInSendingStateByToDeviceIDAndUpdateDB(DBManagerWrapper.IDBActionListener idbActionListener, String... deviceIDs) {
        if (deviceIDs != null && deviceIDs.length > 0) {
            Logger.i(TAG, "mark in sending state to failed for deviceIDS: " + Arrays.toString(deviceIDs));
            for (String toDeviceID : deviceIDs) {
                if (msgSendStates.containsKey(toDeviceID) && msgSendStates.get(toDeviceID).equals(Constants.DB.MSG_SEND_STATE_IN_SENDING)) {
                    msgSendStates.put(toDeviceID, Constants.DB.MSG_SEND_STATE_FAILED);
                }
            }
            DBManagerWrapper.getInstance().updateMsgState(this, idbActionListener);
        } else {
            idbActionListener.failed();
        }
    }

    public void markMsgSendStateAndUpdateDB(String toDeviceID, String sendState, DBManagerWrapper.IDBActionListener listener) {
        Logger.i(TAG, "msg send state update, toDeviceID: " + toDeviceID + ", state: " + sendState);
        msgSendStates.put(toDeviceID, sendState);
        DBManagerWrapper.getInstance().updateMsgState(this, listener);
    }


    public void setMsgSendStates(String toDeviceID, String sendState) {
        if (toDeviceID != null && sendState != null && !toDeviceID.isEmpty() && !sendState.isEmpty()) {
            msgSendStates.put(toDeviceID, sendState);
        }
    }


    public final static MsgEntity buildWithDBItem(Map<String, String> item) {
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
            String extra = item.get(Constants.DB.KEY.MSGS.MSG_EXTRA);

            String[] toIds = toDeviceID.split(Constants.DB.SPLIT_CHAR);
            String[] states = state.split(Constants.DB.SPLIT_CHAR);

            return new MsgEntity(autoID, msgID, msgType, msgData, fromDeviceID, msgTime, msgLen, MapUtils.buildMap(toIds, states), extra);
        } catch (Throwable throwable) {
            Logger.e(throwable);
            return null;
        }
    }


    public final static List<MsgEntity> buildWithDBQueryList(List<Map<String, String>> list) {
        if (list == null) {
            return null;
        }

        List<MsgEntity> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            MsgEntity msgEntity = buildWithDBItem(list.get(i));
            if (msgEntity != null) {
                result.add(msgEntity);
            }
        }
        return result;
    }


    public final static MsgEntity buildMsgEntity(String msgID, String msgType, String msgData, String fromDeviceID, int msgLen, String extra, String... toDeviceIds) {
        Map<String, String> msgSendStates = null;
        if (toDeviceIds != null) {
            msgSendStates = MapUtils.buildMap(toDeviceIds, ArrayUtils.createAndFill(toDeviceIds.length, Constants.DB.MSG_SEND_STATE_IN_SENDING));
        }
        return new MsgEntity(Integer.MAX_VALUE, msgID, msgType, msgData, fromDeviceID, System.currentTimeMillis(), msgLen, msgSendStates, extra);
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

    public void setProgressForDeviceID(String toDeviceID, int progress) {
        if (toDeviceID != null) {
            msgSendProgress.put(toDeviceID, progress);
        }
    }

    /**
     *
     * @param toDeviceID
     * @return real progress or -1 if did not the given deviceID
     */
    public int getProgressByDeviceID(String toDeviceID) {
        if (toDeviceID != null && msgSendProgress.containsKey(toDeviceID)) {
            return msgSendProgress.get(toDeviceID);
        }
        return -1;
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

    public String getExtra() {
        return extra;
    }

    public boolean isValid() {
        return msgID != null && !msgID.isEmpty() && msgType != null && !msgType.isEmpty() && msgData != null && !msgData.isEmpty() && fromDeviceID != null && !fromDeviceID.isEmpty() && msgLen > 0 && msgTime > 0 && msgSendStates != null && !msgSendStates.isEmpty();
    }

    public void insertIntoMsgSendingTable(final DBManagerWrapper.IDBActionListener listener) {
        DBManagerWrapper.getInstance().addSendingMsg(this, listener);
    }

    public void removeItemFromMsgSendingTableAfterSendDone(String toDeviceID, DBManagerWrapper.IDBActionListener listener) {
        if (toDeviceID != null && msgSendStates.containsKey(toDeviceID)) {
            DBManagerWrapper.getInstance().removeSendingMsg(msgID, fromDeviceID, toDeviceID, listener);
        } else {
            listener.failed();
        }
    }

    public void insertIntoMsgTable(DBManagerWrapper.IDBActionListener listener) {
        DBManagerWrapper.getInstance().addMsg(this, listener);
    }

    public boolean isSendoutFromThisDevice() {
        return fromDeviceID != null && fromDeviceID.equals(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID());
    }

}
