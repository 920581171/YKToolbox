package com.luoyk.toolbox.panel.mysql;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoyk.toolbox.api.MySQLApi;
import com.luoyk.toolbox.api.SqlResult;
import com.luoyk.toolbox.utils.Common;
import com.luoyk.toolbox.utils.MysqlWord;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewSQL {
    private JPanel panel;
    private JTextPane textPane;
    private JSplitPane jSplitPane;
    private JPanel buttonPanel;
    private JButton runSql;
    private JButton explain;
    private JComboBox<String> host;
    private JComboBox<String> dataBase;
    private JPanel showPanel;

    private Rectangle2D rectangle2D;

    private JPopupMenu popupMenu;

    private final Set<String> tables = new HashSet<>();
    private final Set<String> fields = new HashSet<>();

    public JPanel getPanel() {
        return panel;
    }

    public NewSQL() {
        init();
        initTextPane();
    }

    public static final SimpleAttributeSet lightAttributeSet = new SimpleAttributeSet();
    public static final SimpleAttributeSet commonAttributeSet = new SimpleAttributeSet();
    public static final SimpleAttributeSet stringAttributeSet = new SimpleAttributeSet();
    public static final SimpleAttributeSet tableAttributeSet = new SimpleAttributeSet();
    public static final SimpleAttributeSet fieldAttributeSet = new SimpleAttributeSet();

    static {
        StyleConstants.setForeground(lightAttributeSet, Color.MAGENTA);
        StyleConstants.setForeground(stringAttributeSet, Color.ORANGE);
        StyleConstants.setForeground(tableAttributeSet, Color.BLUE);
        StyleConstants.setForeground(fieldAttributeSet, Color.RED);
    }

    public void init() {

        Dimension hostDimension = new Dimension(192, (int) host.getPreferredSize().getHeight());
        Dimension dataBaseDimension = new Dimension(128, (int) host.getPreferredSize().getHeight());

        host.setMinimumSize(hostDimension);
        host.setPreferredSize(hostDimension);
        host.setMaximumSize(hostDimension);
        host.addItemListener(e -> {
            String host = (String) e.getItem();
            if (host.equals(Common.language.getString("mysql_new_sql_combo_box_host"))) {
                setDataBaseList(Collections.emptyList());
                dataBase.setEnabled(false);
                return;
            }
            setDataBaseList(MySQLApi.showDataBase(host));
            dataBase.setEnabled(true);
        });
        dataBase.setEnabled(false);
        dataBase.setMinimumSize(dataBaseDimension);
        dataBase.setPreferredSize(dataBaseDimension);
        dataBase.setMaximumSize(dataBaseDimension);

        dataBase.addItemListener(e -> {
            String database = (String) e.getItem();
            if (database.equals(Common.language.getString("mysql_new_sql_combo_box_database"))) {
                return;
            }
            this.tables.clear();
            this.fields.clear();
            this.tables.addAll(MySQLApi.getTables((String) host.getSelectedItem(), database));
            this.fields.addAll(MySQLApi.getTableFields((String) host.getSelectedItem(), database));
            dataBase.setEnabled(true);
        });

        textPane.setMinimumSize(new Dimension(0, 256));

        runSql.setText(Common.language.getString("mysql_new_sql_button_run_sql"));
        runSql.addActionListener(e -> {
            try {
                String sql = textPane.getSelectionEnd() - textPane.getSelectionStart() > 0 ?
                        textPane.getText(textPane.getSelectionStart(), textPane.getSelectionEnd()) :
                        textPane.getText();
                SqlResult sqlResult = MySQLApi.executeSQL(
                        (String) host.getSelectedItem(),
                        (String) dataBase.getSelectedItem(),
                        sql);
                DataTable dataTable = new DataTable();
                dataTable.init(sqlResult);
                setTable(dataTable);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });
        explain.setText(Common.language.getString("mysql_new_sql_button_explain"));
        explain.addActionListener(e -> {
            try {
                String sql = "explain " + (textPane.getSelectionEnd() - textPane.getSelectionStart() > 0 ?
                        textPane.getText(textPane.getSelectionStart(), textPane.getSelectionEnd()) :
                        textPane.getText());
                SqlResult sqlResult = MySQLApi.executeSQL(
                        (String) host.getSelectedItem(),
                        (String) dataBase.getSelectedItem(),
                        sql);
                DataTable dataTable = new DataTable();
                dataTable.init(sqlResult);
                setTable(dataTable);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        });
    }

    public void setTable(DataTable dataTable) {
        showPanel.removeAll();
        showPanel.add(dataTable.getPanel(), BorderLayout.CENTER);
        jSplitPane.validate();
    }

    public void clearTable() {
        showPanel.removeAll();
    }

    public void setHostSet(Set<String> hostSet) {
        host.removeAllItems();
        host.addItem(Common.language.getString("mysql_new_sql_combo_box_host"));
        dataBase.addItem(Common.language.getString("mysql_new_sql_combo_box_database"));
        for (String s : hostSet) {
            host.addItem(s);
        }
    }

    public void setDataBaseList(List<String> dataBases) {
        dataBase.removeAllItems();
        dataBase.addItem(Common.language.getString("mysql_new_sql_combo_box_database"));
        for (String base : dataBases) {
            dataBase.addItem(base);
        }
    }

    public void initTextPane() {
        StyledDocument document = new DefaultStyledDocument();
        document.addDocumentListener(documentListener);
        textPane.setDocument(document);
        popupMenu = new JPopupMenu();
        textPane.addCaretListener(e -> {
            JTextComponent textComp = (JTextComponent) e.getSource();
            try {
                String text = document.getText(0, document.getLength());
                String word = getTextWord(text, e.getDot());

                Stream<String> tablesFields = Stream.concat(
                        this.tables.stream().filter(str -> str.startsWith(word)),
                        this.fields.stream().filter(str -> str.split("\\.")[1].startsWith(word))
                );

                String wordUpperCase = word.toUpperCase();

                List<String> concat = Stream.concat(
                        //关键字集
                        MysqlWord.getWordSet().stream().filter(str -> str.startsWith(wordUpperCase)),
                        //数据表，数据表字段集
                        tablesFields
                ).collect(Collectors.toList());

                for (char c : word.toCharArray()) {
                    //只有纯英文输入的时候才展示popupMenu
                    if (c > 90 && c < 97) {
                        return;
                    }
                    if (c < 65 || c > 122) {
                        return;
                    }
                }

                popupMenu.removeAll();
                concat.stream().limit(8).forEach(str -> {
                    JMenuItem jMenuItem = new JMenuItem(str);
                    jMenuItem.addActionListener(ee -> {
                        try {
                            int dot = e.getDot() - word.length();
                            document.remove(dot, word.length());
                            document.insertString(dot, str, commonAttributeSet);
                        } catch (BadLocationException badLocationException) {
                            badLocationException.printStackTrace();
                        }
                    });
                    popupMenu.add(jMenuItem);
                });
                rectangle2D = textComp.getUI().modelToView2D(textComp, e.getDot(), Position.Bias.Forward);
            } catch (BadLocationException | NullPointerException exception) {
                exception.printStackTrace();
            }
        });
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    popupMenu.setVisible(false);
                    super.keyReleased(e);
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!popupMenu.isFocusable()) {
                        popupMenu.setFocusable(true);
                        popupMenu.setVisible(false);
                    }
                    popupMenu.show(textPane, (int) rectangle2D.getX() + 24, (int) rectangle2D.getY() + 24);
                    popupMenu.getRootPane().requestFocusInWindow();
                    super.keyReleased(e);
                    return;
                }
                if (e.getKeyCode() >= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z) {
                    popupMenu.setFocusable(false);
                    popupMenu.show(textPane, (int) rectangle2D.getX() + 24, (int) rectangle2D.getY() + 24);
                    //失去焦点时只获取一次焦点，避免中文输入法失焦异常
                    if (!textPane.isFocusOwner()) {
                        textPane.requestFocusInWindow();
                    }
                }
                super.keyReleased(e);
            }
        });

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopupMenu(e.getX(), e.getY());
                }
                super.mouseClicked(e);
            }
        });
    }

    /**
     * 分词
     */
    public static List<String> textSplit(String text) {
        LinkedList<String> linkedList = new LinkedList<>();
        boolean isStr = false;
        int start = 0;
        int end;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            //最后一个单词
            if (i == chars.length - 1) {
                if (chars[i] == KeyEvent.VK_SPACE) {
                    linkedList.add(text.substring(start, chars.length - 1));
                } else {
                    linkedList.add(text.substring(start, chars.length));
                }
                continue;
            }

            end = i;
            char c = chars[i];
            //字符串处理
            if (c == '\'') {
                if (isStr) {
                    isStr = false;
                    continue;
                }
                isStr = true;
            }

            if (!isStr) {
                if (c == KeyEvent.VK_SPACE) {
                    linkedList.add(text.substring(start, end));
                    start = i + 1;
                }
            }
        }
        return linkedList;
    }

    /**
     * 根据位置获得单词
     */
    public static String getTextWord(String text, int dot) {
        boolean isStr = false;
        int start = 0;
        int end = 0;
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            if (dot == i) {
                return text.substring(start, end + 1);
            }

            //最后一个单词
            if (i == chars.length - 1) {
                return text.substring(start, chars.length);
            }

            end = i;
            char c = chars[i];
            //字符串处理
            if (c == '\'') {
                if (isStr) {
                    isStr = false;
                    continue;
                }
                isStr = true;
            }

            if (!isStr) {
                if (c == KeyEvent.VK_SPACE) {
                    start = i + 1;
                }
            }
        }
        return "";
    }

    /**
     * 判断是不是表名
     */
    public boolean isTableWord(String word) {
        return this.tables.stream().anyMatch(str -> str.equals(word));
    }

    /**
     * 判断是不是表名
     */
    public boolean isFieldWord(String word) {
        return this.fields.stream().anyMatch(str -> str.equals(word));
    }

    public DocumentListener documentListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkLight(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkLight(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void checkLight(DocumentEvent e) {
            DefaultStyledDocument document = (DefaultStyledDocument) e.getDocument();
            int length = document.getLength();
            try {
                String text = e.getDocument().getText(0, length);

                LinkedList<String> linkedList = (LinkedList<String>) textSplit(text);

                //对词染色
                for (int i = 0; i < linkedList.size(); i++) {
                    int start = 0;
                    for (int j = 0; j < i; j++) {
                        start += linkedList.get(j).length() + 1;
                    }
                    int finalStart = start;
                    int finalLength = linkedList.get(i).length();
                    String word = linkedList.get(i);

                    if (MysqlWord.isWord(word.toUpperCase())) {
                        CompletableFuture.runAsync(() -> document.setCharacterAttributes(finalStart, finalLength, lightAttributeSet, true));
                    } else if (word.equals("*") || isTableWord(word)) {
                        CompletableFuture.runAsync(() -> document.setCharacterAttributes(finalStart, finalLength, tableAttributeSet, true));
                    } else if (isFieldWord(word)) {
                        CompletableFuture.runAsync(() -> document.setCharacterAttributes(finalStart, finalLength, fieldAttributeSet, true));
                    } else if (word.startsWith("'") && word.endsWith("'")) {
                        CompletableFuture.runAsync(() -> document.setCharacterAttributes(finalStart, finalLength, stringAttributeSet, true));
                    } else {
                        CompletableFuture.runAsync(() -> document.setCharacterAttributes(finalStart, finalLength, commonAttributeSet, true));
                    }
                }
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }
    };

    public void showPopupMenu(int x, int y) {
        JMenu sql = new JMenu("SQL");

        Map<String, String> hashMap = new LinkedHashMap<>() {
            {
                put("SELECT", "SELECT * FROM [TABLE] WHERE [CONDITION]");
                put("INSERT", "INSERT INTO [TABLE] ([FIELD],[FIELD],[FIELD]) , VALUE ([VALUE],[VALUE],[VALUE])");
                put("UPDATE", "UPDATE [TABLE] SET [FIELD] = [VALUE] WHERE [CONDITION]");
                put("DELETE", "DELETE [TABLE] WHERE [CONDITION]");
            }
        };

        hashMap.forEach((key, value) -> sql.add(key).addActionListener(e -> {
            try {
                textPane.getDocument().remove(textPane.getSelectionStart(), textPane.getSelectionEnd() - textPane.getSelectionStart());
                textPane.getDocument().insertString(textPane.getCaret().getDot(), value, commonAttributeSet);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }));

        popupMenu.removeAll();
        popupMenu.add(sql);
        popupMenu.show(textPane, x, y);
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
        panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        jSplitPane = new JSplitPane();
        jSplitPane.setDividerSize(4);
        jSplitPane.setOrientation(0);
        panel.add(jSplitPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        jSplitPane.setLeftComponent(panel1);
        textPane = new JTextPane();
        panel1.add(textPane, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        panel1.add(buttonPanel, BorderLayout.NORTH);
        host = new JComboBox();
        buttonPanel.add(host);
        dataBase = new JComboBox();
        buttonPanel.add(dataBase);
        runSql = new JButton();
        runSql.setText("Button");
        buttonPanel.add(runSql);
        explain = new JButton();
        explain.setText("Button");
        buttonPanel.add(explain);
        showPanel = new JPanel();
        showPanel.setLayout(new BorderLayout(0, 0));
        jSplitPane.setRightComponent(showPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
