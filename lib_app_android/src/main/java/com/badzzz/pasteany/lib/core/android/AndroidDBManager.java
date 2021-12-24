package com.badzzz.pasteany.lib.core.android;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.common.android.DBUtils;
import com.imob.lib.lib_common.Closer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class AndroidDBManager extends IDBManager {

    private class WorkerDBManager extends SQLiteOpenHelper {
        private static final int DB_VER = 1;

        public WorkerDBManager(@Nullable Context context) {
            super(context, Constants.DB.DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (String sql : getTableCreateSqls()) {
                db.execSQL(sql);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private WorkerDBManager workerDBManager;

    public AndroidDBManager(Context context) {
        workerDBManager = new WorkerDBManager(context);
    }

    @Override
    protected List<Map<String, String>> doQuery(String sql) {
        List<Map<String, String>> result = new ArrayList<>();
        SQLiteDatabase writableDatabase = workerDBManager.getWritableDatabase();
        if (writableDatabase != null && writableDatabase.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = writableDatabase.rawQuery(sql, null);
                if (cursor != null) {
                    int count = cursor.getCount();
                    String[] keys = cursor.getColumnNames();
                    for (int i = 0; i < count; i++) {
                        cursor.moveToPosition(i);
                        Map<String, String> map = new HashMap<>();
                        for (int j = 0; j < keys.length; j++) {
                            map.put(keys[j], cursor.getString(cursor.getColumnIndex(keys[j])));
                        }
                        result.add(map);
                    }
                }
            } finally {
                Closer.close(cursor);
            }
        }
        return result;
    }

    @Override
    protected int doUpdate(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues) {
        SQLiteDatabase database = workerDBManager.getWritableDatabase();
        if (database != null && database.isOpen()) {
            return database.update(tableName, DBUtils.buildContentValues(keys, values), DBUtils.buildClause(whereKeys), whereValues);
        }
        return -1;
    }

    @Override
    protected boolean doInsert(String tableName, String[] keys, String[] values) {

        SQLiteDatabase writableDatabase = workerDBManager.getWritableDatabase();

        if (writableDatabase != null && writableDatabase.isOpen()) {
            long insert = writableDatabase.insert(tableName, null, DBUtils.buildContentValues(keys, values));
            return insert > 0;
        }
        return false;
    }

    @Override
    protected boolean doDelete(String tableName, String[] keys, String[] values) {
        SQLiteDatabase db = workerDBManager.getWritableDatabase();
        if (db != null && db.isOpen()) {
            int delete = db.delete(tableName, DBUtils.buildClause(keys), values);
            return delete > 0;
        }
        return false;
    }

    @Override
    protected boolean doExecuteSql(String sql) {
        SQLiteDatabase writableDatabase = workerDBManager.getWritableDatabase();

        if (writableDatabase != null && writableDatabase.isOpen()) {
            try {
                writableDatabase.execSQL(sql);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    protected String getDBFilePath() {
        throw new UnsupportedOperationException();
    }
}
