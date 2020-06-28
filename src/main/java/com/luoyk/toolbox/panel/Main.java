package com.luoyk.toolbox.panel;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.stream.Stream;

public class Main implements ActionListener {

    public static final String MYSQL = "mysql";
    public static final String REDIS = "redis";
    public static final String WEB_SOCKET = "webSocket";

    private JFrame frame;
    private JPanel panel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton mysql;
    private JButton redis;
    private JButton webSocket;

    public void run(String[] args) {
        Common.setLanguage(Locale.CHINA);
        Common.setFont();
        frame = new JFrame(Common.language.getString("title"));
        this.init();
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1280, 720));
        frame.pack();
        frame.setVisible(true);
    }

    public void init() {
        this.initMenu();
        this.initLeftPanel();
        this.initRightPanel();
    }

    private void initMenu() {
        JMenuBar jMenuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(Common.language.getString("menu_file"));
        JMenu fileMenuSettingsMenu = new JMenu(Common.language.getString("menu_file_menu_settings"));
        JMenu fileMenuSettingsMenuFontName = new JMenu(Common.language.getString("menu_file_menu_settings_font_name"));
        JMenu fileMenuSettingsMenuFontSize = new JMenu(Common.language.getString("menu_file_menu_settings_font_size"));

        JMenu fileMenuSettingsMenuLanguageMenu = new JMenu(Common.language.getString("menu_file_menu_settings_language"));
        fileMenuSettingsMenu.add(fileMenuSettingsMenuLanguageMenu);
        JMenuItem englishItem = new JMenuItem("English");
        JMenuItem chineseItem = new JMenuItem("中文");
        englishItem.addActionListener(this);
        chineseItem.addActionListener(this);
        fileMenuSettingsMenuLanguageMenu.add(englishItem);
        fileMenuSettingsMenuLanguageMenu.add(chineseItem);

        Stream.of(Common.language.getString("menu_file_menu_setting_font_name_default"), "宋体", "黑体", "楷体", "仿宋", "微软雅黑").forEach(s -> {
            JMenuItem jMenuItem = new JMenuItem(s);
            fileMenuSettingsMenuFontName.add(jMenuItem);
            jMenuItem.addActionListener(this);
        });
        fileMenuSettingsMenu.add(fileMenuSettingsMenuFontName);

        JMenuItem defaultSize = new JMenuItem(Common.language.getString("menu_file_menu_settings_font_size_default"));
        fileMenuSettingsMenuFontSize.add(defaultSize);
        defaultSize.addActionListener(this);
        for (int i = 12; i <= 32; i = i + 2) {
            JMenuItem jMenuItem = new JMenuItem(String.valueOf(i));
            fileMenuSettingsMenuFontSize.add(jMenuItem);
            jMenuItem.addActionListener(this);
        }
        fileMenuSettingsMenu.add(fileMenuSettingsMenuFontSize);

        fileMenu.add(fileMenuSettingsMenu);

        JMenu helpMenu = new JMenu(Common.language.getString("menu_help"));
        JMenuItem aboutItem = new JMenuItem(Common.language.getString("menu_help_item_about"));
        helpMenu.add(aboutItem);

        jMenuBar.add(fileMenu);
        jMenuBar.add(helpMenu);
        this.frame.setJMenuBar(jMenuBar);
    }

    private void initLeftPanel() {
        Insets insets = new Insets(0, 0, 0, 0);

        ImageIcon mysql = ImageLoader.load("mysql.jpg").size64();
        ImageIcon redis = ImageLoader.load("redis.jpg").size64();
        ImageIcon webSocket = ImageLoader.load("websocket.jpg").size64();

        this.mysql.setText(null);
        this.mysql.setIcon(mysql);
        this.mysql.setMargin(insets);
        this.mysql.setBorderPainted(false);

        this.redis.setText(null);
        this.redis.setMargin(insets);
        this.redis.setIcon(redis);
        this.redis.setBorderPainted(false);

        this.webSocket.setText(null);
        this.webSocket.setMargin(insets);
        this.webSocket.setIcon(webSocket);
        this.webSocket.setBorderPainted(false);

        this.mysql.setActionCommand(MYSQL);
        this.mysql.addActionListener(this);
        this.redis.setActionCommand(REDIS);
        this.redis.addActionListener(this);
        this.webSocket.setActionCommand("webSocket");
        this.webSocket.addActionListener(this);
    }

    private void initRightPanel() {
        rightPanel.setBackground(Color.WHITE);

        MySQL mySQL = new MySQL();
        Redis redis = new Redis();
        WebSocekt webSocekt = new WebSocekt();

        rightPanel.add(MYSQL, mySQL.getPanel());
        rightPanel.add(REDIS, redis.getPanel());
        rightPanel.add("webSocket", webSocekt.getPanel());
    }

    private void revalidate() {
        Common.setFont();
        this.initMenu();
        this.frame.setTitle(Common.language.getString("title"));
        this.frame.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case MYSQL:
                ((CardLayout) rightPanel.getLayout()).show(rightPanel, MYSQL);
                break;
            case REDIS:
                ((CardLayout) rightPanel.getLayout()).show(rightPanel, REDIS);
                break;
            case WEB_SOCKET:
                ((CardLayout) rightPanel.getLayout()).show(rightPanel, WEB_SOCKET);
                break;
            case "English":
                Common.setLanguage(Locale.ENGLISH);
                break;
            case "中文":
                Common.setLanguage(Locale.CHINA);
                break;
            case "宋体":
            case "黑体":
            case "楷体":
            case "仿宋":
            case "微软雅黑":
                Common.fontName = e.getActionCommand();
                break;
            default:
                if (Common.language.getString("menu_file_menu_setting_font_name_default").equals(e.getActionCommand())) {
                    Common.fontName = "微软雅黑";
                } else if (Common.language.getString("menu_file_menu_settings_font_size_default").equals(e.getActionCommand())) {
                    Common.fontSize = 16;
                } else {
                    Common.fontSize = Integer.parseInt(e.getActionCommand());
                }
                break;
        }
        revalidate();
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
        panel.setEnabled(true);
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(leftPanel, BorderLayout.WEST);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        leftPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mysql = new JButton();
        mysql.setText("Button");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(mysql, gbc);
        redis = new JButton();
        redis.setText("Button");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(redis, gbc);
        webSocket = new JButton();
        webSocket.setText("Button");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(webSocket, gbc);
        final Spacer spacer1 = new Spacer();
        leftPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rightPanel = new JPanel();
        rightPanel.setLayout(new CardLayout(0, 0));
        panel.add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
