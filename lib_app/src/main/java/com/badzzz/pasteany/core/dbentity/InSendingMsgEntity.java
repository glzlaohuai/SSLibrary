package com.badzzz.pasteany.core.dbentity;

import com.badzzz.pasteany.core.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InSendingMsgEntity {
    private String msgID;
    private String fromID;
    private String toID;


    private InSendingMsgEntity(String msgID, String fromID, String toID) {
        this.msgID = msgID;
        this.fromID = fromID;
        this.toID = toID;
    }


    public static InSendingMsgEntity buildWithDBItem(Map<String, String> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        String msgID = item.get(Constants.DB.KEY.MSGS.MSG_ID);
        String fromID = item.get(Constants.DB.KEY.MSGS.MSG_FROM);
        String toID = item.get(Constants.DB.KEY.MSGS.MSG_TO);

        return new InSendingMsgEntity(msgID, fromID, toID);
    }


    public static List<InSendingMsgEntity> buildWithDBQueryList(List<Map<String, String>> list) {
        if (list == null) return null;

        List<InSendingMsgEntity> result = new ArrayList<>();
        for (Map<String, String> item : list) {
            result.add(buildWithDBItem(item));
        }

        return result;
    }


    public static Map<String, Set<String>> inSendingListToMsgIDToDeviceIDSetMap(List<InSendingMsgEntity> list) {
        if (list == null || list.isEmpty()) return null;
        Map<String, Set<String>> map = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            InSendingMsgEntity inSendingMsgEntity = list.get(i);
            if (inSendingMsgEntity.isValid()) {
                String msgID = inSendingMsgEntity.getMsgID();

                Set<String> toIDSet = map.get(msgID);
                if (toIDSet == null) {
                    toIDSet = new HashSet<>();
                    map.put(msgID, toIDSet);
                }
                toIDSet.add(inSendingMsgEntity.getToID());
            }
        }

        return map;
    }


    public boolean isValid() {
        return msgID != null && !msgID.isEmpty() && fromID != null && !fromID.isEmpty() && toID != null && !toID.isEmpty();
    }


    public String getMsgID() {
        return msgID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getToID() {
        return toID;
    }
}
