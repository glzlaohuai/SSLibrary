package com.badzzz.pasteany.lib.core.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.badzzz.pasteany.core.interfaces.IDBManager;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class AndroidDBManager extends IDBManager {

    private class WorkerDBManager extends SQLiteOpenHelper {

        public WorkerDBManager(@Nullable Context context) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    }


    private Context context;
    private WorkerDBManager workerDBManager;

    public AndroidDBManager(Context context) {
        this.context = context;
    }

    @Override
    protected List<Map<String, String>> doQuery(String tableName, String[] whereKeys, String[] whereValues, String orderBy, int limit) {
        return null;
    }

    @Override
    protected int doUpdate(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues) {
        return 0;
    }

    @Override
    protected boolean doInsert(String tableName, String[] keys, String[] values) {
        return false;
    }

    @Override
    protected boolean doExecuteSql(String sql) {
        return false;
    }

    @Override
    protected String getDBRootDir() {
        return null;
    }
}
