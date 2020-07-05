package com.luoyk.toolbox.panel.mysql;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoyk.toolbox.api.ConnectionInfo;
import com.luoyk.toolbox.api.MySQLConnection;
import com.luoyk.toolbox.panel.MessageDialog;
import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NewConnection extends JDialog {
    private JTextField host;
    private JTextField port;
    private JTextField username;
    private JPasswordField password;
    private JPanel panel;
    private JButton confirm;
    private JButton cancel;
    private JButton test;
    private JLabel hostLabel;
    private JLabel passwordLabel;
    private JLabel portLabel;
    private JLabel usernameLabel;
    private JLabel title;

    private Consumer<ConnectionInfo> confirmAction;

    public static NewConnection newDialog(Consumer<ConnectionInfo> confirmAction) {
        NewConnection newConnection = new NewConnection();
        newConnection.confirmAction = confirmAction;
        newConnection.init();
        return newConnection;
    }

    private NewConnection() {
    }

    private void init() {
        title.setText(Common.language.getString("mysql_button_new_connection"));
        hostLabel.setText(Common.language.getString("mysql_dialog_new_connection_host_label"));
        portLabel.setText(Common.language.getString("mysql_dialog_new_connection_port_label"));
        usernameLabel.setText(Common.language.getString("mysql_dialog_new_connection_username_label"));
        passwordLabel.setText(Common.language.getString("mysql_dialog_new_connection_password_label"));


        confirm.setText(Common.language.getString("dialog_button_confirm"));
        confirm.addActionListener(e -> {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setHost(host.getText());
            connectionInfo.setPort(port.getText());
            connectionInfo.setUsername(username.getText());
            connectionInfo.setPassword(password.getPassword());

            confirmAction.accept(connectionInfo);

            this.dispose();
        });
        cancel.setText(Common.language.getString("dialog_button_cancel"));
        cancel.addActionListener(e -> this.dispose());

        test.setText(Common.language.getString("dialog_button_test"));
        test.addActionListener(e -> {
            ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setHost(host.getText());
            connectionInfo.setPort(port.getText());
            connectionInfo.setUsername(username.getText());
            connectionInfo.setPassword(password.getPassword());

            if (MySQLConnection.TestConnection(connectionInfo)) {
                MessageDialog.newDialog(Common.language.getString("mysql_dialog_new_connection_success"));
            }
        });

        this.setIconImage(ImageLoader.load(ImageLoader.MYSQL_CONNECTION).size32().getImage());
        this.setTitle(Common.language.getString("mysql_button_new_connection"));
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
        panel1.setLayout(new GridLayoutManager(3, 4, new Insets(12, 12, 12, 12), -1, -1));
        panel.add(panel1, BorderLayout.CENTER);
        usernameLabel = new JLabel();
        usernameLabel.setText("账号");
        panel1.add(usernameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        username = new JTextField();
        username.setText("");
        panel1.add(username, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        hostLabel = new JLabel();
        hostLabel.setText("主机");
        panel1.add(hostLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        host = new JTextField();
        host.setText("");
        panel1.add(host, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(144, -1), null, 0, false));
        portLabel = new JLabel();
        portLabel.setText("端口");
        panel1.add(portLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        port = new JTextField();
        port.setText("");
        panel1.add(port, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(48, -1), null, 0, false));
        password = new JPasswordField();
        panel1.add(password, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        passwordLabel = new JLabel();
        passwordLabel.setText("密码");
        panel1.add(passwordLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(8, 8, 8, 8), -1, -1));
        panel.add(panel2, BorderLayout.SOUTH);
        test = new JButton();
        test.setText("Button");
        panel2.add(test, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        cancel = new JButton();
        cancel.setText("Button");
        panel2.add(cancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        confirm = new JButton();
        confirm.setText("Button");
        panel2.add(confirm, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(panel3, BorderLayout.NORTH);
        title = new JLabel();
        title.setText("新连接");
        panel3.add(title);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
