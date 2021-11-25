package com.badzzz.pasteany.core.interfaces;

import java.util.List;
import java.util.Map;

public abstract class IDBManager {

    public abstract boolean createTable(String tableName);

    public abstract List<Map<String, String>> query(String sql);

    public abstract boolean executeSql(String sql);






}
