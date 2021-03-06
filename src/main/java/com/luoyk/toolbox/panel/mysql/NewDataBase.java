package com.luoyk.toolbox.panel.mysql;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoyk.toolbox.api.MySQLApi;
import com.luoyk.toolbox.api.SqlResult;
import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NewDataBase extends JDialog {
    private JPanel panel;
    private JTextField dbName;
    private JLabel dbNameLabel;
    private JLabel characterLebel;
    private JLabel collationLabel;
    private JComboBox<String> collation;
    private JButton confirm;
    private JButton cancel;
    private JComboBox<String> character;

    private String host;

    private Consumer<Boolean> confirmAction;

    private NewDataBase() {
    }

    private NewDataBase(String host, Consumer<Boolean> confirmAction) {
        this.host = host;
        this.confirmAction = confirmAction;
        init();
    }

    public static void create(String host, Consumer<Boolean> confirmAction) {
        NewDataBase newDataBase = new NewDataBase(host, confirmAction);
    }

    public void init() {
        this.dbNameLabel.setText(Common.language.getString("mysql_new_database_db_name"));
        this.characterLebel.setText(Common.language.getString("mysql_new_database_character"));
        this.collationLabel.setText(Common.language.getString("mysql_new_database_db_name"));

        /*
         * +----------+---------------------------------+---------------------+--------+
         * | Charset  | Description                     | Default collation   | Maxlen |
         * +----------+---------------------------------+---------------------+--------+
         */

        SqlResult characterSetResult = MySQLApi.showCharacterSet(host);

        Map<String, String[]> characterMap = Arrays.stream(characterSetResult.getData())
                .collect(Collectors.toMap(strings -> strings[0], Function.identity()));

        for (String[] character : characterSetResult.getData()) {
            this.character.addItem(character[0]);
        }

        /*
         * +----------------------------+----------+-----+---------+----------+---------+---------------+
         * | Collation                  | Charset  | Id  | Default | Compiled | Sortlen | Pad_attribute |
         * +----------------------------+----------+-----+---------+----------+---------+---------------+
         */
        this.character.addItemListener(e -> {
            SqlResult collationResult = MySQLApi.showCollation(host, (String) e.getItem());
            for (String[] collations : collationResult.getData()) {
                this.collation.addItem(collations[0]);
            }
            this.collation.setSelectedItem(characterMap.get((String) e.getItem())[2]);
            this.pack();
        });

        this.confirm.addActionListener(e -> {
            if (confirmAction != null) {
                boolean b = MySQLApi.createDataBase(host, dbName.getText(),
                        (String) character.getSelectedItem(),
                        (String) collation.getSelectedItem());
                confirmAction.accept(b);
                NewDataBase.this.dispose();
            }
        });
        this.cancel.addActionListener(e -> this.dispose());

        this.setIconImage(ImageLoader.load(ImageLoader.MYSQL_CONNECTION).size32().getImage());
        this.setTitle(Common.language.getString("mysql_tree_popup_menu_item_new_database"));
        this.setContentPane(panel);
        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(panel1, BorderLayout.SOUTH);
        confirm = new JButton();
        confirm.setText("确定");
        panel1.add(confirm);
        cancel = new JButton();
        cancel.setText("取消");
        panel1.add(cancel);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 3, new Insets(12, 12, 12, 12), -1, -1));
        panel.add(panel2, BorderLayout.CENTER);
        dbNameLabel = new JLabel();
        dbNameLabel.setText("数据库名");
        panel2.add(dbNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        dbName = new JTextField();
        panel2.add(dbName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        characterLebel = new JLabel();
        characterLebel.setText("字符集名");
        panel2.add(characterLebel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        collationLabel = new JLabel();
        collationLabel.setText("排序方式");
        panel2.add(collationLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        collation = new JComboBox();
        panel2.add(collation, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        character = new JComboBox();
        panel2.add(character, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
