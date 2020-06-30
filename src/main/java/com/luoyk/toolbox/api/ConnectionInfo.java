package com.luoyk.toolbox.api;

import java.sql.Connection;

public class ConnectionInfo {
    private String host;
    private String port;
    private String username;
    private char[] password;
    private Connection connection;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connections) {
        this.connection = connections;
    }
}
