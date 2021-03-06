package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.dbentity.DeviceEntity;
import com.badzzz.pasteany.core.dbentity.InSendingMsgEntity;
import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.utils.SSThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * wrap of {@link IDBManager}, invoke all method in worker thread.
 */
public class DBManagerWrapper {

    private static final String TAG = "DBManagerWrapper";

    private IDBManager dbManager;
    private final static DBManagerWrapper instance = new DBManagerWrapper();

    private ExecutorService executorService = Executors.newSingleThreadExecutor(SSThreadFactory.build("dbwrapper"));


    public static class IDBActionListenerAdapter implements IDBActionListener {

        @Override
        public void succeeded(List<Map<String, String>> resultList) {

        }

        @Override
        public void failed() {

        }
    }


    public interface IDBActionListener {
        void succeeded(List<Map<String, String>> resultList);

        void failed();
    }


    public abstract static class IDBActionFinishListener implements IDBActionListener {
        private List<Map<String, String>> resultList;

        public abstract void onFinished();

        @Override
        public void succeeded(List<Map<String, String>> resultList) {
            this.resultList = resultList;
            onFinished();
        }

        @Override
        public void failed() {
            onFinished();
        }

        public List<Map<String, String>> getResultList() {
            return resultList == null ? new ArrayList<Map<String, String>>() : resultList;
        }
    }


    public static DBManagerWrapper getInstance() {
        return instance;
    }


    private DBManagerWrapper() {
        dbManager = PlatformManagerHolder.get().getAppManager().getDBManager();
    }

    public void addDeviceInfo(final DeviceEntity deviceEntity, final IDBActionListener listener) {
        if (deviceEntity.isValid()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    boolean result = dbManager.executeSql(String.format(Constants.DB.SQL_INSERT_OR_UPDATE_DEVICE, deviceEntity.getId(), deviceEntity.getName(), deviceEntity.getPlatform()));
                    if (result) {
                        listener.succeeded(null);
                    } else {
                        listener.failed();
                    }
                }
            });
        } else {
            listener.failed();
        }

    }

    public void updateDeviceName(final String deviceID, final String deviceName, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int update = dbManager.update(Constants.DB.TB_DEVICES, new String[]{Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_NAME}, new String[]{deviceName}, new String[]{Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_ID}, new String[]{deviceID});
                if (update > 0) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }

    public void queryAllMsgs(int fromID, int limit, IDBActionListener listener) {
        String sql = String.format(Constants.DB.SQL_QUERY_MSGS, fromID, limit);
        doQuery(sql, listener);
    }

    public void queryAllSendingMsgs(IDBActionListener listener) {
        String sql = Constants.DB.SQL_QUERY_ALL_SENDING_MSGS;
        doQuery(sql, listener);
    }


    public void queryAllDevices(IDBActionListener listener) {
        String sql = Constants.DB.SQL_QUERY_ALL_DEVICES;
        doQuery(sql, listener);
    }

    public void queryAllSendingMsgsAndMarkThemAsFailed(final IDBActionFinishListener listener) {

        Logger.i(TAG, "query all sending msgs and mark them as send failed if it's still in sending progress");

        queryAllSendingMsgs(new IDBActionFinishListener() {
            @Override
            public void onFinished() {
                List<InSendingMsgEntity> inSendingMsgEntities = InSendingMsgEntity.buildWithDBQueryList(getResultList());

                if (inSendingMsgEntities != null && !inSendingMsgEntities.isEmpty()) {
                    final Map<String, Set<String>> msgIDToDeviceIDsMap = InSendingMsgEntity.inSendingListToMsgIDToDeviceIDSetMap(inSendingMsgEntities);
                    Logger.i(TAG, "got sending msgs < msgID -> toDeviceIDSet >: " + msgIDToDeviceIDsMap);


                    if (msgIDToDeviceIDsMap != null && !msgIDToDeviceIDsMap.isEmpty()) {
                        IDBActionFinishListener wrapListener = new IDBActionFinishListener() {
                            boolean everFailed = false;
                            int count = msgIDToDeviceIDsMap.keySet().size();

                            @Override
                            public void failed() {
                                super.failed();
                                everFailed = true;
                            }

                            @Override
                            public void onFinished() {
                                count--;
                                if (count == 0) {
                                    if (everFailed) {
                                        listener.failed();
                                    } else {
                                        listener.succeeded(null);
                                    }
                                }
                            }
                        };
                        loopUpdateMsgSendStateToFailed(msgIDToDeviceIDsMap, wrapListener);
                    } else {
                        listener.failed();
                    }
                } else {
                    listener.succeeded(null);
                }
            }
        });
    }


    private void loopUpdateMsgSendStateToFailed(final Map<String, Set<String>> map, final IDBActionFinishListener finishListener) {
        if (map == null || map.isEmpty()) {
            finishListener.onFinished();
            return;
        }

        final String msgID = map.keySet().iterator().next();
        DBManagerWrapper.getInstance().removeSendingMsg(msgID, new IDBActionListenerAdapter());

        queryMsgDetail(msgID, new IDBActionFinishListener() {
            @Override
            public void onFinished() {
                List<Map<String, String>> resultList = getResultList();
                if (resultList == null || resultList.isEmpty()) {
                    Logger.i(TAG, "got no msg detail before try to update msg state, something went wrong. msgID is: " + msgID);
                    finishListener.failed();
                } else {
                    Logger.i(TAG, "got msg detail for msgID: " + msgID + ", update its send state.");
                    MsgEntity msgEntity = MsgEntity.buildWithDBItem(resultList.get(0));
                    msgEntity.markMsgSendStatesAsFailedIfInSendingStateByToDeviceIDAndUpdateDB(finishListener, map.get(msgID).toArray(new String[0]));
                }
                map.remove(msgID);
                loopUpdateMsgSendStateToFailed(map, finishListener);
            }
        });


    }


    public void queryAllRelatedMsgs(String deviceID, int fromID, int limit, IDBActionListener listener) {
        String sql = String.format(Constants.DB.SQL_QUERY_DEVICE_RELATED_MSGS, fromID, deviceID, deviceID, limit);
        doQuery(sql, listener);
    }

    public void queryMsgDetail(String msgID, IDBActionListener listener) {
        final String sql = String.format(Constants.DB.SQL_QUERY_MSG_DETAIL, msgID);
        doQuery(sql, listener);
    }

    public void updateMsgState(final MsgEntity msgEntity, final IDBActionListener listener) {

        if (msgEntity == null || !msgEntity.isValid()) {
            listener.failed();
        } else {
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    MsgEntity.MsgSendStateInDBFormate sendStateInDBFormate = MsgEntity.MsgSendStateInDBFormate.buildWithMsgSendStateMap(msgEntity.getMsgSendStates());
                    int update = dbManager.update(Constants.DB.TB_MSGS, new String[]{Constants.DB.KEY.MSGS.MSG_STATE, Constants.DB.KEY.MSGS.MSG_TO}, new String[]{sendStateInDBFormate.getSendStates(), sendStateInDBFormate.getDeviceIDs()}, new String[]{Constants.DB.KEY.MSGS.MSG_ID}, new String[]{msgEntity.getMsgID()});
                    if (update > 0) {
                        listener.succeeded(null);
                    } else {
                        listener.failed();
                    }
                }
            });
        }
    }


    private void doQuery(final String sql, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Map<String, String>> resultList = dbManager.query(sql);
                if (resultList != null) {
                    listener.succeeded(resultList);
                } else {
                    listener.failed();
                }
            }
        });
    }


    private static String toDeviceIDSetToString(Set<String> idSet) {
        StringBuilder sb = new StringBuilder();
        for (String id : idSet) {
            sb.append(id);
            sb.append(Constants.DB.SPLIT_CHAR);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }


    public void addSendingMsg(final MsgEntity msgEntity, final IDBActionListener listener) {
        if (msgEntity.isValid()) {
            Logger.i(TAG, "add into sending msg table: " + msgEntity);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    IDBActionFinishListener wrapListener = new IDBActionFinishListener() {
                        int count = msgEntity.getMsgSendStates().keySet().size();
                        boolean everFailed = false;

                        @Override
                        public void failed() {
                            everFailed = true;
                        }

                        @Override
                        public void onFinished() {
                            count--;
                            if (count == 0) {
                                if (everFailed) {
                                    listener.failed();
                                } else {
                                    listener.succeeded(null);
                                }
                            }
                        }
                    };

                    for (String toID : msgEntity.getMsgSendStates().keySet()) {
                        boolean insert = dbManager.insert(Constants.DB.TB_MSGS_SENDING, new String[]{Constants.DB.KEY.MSGS.MSG_ID, Constants.DB.KEY.MSGS.MSG_FROM, Constants.DB.KEY.MSGS.MSG_TO}, new String[]{msgEntity.getMsgID(), msgEntity.getFromDeviceID(), toID});

                        if (insert) {
                            wrapListener.succeeded(null);
                        } else {
                            wrapListener.failed();
                        }
                    }
                }
            });

        } else {
            listener.failed();
        }
    }

    public void addMsg(final MsgEntity msgEntity, final IDBActionListener listener) {
        if (msgEntity.isValid()) {
            Logger.i(TAG, "add into msg table: " + msgEntity);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    MsgEntity.MsgSendStateInDBFormate msgSendStateInDBFormate = MsgEntity.MsgSendStateInDBFormate.buildWithMsgSendStateMap(msgEntity.getMsgSendStates());
                    boolean insert = dbManager.insert(Constants.DB.TB_MSGS, new String[]{Constants.DB.KEY.MSGS.MSG_ID, Constants.DB.KEY.MSGS.MSG_FROM, Constants.DB.KEY.MSGS.MSG_TO, Constants.DB.KEY.MSGS.MSG_TYPE, Constants.DB.KEY.MSGS.MSG_DATA, Constants.DB.KEY.MSGS.MSG_LEN, Constants.DB.KEY.MSGS.MSG_STATE, Constants.DB.KEY.MSGS.MSG_TIME, Constants.DB.KEY.MSGS.MSG_EXTRA}, new String[]{msgEntity.getMsgID(), msgEntity.getFromDeviceID(), msgSendStateInDBFormate.getDeviceIDs(), msgEntity.getMsgType(), msgEntity.getMsgData(), String.valueOf(msgEntity.getMsgLen()), msgSendStateInDBFormate.getSendStates(), String.valueOf(msgEntity.getMsgTime()), msgEntity.getExtra()});
                    if (insert) {
                        listener.succeeded(null);
                    } else {
                        listener.failed();
                    }
                }
            });

        } else {
            listener.failed();
        }
    }


    /**
     * remove specific item
     * @param msgID
     * @param fromDeviceID
     * @param toDeviceID
     * @param listener
     */
    public void removeSendingMsg(final String msgID, final String fromDeviceID, final String toDeviceID, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean delete = dbManager.delete(Constants.DB.TB_MSGS_SENDING, new String[]{Constants.DB.KEY.MSGS.MSG_ID, Constants.DB.KEY.MSGS.MSG_FROM, Constants.DB.KEY.MSGS.MSG_TO}, new String[]{msgID, fromDeviceID, toDeviceID});
                if (delete) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }

    /**
     * remove all items related by msgID
     * @param msgID
     * @param listener
     */
    public void removeSendingMsg(final String msgID, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean delete = dbManager.delete(Constants.DB.TB_MSGS_SENDING, new String[]{Constants.DB.KEY.MSGS.MSG_ID}, new String[]{msgID});
                if (delete) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }


}
