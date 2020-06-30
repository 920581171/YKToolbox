package com.luoyk.toolbox.api;

import com.luoyk.toolbox.panel.MessageDialog;
import com.luoyk.toolbox.utils.Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

public class MySQLConnection {

    private final static HashMap<String, ConnectionInfo> HASH_MAP = new HashMap<>();

    public static Set<String> getConnectionHosts() {
        return HASH_MAP.keySet();
    }

    public static boolean newConnection(ConnectionInfo connectionInfo) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + connectionInfo.getHost() + ":" + connectionInfo.getPort() +
                            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    connectionInfo.getUsername(), new String(connectionInfo.getPassword()));
            final ConnectionInfo getInfo = HASH_MAP.get(connectionInfo.getHost());
            if (getInfo == null) {
                connectionInfo.setConnection(connection);
                HASH_MAP.put(connectionInfo.getHost(), connectionInfo);
            } else {
                getInfo.setConnection(connection);
            }
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_fail"));
            e.printStackTrace();
        }
        return false;
    }

    public static void closeConnection(String host) {
        try {
            ConnectionInfo connectionInfo = HASH_MAP.get(host);
            if (connectionInfo.getConnection() != null) {
                connectionInfo.getConnection().close();
                connectionInfo.setConnection(null);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_close_connection_fail"));
        }
    }

    public static void removeConnectionInfo(String host) {
        try {
            ConnectionInfo connectionInfo = HASH_MAP.get(host);
            if (connectionInfo.getConnection() != null) {
                connectionInfo.getConnection().close();
            }
            HASH_MAP.remove(host);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_close_connection_fail"));
        }
    }

    public static Connection getConnection(String host) {
        ConnectionInfo connectionInfo = HASH_MAP.get(host);
        if (connectionInfo == null) {
            MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_statement_fail"));
            throw new NullPointerException();
        }
        if (connectionInfo.getConnection() == null) {
            newConnection(connectionInfo);
        }
        return connectionInfo.getConnection();
    }
}
