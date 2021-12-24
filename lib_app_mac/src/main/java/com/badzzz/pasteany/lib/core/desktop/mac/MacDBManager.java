package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.IDBManager;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacDBManager extends IDBManager {

    private static final String TAG = "MacDBManager";

    //query timeout, seconds unit
    private static final int TIMEOUT_QUERY = 10;


    public MacDBManager() {
        super();
        createDBFileIfNotExists();
        try {
            createTableIfNotExists();
        } catch (SQLException throwables) {
            Logger.e(throwables);
            throw new RuntimeException("table create failed");
        }
    }

    private void createTableIfNotExists() throws SQLException {
        for (String sql : getTableCreateSqls()) {
            getStatement().executeUpdate(sql);
        }
    }

    private void createDBFileIfNotExists() {
        File file = new File(getDBFilePath());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }


    private Statement getStatement() {
        Statement statement = null;
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getDBFilePath());
            if (connection != null) {
                statement = connection.createStatement();
                statement.setQueryTimeout(TIMEOUT_QUERY);
            }
            return statement;
        } catch (Throwable throwables) {
            Logger.e(throwables);
            closeStatement(statement);
        }
        return null;
    }


    @Override
    protected List<Map<String, String>> doQuery(String sql) {
        Statement statement = getStatement();
        if (statement != null) {
            try {
                ResultSet resultSet = statement.executeQuery(sql);
                try {
                    if (resultSet != null) {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        if (metaData != null) {
                            int columnCount = metaData.getColumnCount();
                            String[] keys = new String[columnCount];
                            for (int i = 1; i <= columnCount; i++) {
                                keys[i - 1] = metaData.getColumnName(i);
                            }

                            List<Map<String, String>> result = new ArrayList<>();

                            while (resultSet.next()) {
                                Map<String, String> item = new HashMap<>();
                                for (String key : keys) {
                                    item.put(key, resultSet.getString(key));
                                }
                                result.add(item);
                            }

                            return result;
                        }
                    }
                } finally {
                    Closer.close(resultSet);
                    closeStatement(statement);
                }
            } catch (SQLException throwables) {
                Logger.e(throwables);
            }
        }
        return null;
    }


    /**
     * WHERE key1 = 'value1', AND key2 = 'value2'
     * @param keys
     * @param values
     * @return
     */
    private static String buildWhereClause(String[] keys, String[] values) {

        if (keys == null || values == null || keys.length != values.length) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("WHERE ");
        //WHERE clause
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i] + " = '" + values[i] + "'");

            if (i != keys.length - 1) {
                sb.append(" AND ");
            }
        }

        return sb.toString();
    }


    private static String buildUpdateClause(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues) {
        if (tableName == null || keys == null || values == null || keys.length != values.length || whereKeys == null || whereValues == null || whereKeys.length != whereValues.length) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE ");
        sb.append(tableName);
        sb.append(" SET ");


        //SET clause
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i] + " = '" + values[i] + "'");

            //not the last one
            if (i != keys.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(" " + buildWhereClause(whereKeys, whereValues));
        sb.append(";");

        return sb.toString();
    }


    private static String buildInsertClause(String[] keys, String[] values) {
        if (keys == null || values == null || keys.length != values.length) {
            return null;
        }

        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i]);
            if (i != keys.length - 1) {
                sb.append(",");
            }
        }

        sb.append(") ");


        sb.append("VALUES(");

        for (int i = 0; i < values.length; i++) {
            sb.append("'");
            sb.append(values[i]);
            sb.append("'");

            if (i != values.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(");");
        return sb.toString();
    }


    @Override
    protected int doUpdate(String tableName, String[] keys, String[] values, String[] whereKeys, String[] whereValues) {
        Statement statement = getStatement();
        if (statement != null) {
            try {
                return statement.executeUpdate(buildUpdateClause(tableName, keys, values, whereKeys, whereValues));
            } catch (SQLException throwables) {
                Logger.e(throwables);
            } finally {
                closeStatement(statement);
            }
        }
        return 0;
    }


    private final static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                Connection connection = statement.getConnection();
                Closer.close(connection);
            } catch (SQLException throwables) {
                Logger.e(throwables);
            }
        }
        Closer.close(statement);
    }

    @Override
    protected boolean doInsert(String tableName, String[] keys, String[] values) {
        String insertClause = buildInsertClause(keys, values);
        String insertSql = "INSERT INTO " + tableName + insertClause;

        Statement statement = getStatement();
        if (statement != null) {
            try {
                statement.execute(insertSql);
                return true;
            } catch (SQLException throwables) {
                Logger.e(throwables);
            } finally {
                closeStatement(statement);
            }
        }

        return false;
    }

    @Override
    protected boolean doDelete(String tableName, String[] keys, String[] values) {
        StringBuilder sb = new StringBuilder("DELETE FROM TABLE " + tableName);
        sb.append(" " + buildWhereClause(keys, values));
        sb.append(";");

        Statement statement = getStatement();
        try {
            statement.execute(sb.toString());
            return true;
        } catch (SQLException throwables) {
            Logger.e(throwables);
        } finally {
            closeStatement(statement);
        }

        return false;
    }

    @Override
    protected boolean doExecuteSql(String sql) {

        Statement statement = getStatement();
        if (statement != null) {
            try {
                statement.execute(sql);
                return true;
            } catch (SQLException throwables) {
                Logger.e(throwables);
            } finally {
                closeStatement(statement);
            }
        }
        return false;
    }

    @Override
    protected String getDBFilePath() {
        return MacConstants.DB_FILE;
    }
}
