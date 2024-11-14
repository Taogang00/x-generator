package com.xg.render;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.xg.model.TableInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TableListCellRenderer extends JLabel implements ListCellRenderer<String> {
    private JLabel label;
    private JLabel rowEndLabel;
    Map<String, TableInfo> tableInfoMap;
    String searchTableName;

    Map<String, String> highlightKey = new HashMap<>();

    public Map<String, String> getHighlightKey() {
        return highlightKey;
    }

    public void setHighlightKey(Map<String, String> highlightKey) {
        this.highlightKey = highlightKey;
    }

    public String getSearchTableName() {
        return searchTableName;
    }

    public void setSearchTableName(String searchTableName) {
        this.searchTableName = searchTableName;
    }

    public TableListCellRenderer(Map<String, TableInfo> tableInfoMap) {
        setOpaque(true);
        setLayout(new BorderLayout());
        this.tableInfoMap = tableInfoMap;
        label = new JLabel();
        label.setIcon(AllIcons.Javaee.PersistenceEntity);
        // 设置内部边距（上、左、下、右的间距）
        label.setBorder(JBUI.Borders.empty(5));

        rowEndLabel = new JLabel();
        rowEndLabel.setForeground(JBColor.GRAY);
        rowEndLabel.setBorder(JBUI.Borders.empty(5));
        add(label, BorderLayout.WEST);
        add(rowEndLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        label.setText(value);
        if (highlightKey.containsKey(value)) {
            label.setText(highlightKey.get(value));
        }
        TableInfo tableInfo = tableInfoMap.get(value);
        rowEndLabel.setText(tableInfo.getComment());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
