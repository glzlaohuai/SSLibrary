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


    public void saveDeviceInfo(final String deviceID, final String deviceName, final String platform, final IDBActionListener listener) {
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
    }

    public void queryAllRelatedMsgs(String deviceID, int fromID, int limit, IDBActionListener listener) {
        String sql = String.format(Constants.DB.SQL_QUERY_DEVICE_RELATED_MSGS, fromID, deviceID, deviceID, limit);
        doQuery(sql, listener);
    }


    private void doQuery(String sql, IDBActionListener listener) {
        List<Map<String, String>> query = dbManager.query(sql);
        if (query != null) {
            listener.succeeded(query);
        } else {
            listener.failed();
        }
    }


}
