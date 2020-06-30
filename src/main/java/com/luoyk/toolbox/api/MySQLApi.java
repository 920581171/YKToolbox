package com.luoyk.toolbox.api;

import com.luoyk.toolbox.panel.MessageDialog;
import com.luoyk.toolbox.utils.Common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
}
