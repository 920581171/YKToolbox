package com.luoyk.toolbox.api;

import com.luoyk.toolbox.panel.MessageDialog;
import com.luoyk.toolbox.utils.Common;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySQLApi {
    public static List<String> showDataBase(String host) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("show databases");
            try (ResultSet resultSet = statement.executeQuery("show databases")) {
                List<String> list = new ArrayList<>(resultSet.getMetaData().getColumnCount());
                while (resultSet.next()) {
                    list.add(resultSet.getString("database"));
                }
                return list;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_statement_fail"));
            throw new RuntimeException(throwable);
        }
    }

    public static List<String> getTables(String host, String database) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("use " + database);
            try (ResultSet resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables where table_type = 'BASE TABLE' and table_schema = '" + database + "'")) {
                List<String> list = new ArrayList<>(resultSet.getMetaData().getColumnCount());
                while (resultSet.next()) {
                    list.add(resultSet.getString("table_name"));
                }
                return list;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_statement_fail"));
            throw new RuntimeException(throwable);
        }
    }

    public static List<String> getTableFields(String host, String database) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("use " + database);
            try (ResultSet resultSet = statement.executeQuery("select column_name,table_name from information_schema.columns where table_schema = '" + database + "'")) {
                List<String> list = new ArrayList<>(resultSet.getMetaData().getColumnCount());
                while (resultSet.next()) {
                    list.add(resultSet.getString("table_name") + "." + resultSet.getString("column_name"));
                }
                return list;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_statement_fail"));
            throw new RuntimeException(throwable);
        }
    }

    public static List<String> getViews(String host, String database) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("use " + database);
            try (ResultSet resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables where table_type = 'VIEW' and table_schema = '" + database + "'")) {
                List<String> list = new ArrayList<>(resultSet.getMetaData().getColumnCount());
                while (resultSet.next()) {
                    list.add(resultSet.getString("table_name"));
                }
                return list;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_statement_fail"));
            throw new RuntimeException(throwable);
        }
    }

    public static SqlResult selectTable(String host, String database, String table, int page, int size) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("use " + database);
            String sql = "select * from " + table + " limit " + page * size + "," + size;
            long time = System.currentTimeMillis();
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                time = System.currentTimeMillis() - time;
                return convertSqlResult(resultSet).setSql(sql).setTime((int) time);
            }
        } catch (SQLException throwable) {
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_query_error"));
            throw new RuntimeException(throwable);
        }
    }

    public static SqlResult executeSQL(String host, String database, String sql) {
        Connection connection = MySQLConnection.getConnection(host);
        try (Statement statement = connection.createStatement()) {
            statement.execute("use " + database);
            long time = System.currentTimeMillis();
            statement.execute(sql);
            time = System.currentTimeMillis() - time;
            try (ResultSet resultSet = statement.getResultSet()) {
                return convertSqlResult(resultSet).setSql(sql).setTime((int) time);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_sql_execute_fail"));
            throw new RuntimeException(throwables);
        }
    }

    private static SqlResult convertSqlResult(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }
        LinkedList<String[]> linkedList = new LinkedList<>();
        while (resultSet.next()) {
            String[] data = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                data[i - 1] = resultSet.getString(i);
            }
            linkedList.add(data);
        }
        String[][] data = new String[linkedList.size()][];
        linkedList.toArray(data);

        return new SqlResult()
                .setColumnNames(columnNames)
                .setData(data);
    }
}
