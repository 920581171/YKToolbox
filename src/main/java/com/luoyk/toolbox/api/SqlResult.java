package com.luoyk.toolbox.api;

public class SqlResult {
    private String sql;
    private int time;
    private String[] columnNames;
    private String[][] data;

    public String getSql() {
        return sql;
    }

    public SqlResult setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public int getTime() {
        return time;
    }

    public SqlResult setTime(int time) {
        this.time = time;
        return this;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public SqlResult setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public String[][] getData() {
        return data;
    }

    public SqlResult setData(String[][] data) {
        this.data = data;
        return this;
    }
}
