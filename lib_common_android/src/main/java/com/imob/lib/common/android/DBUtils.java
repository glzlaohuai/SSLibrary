package com.imob.lib.common.android;

import android.content.ContentValues;

public class DBUtils {

    public static ContentValues buildContentValues(String[] keys, String[] values) {
        ContentValues contentValues = new ContentValues();

        if (keys == null || values == null || keys.length != values.length) {
            return contentValues;
        }

        for (int i = 0; i < keys.length; i++) {
            contentValues.put(keys[i], values[i]);
        }
        return contentValues;
    }

    public static String buildClause(String[] columnNames) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i] + "=? and ");
        }
        sb.delete(sb.length() - 4, sb.length());
        return sb.toString();
    }


}
