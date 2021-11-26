package com.badzzz.pasteany.core.interfaces;

import java.util.List;
import java.util.Map;

public abstract class IDBManager {

    public synchronized List<Map<String, String>> query(String tableName, String[] whereKeys, String[] whereValues, String orderBy, int limit) {
        return doQuery(tableName, whereKeys, whereValues, orderBy, limit);
    }

    public synchronized int update(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues) {
        return doUpdate(tableName, keys, values, whereKeys, whereValues);
    }

    public synchronized boolean insert(String tableName, String[] keys, String[] values) {
        return doInsert(tableName, keys, values);
    }

    public synchronized void executeSql(String sql) {
        doExecuteSql(sql);
    }


    protected abstract List<Map<String, String>> doQuery(String tableName, String[] whereKeys, String[] whereValues, String orderBy, int limit);

    protected abstract int doUpdate(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues);

    protected abstract boolean doInsert(String tableName, String[] keys, String[] values);

    protected abstract boolean doExecuteSql(String sql);

    protected abstract String getDBRootDir();

}
