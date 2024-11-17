package com.xg.render;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.xg.model.XGGeneratorTableFieldsObj;
import com.xg.model.XGXmlElementColumnInfo;
import com.xg.model.XGXmlElementTable;
import com.xg.model.XgGeneratorTableObj;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TableListCellRenderer
 *
 * @author taogang
 * @date 2024/11/15
 */
@Slf4j
public class XGTableListCellRenderer extends JLabel implements ListCellRenderer<String> {
    private final JLabel runInfoLabel;
    private final JLabel rowStartLabel;
    private final JLabel rowEndLabel;
    private final Map<String, XGXmlElementTable> tableInfoMap;
    private final Map<String, XgGeneratorTableObj> xgGeneratorTableObjMap;

    public XGTableListCellRenderer(Map<String, XGXmlElementTable> tableInfoMap, JLabel runInfoLabel, Map<String, XgGeneratorTableObj> xgGeneratorTableObjMap) {
        setOpaque(true);
        setLayout(new BorderLayout());
        this.runInfoLabel = runInfoLabel;
        this.tableInfoMap = tableInfoMap;
        this.xgGeneratorTableObjMap = xgGeneratorTableObjMap;

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
        XGXmlElementTable XGXmlElementTable = tableInfoMap.get(value);
        rowStartLabel.setText(value);
        rowEndLabel.setText(XGXmlElementTable.getComment());
        if (isSelected) {
            runInfoLabel.setText("已选择" + list.getSelectedIndices().length + "张表");
            List<? extends String> selectedValuesList = list.getSelectedValuesList();
            for (String s : selectedValuesList) {
                XGXmlElementTable xgXmlElementTable = tableInfoMap.get(s);
                XgGeneratorTableObj xgGeneratorTableObj = new XgGeneratorTableObj();
                xgGeneratorTableObj.setTableName(xgXmlElementTable.getName());
                xgGeneratorTableObj.setTableComment(xgXmlElementTable.getComment());
                xgGeneratorTableObj.setEntityClassName(xgXmlElementTable.getName());

                List<XGGeneratorTableFieldsObj> fields = new ArrayList<>();
                for (XGXmlElementColumnInfo columnInfo : xgXmlElementTable.getColumnList()) {
                    XGGeneratorTableFieldsObj xgGeneratorTableFieldsObj = new XGGeneratorTableFieldsObj();
                    xgGeneratorTableFieldsObj.setComment(columnInfo.getComment());
                    xgGeneratorTableFieldsObj.setPrimaryKey(columnInfo.getPrimaryKey());
                    xgGeneratorTableFieldsObj.setPropertyName(columnInfo.getFieldName());
                    //TODO转换
                    xgGeneratorTableFieldsObj.setPropertyType(columnInfo.getFieldType());
                    fields.add(xgGeneratorTableFieldsObj);
                }
                xgGeneratorTableObj.setFields(fields);
                xgGeneratorTableObjMap.put(s, xgGeneratorTableObj);
            }
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
