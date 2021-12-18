package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MsgEntitiesManager {

    private static boolean inited = false;
    private static boolean everLoaded = false;

    private static List<MsgEntity> msgEntities = new ArrayList<>();

    private static Set<IMsgEntityBatchLoadListener> listenerSet = new HashSet<>();
    private static IMsgEntityBatchLoadListener noActionListener = new IMsgEntityBatchLoadListener() {
        @Override
        public void onFinished() {

        }
    };
    private static Comparator<MsgEntity> msgEntityComparator = new Comparator<MsgEntity>() {
        @Override
        public int compare(MsgEntity o1, MsgEntity o2) {
            return o1.getAutoID() - o2.getAutoID();
        }
    };


    private static final int MSG_BATCH_LOAD_SIZE = 1;

    public interface IMsgEntityBatchLoadListener {
        void onFinished();
    }


    public synchronized final static void init() {
        if (!inited) {
            inited = true;
            doInit();
        }
    }


    private final static void doInit() {
        DBManagerWrapper.getInstance().queryAllSendingMsgsAndMarkThemAsFailed(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {
                //query first batch msgs
                loadNextBatch(noActionListener);
            }
        });
    }

    public static boolean hasEverLoaded() {
        return everLoaded;
    }


    public static List<MsgEntity> getAllMsgEntities() {
        return msgEntities;
    }


    public static void loadNextBatch(IMsgEntityBatchLoadListener listener) {
        listenerSet.add(listener);
        int firstID = msgEntities.size() == 0 ? Integer.MAX_VALUE : msgEntities.get(0).getAutoID();
        DBManagerWrapper.getInstance().queryAllMsgs(firstID, MSG_BATCH_LOAD_SIZE, new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {
                everLoaded = true;
                List<MsgEntity> queryedList = MsgEntity.buildWithDBQueryList(getResultList());
                if (queryedList != null) {
                    Collections.sort(queryedList, msgEntityComparator);
                }
                Set<IMsgEntityBatchLoadListener> tmpListenerSet = new HashSet<>(listenerSet);
                for (IMsgEntityBatchLoadListener each : tmpListenerSet) {
                    each.onFinished();
                }
                listenerSet.removeAll(tmpListenerSet);
            }
        });

    }


    // TODO: 2021/12/19  
    public static void sendStringMsgToPeers(String msgID, String content, Set<String> tagSet) {

    }

    public static void sendFileMsgToPeers(String msgID, String file, Set<String> tagSet) {

    }


}
