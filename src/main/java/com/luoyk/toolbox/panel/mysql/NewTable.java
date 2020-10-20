package com.luoyk.toolbox.panel.mysql;

import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.MysqlWord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NewTable {

    private final String[] columnNames = new String[]{
            Common.language.getString("mysql_new_table_field_name"),
            Common.language.getString("mysql_new_table_data_type"),
            Common.language.getString("mysql_new_table_data_length"),
            Common.language.getString("mysql_new_table_precision"),
            Common.language.getString("mysql_new_table_primary_key"),
            Common.language.getString("mysql_new_table_not_null"),
            Common.language.getString("mysql_new_table_comment")
    };

    private static Object[] getBaseField() {
        return new Object[]{"", "", 0, 0, false, false, ""};
    }

    private final Object[][] data = new Object[][]{
            getBaseField()
    };

    private final DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames);

    private JTable table;
    private JPanel panel;
    private JButton addField;
    private JButton removeField;
    private JButton up;
    private JButton down;
    private JButton checkSQL;

    private final String host;
    private final String dataBase;

    public NewTable(String host, String dataBase) {
        this.host = host;
        this.dataBase = dataBase;
        init();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void init() {

        initButton();

        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(Common.fontSize + Common.fontSize / 2);
        table.setModel(defaultTableModel);

        table.getColumnModel().getColumn(1).setCellEditor(newJComboBox());
        table.getColumnModel().getColumn(4).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(4).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        table.getColumnModel().getColumn(5).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(5).setCellRenderer(table.getDefaultRenderer(Boolean.class));
    }

    private void initButton() {
        addField.setText(Common.language.getString("mysql_new_table_add_field"));
        removeField.setText(Common.language.getString("mysql_new_table_remove_field"));
        up.setText(Common.language.getString("mysql_new_table_up"));
        down.setText(Common.language.getString("mysql_new_table_down"));
        checkSQL.setText(Common.language.getString("mysql_new_table_check_sql"));

        addField.addActionListener(e -> {
            defaultTableModel.addRow(getBaseField());
            table.invalidate();
        });

        removeField.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            defaultTableModel.removeRow(selectedRow);
            table.invalidate();
        });

        up.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != 0) {
                defaultTableModel.moveRow(selectedRow, selectedRow, selectedRow - 1);
                table.changeSelection(selectedRow - 1, 6, false, false);
            }
        });

        down.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != table.getRowCount() - 1) {
                defaultTableModel.moveRow(selectedRow, selectedRow, selectedRow + 1);
                table.changeSelection(selectedRow + 1, 6, false, false);
            }
        });

        //todo 单独显示SQL
        checkSQL.addActionListener(e -> {
            String sql = getSQL();
            //todo 执行SQL
//            MySQLApi.executeSQL(host, dataBase, sql);
        });
    }

    //生成SQL语句
    public String getSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE `t_table` (\n");

        StringBuilder pk = null;

        int rowCount = table.getModel().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnNames.length; j++) {
                String columnName = columnNames[j];
                String text = String.valueOf(table.getModel().getValueAt(i, j));
                if (columnName.equals(Common.language.getString("mysql_new_table_field_name"))) {
                    builder.append("`").append(text).append("` ");
                } else if (columnName.equals(Common.language.getString("mysql_new_table_data_type"))) {
                    builder.append(text).append(" ");
                } else if (columnName.equals(Common.language.getString("mysql_new_table_data_length"))) {
                    builder.append("(").append(text).append(")").append(" ");
                } else if (columnName.equals(Common.language.getString("mysql_new_table_precision"))) {
                } else if (columnName.equals(Common.language.getString("mysql_new_table_primary_key"))) {
                    if (pk == null) {
                        pk = new StringBuilder("PRIMARY KEY (`");
                    }
                    pk.append(table.getModel().getValueAt(i, 0)).append("`,");
                } else if (columnName.equals(Common.language.getString("mysql_new_table_not_null"))) {
                    builder.append("NOT NULL ");
                } else if (columnName.equals(Common.language.getString("mysql_new_table_comment"))) {
                    builder.append("COMMENT '").append(text).append("'");
                }
            }
            builder.append(",\n");

        }

        if (pk != null) {
            pk.deleteCharAt(pk.length() - 1);
            pk.append(")\n");
            builder.append(pk);
            builder.append(")");
        }

        System.out.println(builder.toString());

        return builder.toString();
    }

    public DefaultCellEditor newJComboBox() {
        JComboBox<String> jComboBox = new JComboBox<>();
        jComboBox.setMaximumRowCount(8);
        for (String s : MysqlWord.typeList()) {
            jComboBox.addItem(s);
        }
        return new DefaultCellEditor(jComboBox);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, BorderLayout.CENTER);
        table = new JTable();
        scrollPane1.setViewportView(table);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.add(panel1, BorderLayout.NORTH);
        addField = new JButton();
        addField.setText("Button");
        panel1.add(addField);
        removeField = new JButton();
        removeField.setText("Button");
        panel1.add(removeField);
        up = new JButton();
        up.setText("Button");
        panel1.add(up);
        down = new JButton();
        down.setText("Button");
        panel1.add(down);
        checkSQL = new JButton();
        checkSQL.setText("Button");
        panel1.add(checkSQL);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
