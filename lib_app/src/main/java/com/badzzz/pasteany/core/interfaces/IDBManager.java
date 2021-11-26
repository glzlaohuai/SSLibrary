package com.badzzz.pasteany.core.interfaces;

import com.badzzz.pasteany.core.utils.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class IDBManager {

    private static final Set<String> sqlSet = new HashSet<>();

    static {
        sqlSet.add(Constants.DB.SQL_CREATE_TABLE_DEVICES);
        sqlSet.add(Constants.DB.SQL_CREATE_TABLE_MSGS);
    }

    public synchronized List<Map<String, String>> query(String sql) {
        return doQuery(sql);
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

    protected abstract List<Map<String, String>> doQuery(String sql);

    protected abstract int doUpdate(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues);

    protected abstract boolean doInsert(String tableName, String[] keys, String[] values);

    protected abstract boolean doExecuteSql(String sql);

    protected abstract String getDBRootDir();

    protected Set<String> getTableCreateSqls() {
        return IDBManager.sqlSet;
    }


}
