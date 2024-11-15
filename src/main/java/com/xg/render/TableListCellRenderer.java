package com.xg.render;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.xg.model.TableInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * TableListCellRenderer
 *
 * @author taogang
 * @date 2024/11/15
 */
@Slf4j
public class TableListCellRenderer extends JLabel implements ListCellRenderer<String> {
    private final JLabel runInfoLabel;
    private final JLabel rowStartLabel;
    private final JLabel rowEndLabel;
    private final Map<String, TableInfo> tableInfoMap;

    public TableListCellRenderer(Map<String, TableInfo> tableInfoMap, JLabel runInfoLabel) {
        setOpaque(true);
        setLayout(new BorderLayout());
        this.runInfoLabel = runInfoLabel;
        this.tableInfoMap = tableInfoMap;

        rowStartLabel = new JLabel();
        rowStartLabel.setIcon(AllIcons.Javaee.PersistenceEntity);
        // 设置内部边距（上、左、下、右的间距）
        rowStartLabel.setBorder(JBUI.Borders.empty(5));
        add(rowStartLabel, BorderLayout.WEST);

        rowEndLabel = new JLabel();
        rowEndLabel.setForeground(JBColor.GRAY);
        rowEndLabel.setBorder(JBUI.Borders.empty(5));
        add(rowEndLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        TableInfo tableInfo = tableInfoMap.get(value);
        rowStartLabel.setText(value);
        rowEndLabel.setText(tableInfo.getComment());
        if (isSelected) {
            runInfoLabel.setText("已选择" + list.getSelectedIndices().length + "张表");
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
