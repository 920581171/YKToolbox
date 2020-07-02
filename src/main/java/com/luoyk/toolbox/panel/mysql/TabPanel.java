package com.luoyk.toolbox.panel.mysql;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class TabPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel closeLabel;

    private Consumer<MouseEvent> close;

    private final MouseListener closeMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            close.accept(e);
            super.mouseClicked(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            closeLabel.setForeground(Color.BLACK);
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            closeLabel.setForeground(Color.gray);
            super.mouseExited(e);
        }
    };

    public TabPanel(String title) {
        this.setOpaque(false);
        this.titleLabel = new JLabel(title);
        this.add(this.titleLabel);
    }

    public void setTabTitle(String title) {
        this.titleLabel.setText(title);
    }

    public void setClose(Consumer<MouseEvent> close) {
        if (this.closeLabel != null) {
            return;
        }
        this.closeLabel = new JLabel("x");
        this.add(this.closeLabel);
        this.closeLabel.addMouseListener(closeMouseListener);
        this.close = close;
    }

    public void removeClose() {
        if (this.closeLabel != null) {
            this.remove(closeLabel);
            this.closeLabel = null;
        }
    }

    public JLabel getTitleLabel() {
        return this.titleLabel;
    }

    public TabPanel setTitleLabel(JLabel titleLabel) {
        this.titleLabel = titleLabel;
        return this;
    }

    public JLabel getCloseLabel() {
        return this.closeLabel;
    }

    public TabPanel setCloseLabel(JLabel closeLabel) {
        this.closeLabel = closeLabel;
        return this;
    }

    public static TabPanel newLastTab(ActionListener actionListener, String[] menus) {

        JPopupMenu jPopupMenu = new JPopupMenu();

        for (String menu : menus) {
            JMenuItem jMenuItem = new JMenuItem(menu);
            jMenuItem.setActionCommand(menu);
            jMenuItem.addActionListener(actionListener);
            jPopupMenu.add(jMenuItem);
        }

        TabPanel lastTab = new TabPanel("+");
        lastTab.getTitleLabel().setForeground(Color.GRAY);
        lastTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                jPopupMenu.show(lastTab, e.getX(), e.getY());
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lastTab.getTitleLabel().setForeground(Color.BLACK);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lastTab.getTitleLabel().setForeground(Color.GRAY);
                super.mouseExited(e);
            }
        });
        return lastTab;
    }
}
