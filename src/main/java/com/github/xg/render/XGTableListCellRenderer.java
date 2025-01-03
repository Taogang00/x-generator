package com.github.xg.render;

import com.github.xg.model.XGXmlElementTable;
import com.github.xg.ui.XGCodeUI;
import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * TableListCellRenderer
 *
 * @author taogang
 * @date 2024/11/15
 */
@Slf4j
public class XGTableListCellRenderer extends JLabel implements ListCellRenderer<String> {
    private final JLabel rowStartLabel;
    private final JLabel rowEndLabel;
    private final XGCodeUI xgCodeUI;

    public XGTableListCellRenderer(XGCodeUI xgCodeUI) {
        setOpaque(true);
        setLayout(new BorderLayout());
        this.xgCodeUI = xgCodeUI;

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
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        XGXmlElementTable XGXmlElementTable = xgCodeUI.getTableInfoMap().get(value);
        rowStartLabel.setText(value);
        rowEndLabel.setText(XGXmlElementTable.getComment());
        if (isSelected) {
            List<? extends String> selectedValuesList = list.getSelectedValuesList();
            xgCodeUI.getRunInfoLabel().setText("已选择【" + selectedValuesList.size() + "】张表");
            xgCodeUI.initXgGeneratorSelectedTableValuesList(selectedValuesList);
        }
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
