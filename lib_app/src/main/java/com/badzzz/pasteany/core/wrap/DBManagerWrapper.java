package com.badzzz.pasteany.core.wrap;

import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.badzzz.pasteany.core.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * wrap of {@link IDBManager}, invoke all method in worker thread.
 */
public class DBManagerWrapper {

    private IDBManager dbManager;
    private final static DBManagerWrapper instance = new DBManagerWrapper();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public interface IDBActionListener {
        void succeeded(List<Map<String, String>> resultList);

        void failed();
    }


    public static DBManagerWrapper getInstance() {
        return instance;
    }


    private DBManagerWrapper() {
        dbManager = PlatformManagerHolder.get().getAppManager().getDBManager();
    }


    public void addDeviceInfo(final String deviceID, final String deviceName, final String platform, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean insert = dbManager.insert(Constants.DB.TB_CONNECTED_DEVICES, new String[]{Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_ID, Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_NAME, Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_PLATFORM}, new String[]{deviceID, deviceName, platform});
                if (insert) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }

    public void updateDeviceName(final String deviceID, final String deviceName, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int update = dbManager.update(Constants.DB.TB_CONNECTED_DEVICES, new String[]{Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_NAME}, new String[]{deviceName}, new String[]{Constants.DB.KEY.CONNECTED_DEVICES.DEVICE_ID}, new String[]{deviceID});
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

    public void queryAllRelatedMsgs(String deviceID, int fromID, int limit, IDBActionListener listener) {
        String sql = String.format(Constants.DB.SQL_QUERY_DEVICE_RELATED_MSGS, fromID, deviceID, deviceID, limit);
        doQuery(sql, listener);
    }

    public void queryMsgDetail(int autoID, IDBActionListener listener) {
        final String sql = String.format(Constants.DB.SQL_QUERY_MSG_DETAIL, autoID);
        doQuery(sql, listener);
    }

    public void updateMsgState(final int autoID, final String state, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int update = dbManager.update(Constants.DB.TB_MSGS, new String[]{Constants.DB.KEY.MSGS.MSG_STATE}, new String[]{state}, new String[]{Constants.DB.AUTO_INCREAMENT_ID}, new String[]{String.valueOf(autoID)});
                if (update > 0) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
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

    public void addSendingMsg(final String msgID, final String fromDeviceID, final String toDeviceID, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean insert = dbManager.insert(Constants.DB.TB_MSGS_SENDING, new String[]{Constants.DB.KEY.MSGS.MSG_ID, Constants.DB.KEY.MSGS.MSG_FROM, Constants.DB.KEY.MSGS.MSG_TO}, new String[]{msgID, fromDeviceID, toDeviceID});
                if (insert) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }

    public void addMsg(final String msgID, final String fromDeviceID, final String toDeviceID, final String type, final String data, final int len, final String state, final long receiveTime, final long sendTime, final IDBActionListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                boolean insert = dbManager.insert(Constants.DB.TB_MSGS, new String[]{Constants.DB.KEY.MSGS.MSG_ID, Constants.DB.KEY.MSGS.MSG_FROM, Constants.DB.KEY.MSGS.MSG_TO, Constants.DB.KEY.MSGS.MSG_TYPE, Constants.DB.KEY.MSGS.MSG_DATA, Constants.DB.KEY.MSGS.MSG_LEN, Constants.DB.KEY.MSGS.MSG_STATE, Constants.DB.KEY.MSGS.MSG_TIME_RECEIVE, Constants.DB.KEY.MSGS.MSG_TIME_SEND, Constants.DB.KEY.MSGS.MSG_TIME_INSERT}, new String[]{msgID, fromDeviceID, toDeviceID, type, data, String.valueOf(len), state, String.valueOf(receiveTime), String.valueOf(sendTime), String.valueOf(System.currentTimeMillis())});
                if (insert) {
                    listener.succeeded(null);
                } else {
                    listener.failed();
                }
            }
        });
    }


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


}
