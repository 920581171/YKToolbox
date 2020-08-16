package com.luoyk.toolbox.panel.mysql;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoyk.toolbox.api.MySQLApi;
import com.luoyk.toolbox.api.MySQLConnection;
import com.luoyk.toolbox.api.SqlResult;
import com.luoyk.toolbox.panel.MessageDialog;
import com.luoyk.toolbox.panel.Refresh;
import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.ImageLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;


public class MySQL implements Refresh {

    public static final int TREE_PATH_COUNT_ROOT = 1;
    public static final int TREE_PATH_COUNT_HOST = 2;
    public static final int TREE_PATH_COUNT_DATABASE = 3;
    public static final int TREE_PATH_COUNT_TABLE = 4;
    public static final int TREE_PATH_COUNT_SELECT_TABLE = 5;

    public static final JMenuItem close = new JMenuItem(Common.language.getString("mysql_tree_popup_menu_item_close"));
    public static final JMenuItem newDatabase = new JMenuItem(Common.language.getString("mysql_tree_popup_menu_item_new_database"));
    public static final JMenuItem dropDataBase = new JMenuItem(Common.language.getString("mysql_tree_popup_menu_item_drop_database"));
    public static final JMenuItem newTable = new JMenuItem(Common.language.getString("mysql_tree_popup_menu_item_new_table"));
    public static final JMenuItem dropTable = new JMenuItem(Common.language.getString("mysql_tree_popup_menu_item_drop_table"));

    public static ActionListener closeAction;
    public static ActionListener newDatabaseAction;
    public static ActionListener dropDataBaseAction;
    public static ActionListener newTableAction;
    public static ActionListener dropTableAction;

    private JPanel panel;
    private JPanel center;
    private JButton newConnection;
    private JButton newSQL;
    private JPanel west;
    private JTree connectionTree;
    private JTabbedPane tabbedPane;
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

    private TabPanel firstTab;
    private TabPanel lastTab;
    private DataTable dataTable;

    private MouseAdapter connectionTreeMouseListener;

    public MySQL() {
        initTool();
        refresh();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void initTool() {

        Insets insets = new Insets(0, 0, 0, 0);
        Dimension dimension = new Dimension(64, 64);

        newConnection.setText(null);
        newConnection.setMargin(insets);
        newConnection.setPreferredSize(dimension);
        newConnection.setMinimumSize(dimension);
        newConnection.setMaximumSize(dimension);
        newConnection.setIcon(ImageLoader.load(ImageLoader.MYSQL_CONNECTION).size48());
        newConnection.setBorderPainted(false);
        newConnection.setFocusPainted(false);
        newConnection.setBackground(Color.WHITE);

        newConnection.addActionListener(e ->
                NewConnection.newDialog(connectionInfo -> {
                    if (MySQLConnection.newConnection(connectionInfo)) {
                        DefaultMutableTreeNode database = new DefaultMutableTreeNode(connectionInfo.getHost());
                        rootNode.add(database);
                        treeModel.reload(rootNode);
                    }
                })
        );

        newSQL.setText(null);
        newSQL.setToolTipText(Common.language.getString("mysql_button_new_sql"));
        newSQL.setMargin(insets);
        newSQL.setPreferredSize(dimension);
        newSQL.setMinimumSize(dimension);
        newSQL.setMaximumSize(dimension);
        newSQL.setIcon(ImageLoader.load(ImageLoader.MYSQL_SQL).size48());
        newSQL.setBorderPainted(false);
        newSQL.setFocusPainted(false);
        newSQL.setBackground(Color.WHITE);
    }

    private void initTree() {
        west.setPreferredSize(new Dimension(256, 256));
        connectionTree.removeMouseListener(connectionTreeMouseListener);
        connectionTreeMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TreePath path = connectionTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
                        connectionTree.setSelectionPath(path);

                        switch (path.getPathCount()) {
                            case TREE_PATH_COUNT_HOST:
                                showConnectionTreePopMenu(e, close, newDatabase);
                                break;
                            case TREE_PATH_COUNT_DATABASE:
                                showConnectionTreePopMenu(e, close, dropDataBase);
                                break;
                            case TREE_PATH_COUNT_TABLE:
                                showConnectionTreePopMenu(e, close, newTable);
                                break;
                            case TREE_PATH_COUNT_SELECT_TABLE:
                                showConnectionTreePopMenu(e, close, dropTable);
                                break;
                            default:
                                break;
                        }

                        popupMenuAction(path);
                    }

                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                        switch (path.getPathCount()) {
                            case TREE_PATH_COUNT_HOST:
                                hostDoubleClick(path);
                                break;
                            case TREE_PATH_COUNT_DATABASE:
                                break;
                            case TREE_PATH_COUNT_TABLE:
                                tableDoubleClick(path);
                                break;
                            case TREE_PATH_COUNT_SELECT_TABLE:
                                selectTableDoubleClick(path);
                                break;
                            default:
                                break;
                        }
                    }
                }
                super.mouseClicked(e);
            }
        };

        rootNode.removeAllChildren();
        MySQLConnection.getConnectionHosts().forEach(hostName -> rootNode.add(new DefaultMutableTreeNode(hostName)));
        treeModel.reload(rootNode);
        connectionTree.setRootVisible(false);
        connectionTree.setModel(treeModel);
        connectionTree.addMouseListener(connectionTreeMouseListener);
    }

    private void hostDoubleClick(TreePath path) {
        DefaultMutableTreeNode host = (DefaultMutableTreeNode) path.getLastPathComponent();
        //判断该节点是否展开过
        if (!connectionTree.hasBeenExpanded(path)) {
            String hostStr = (String) host.getUserObject();
            final List<String> list = MySQLApi.showDataBase(hostStr);
            list.forEach(databaseName -> {
                DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(databaseName);
                databaseNode.add(new DefaultMutableTreeNode(Common.language.getString("mysql_tree_path_2_tables")));
                databaseNode.add(new DefaultMutableTreeNode(Common.language.getString("mysql_tree_path_2_views")));
                host.add(databaseNode);
            });
            connectionTree.expandPath(path);
        } else {
            host.removeAllChildren();
        }
        treeModel.reload(host);
    }

    private void tableDoubleClick(TreePath path) {
        DefaultMutableTreeNode host = (DefaultMutableTreeNode) path.getPathComponent(1);
        DefaultMutableTreeNode database = (DefaultMutableTreeNode) path.getPathComponent(2);
        DefaultMutableTreeNode path3 = (DefaultMutableTreeNode) path.getLastPathComponent();
        String databaseStr = (String) database.getUserObject();
        String hostStr = (String) host.getUserObject();

        if (path3.getUserObject().equals(Common.language.getString("mysql_tree_path_2_tables"))) {
            //判断该节点是否展开过，未展开过就添加新数据
            if (!connectionTree.hasBeenExpanded(path)) {
                final List<String> tables = MySQLApi.getTables(hostStr, databaseStr);
                tables.forEach(tableName -> path3.add(new DefaultMutableTreeNode(tableName)));
                connectionTree.expandPath(path);
                treeModel.reload(path3);
            }
        }

        if (path3.getUserObject().equals(Common.language.getString("mysql_tree_path_2_views"))) {
            //判断该节点是否展开过，未展开过就添加新数据
            if (!connectionTree.hasBeenExpanded(path)) {
                final List<String> tables = MySQLApi.getViews(hostStr, databaseStr);
                tables.forEach(tableName -> path3.add(new DefaultMutableTreeNode(tableName)));
                connectionTree.expandPath(path);
                treeModel.reload(path3);
            }
        }
    }

    private void selectTableDoubleClick(TreePath path) {
        DefaultMutableTreeNode host = (DefaultMutableTreeNode) path.getPathComponent(1);
        DefaultMutableTreeNode database = (DefaultMutableTreeNode) path.getPathComponent(2);
        DefaultMutableTreeNode table = (DefaultMutableTreeNode) path.getLastPathComponent();

        final String hostStr = (String) host.getUserObject();
        final String databaseStr = (String) database.getUserObject();
        final String tableStr = (String) table.getUserObject();

        SqlResult sqlResult = MySQLApi.selectTable(hostStr, databaseStr, tableStr, 0, 1000);
        dataTable.init(sqlResult);
        firstTab.setTabTitle(tableStr);
        firstTab.setClose(e -> clearTable());
    }

    public void initTabbedPane() {
        String[] menus = new String[]{
                Common.language.getString("mysql_button_new_sql")
        };

        this.firstTab = new TabPanel(Common.language.getString("mysql_date_table_first_tab"));
        this.lastTab = TabPanel.newLastTab(e -> {
            if (e.getActionCommand().equals(menus[0])) {
                NewSQL newSQL = new NewSQL();
                newSQL.setHostSet(MySQLConnection.getConnectionHosts());
                int tabCount = tabbedPane.getTabCount();
                TabPanel newSqlTab = new TabPanel(Common.language.getString("mysql_button_new_sql"));
                newSqlTab.setClose(mouseEvent -> {
                    tabbedPane.remove(newSqlTab);
                    tabbedPane.remove(newSQL.getPanel());
                    if (tabbedPane.getSelectedComponent() == null) {
                        //数组从0数起，-1指本身,-2指上一个
                        int previous = tabbedPane.getTabCount() - 2;
                        tabbedPane.setSelectedIndex(previous);
                    }
                });
                tabbedPane.addTab(null, null);
                tabbedPane.setComponentAt(tabCount - 1, newSQL.getPanel());
                tabbedPane.setTabComponentAt(tabCount - 1, newSqlTab);
                tabbedPane.setTabComponentAt(tabCount, lastTab);
                tabbedPane.setSelectedComponent(newSQL.getPanel());
            }
        }, menus);

        dataTable = new DataTable();
        tabbedPane.removeAll();
        tabbedPane.setFocusable(false);
        tabbedPane.add(dataTable.getPanel(), 0);
        tabbedPane.addTab(null, null);
        tabbedPane.setTabComponentAt(0, firstTab);
        tabbedPane.setTabComponentAt(1, lastTab);
    }

    private void popupMenuAction(TreePath path) {
        close.addActionListener(Optional.ofNullable(closeAction).map(actionListener -> {
                    close.removeActionListener(actionListener);
                    return closeAction = null;
                }).orElseGet(() -> closeAction = e -> {
                    switch (path.getPathCount()) {
                        case TREE_PATH_COUNT_HOST:
                            DefaultMutableTreeNode host = (DefaultMutableTreeNode) path.getLastPathComponent();
                            String hostStr = getTreePathNodeName(path);
                            MySQLConnection.closeConnection(hostStr);
                            host.removeAllChildren();
                            treeModel.reload(host);
                            break;
                        case TREE_PATH_COUNT_DATABASE:
                            connectionTree.collapsePath(path);
                            break;
                        case TREE_PATH_COUNT_TABLE:
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            node.removeAllChildren();
                            treeModel.reload(node);
                            break;
                        case TREE_PATH_COUNT_SELECT_TABLE:
                            clearTable();
                            break;
                        default:
                            break;
                    }
                })
        );

        newDatabase.addActionListener(
                Optional.ofNullable(newDatabaseAction).map(actionListener -> {
                    newDatabase.removeActionListener(actionListener);
                    return newDatabaseAction = null;
                }).orElseGet(() -> newDatabaseAction = e -> {
                    String hostStr = getTreePathNodeName(path);
                    NewDataBase.create(hostStr, aBoolean -> {
                        if (aBoolean) {
                            MessageDialog.showDialog(Common.language.getString("mysql_new_database_create_success"));
                            initTree();
                        } else {
                            MessageDialog.showDialog(Common.language.getString("mysql_new_database_create_fail"));
                        }
                    });
                })
        );

        dropDataBase.addActionListener(
                Optional.ofNullable(dropDataBaseAction).map(actionListener -> {
                    dropDataBase.removeActionListener(actionListener);
                    return dropDataBaseAction = null;
                }).orElseGet(() -> dropDataBaseAction = e -> {
                    String str = getTreePathNodeName(path.getParentPath());
                    String database = getTreePathNodeName(path);
                    MessageDialog.newDialog(Common.language.getString("mysql_dialog_drop_database_message"))
                            .setConfirmActionListener(e2 -> {
                                MySQLApi.dropDataBase(str, database);
                                initTree();
                            }).init();
                })
        );

        newTable.addActionListener(
                Optional.ofNullable(newTableAction).map(actionListener -> {
                    newTable.removeActionListener(newTableAction);
                    return newTableAction = null;
                }).orElseGet(() -> newTableAction = e -> {
                    NewTable table = new NewTable();
                    int tabCount = tabbedPane.getTabCount();
                    TabPanel newTable = new TabPanel(Common.language.getString("mysql_button_new_sql"));
                    newTable.setClose(mouseEvent -> {
                        tabbedPane.remove(newTable);
                        tabbedPane.remove(table.getPanel());
                        if (tabbedPane.getSelectedComponent() == null) {
                            //数组从0数起，-1指本身,-2指上一个
                            int previous = tabbedPane.getTabCount() - 2;
                            tabbedPane.setSelectedIndex(previous);
                        }
                    });
                    tabbedPane.addTab(null, null);
                    tabbedPane.setComponentAt(tabCount - 1, table.getPanel());
                    tabbedPane.setTabComponentAt(tabCount - 1, newTable);
                    tabbedPane.setTabComponentAt(tabCount, lastTab);
                    tabbedPane.setSelectedComponent(table.getPanel());
                })
        );

        dropTable.addActionListener(
                Optional.ofNullable(dropTableAction).map(actionListener -> {
                    dropTable.removeActionListener(dropTableAction);
                    return dropTableAction = null;
                }).orElseGet(() -> dropTableAction = e -> {
                })
        );
    }

    private void clearTable() {
        firstTab.removeClose();
        firstTab.setTabTitle(Common.language.getString("mysql_date_table_first_tab"));
        dataTable.clearTable();
    }

    @Override
    public void refresh() {
        initTree();
        initTabbedPane();
        newConnection.setToolTipText(Common.language.getString("mysql_button_new_connection"));
    }

    public String getTreePathNodeName(TreePath treePath) {
        Object userObject = ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();
        return userObject instanceof String ? (String) userObject : null;
    }

    public void showConnectionTreePopMenu(MouseEvent e, JMenuItem... items) {
        JPopupMenu jPopupMenu = new JPopupMenu();
        for (JMenuItem item : items) {
            jPopupMenu.add(item);
        }
        jPopupMenu.show(connectionTree, e.getX(), e.getY());
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
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerSize(4);
        panel.add(splitPane1, BorderLayout.CENTER);
        center = new JPanel();
        center.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(center);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        center.add(panel1, BorderLayout.NORTH);
        newConnection = new JButton();
        newConnection.setText("Button");
        panel1.add(newConnection);
        newSQL = new JButton();
        newSQL.setText("Button");
        panel1.add(newSQL);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        center.add(panel2, BorderLayout.CENTER);
        tabbedPane = new JTabbedPane();
        panel2.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        west = new JPanel();
        west.setLayout(new BorderLayout(0, 0));
        splitPane1.setLeftComponent(west);
        final JScrollPane scrollPane1 = new JScrollPane();
        west.add(scrollPane1, BorderLayout.CENTER);
        connectionTree = new JTree();
        scrollPane1.setViewportView(connectionTree);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
