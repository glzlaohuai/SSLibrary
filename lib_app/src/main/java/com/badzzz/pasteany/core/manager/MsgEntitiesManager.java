package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;

import java.util.ArrayList;
import java.util.List;

public class MsgEntitiesManager {

    private static boolean inited = false;

    private static List<MsgEntity> msgEntities = new ArrayList<>();


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
            }
        });
    }


    private static void loadNextBatch() {

    }


}
