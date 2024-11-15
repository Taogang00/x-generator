package com.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.XmlUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.xg.model.ColumnInfo;
import com.xg.model.TableInfo;
import com.xg.render.TableListCellRenderer;
import com.xg.utils.XGFileChooserUtil;
import com.xg.utils.XGMavenUtil;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XGCodeGeneratorUI {

    @Getter
    private JPanel rootJPanel;

    private JComboBox<String> projectModuleComboBox;
    private ExpandableTextField controllerPathTextField;
    private ExpandableTextField servicePathTextField;
    private ExpandableTextField mapperPathTextField;
    private ExpandableTextField entityPathTextField;
    private ExpandableTextField dtoPathTextField;
    private ExpandableTextField queryPathTextField;
    private ExpandableTextField mapStructPathTextField;
    private ExpandableTextField mapperXmlPathTextField;
    private ExpandableTextField codeGeneratorPathTextField;

    private JRadioButton ignoreRadioButton;
    private JRadioButton coverRadioButton;
    private JCheckBox controllerCheckBox;
    private JCheckBox serviceCheckBox;
    private JCheckBox mapperCheckBox;
    private JCheckBox entityCheckBox;
    private JCheckBox dtoCheckBox;
    private JCheckBox queryCheckBox;
    private JCheckBox mapStructCheckBox;
    private JButton importBtn;
    private JComboBox<String> comboBox1;
    private JList<String> tableList;
    private JButton settingBtn;
    private JTextField ignoreTablePrefixTextField;
    private JTextField authorTextField;
    private JButton packageAllBtn;
    private JButton packageNoAllBtn;
    private JButton packageInverseBtn;
    private JLabel runInfoLabel;
    private JBLabel qaLabel;

    private List<TableInfo> tableInfoList;

    public XGCodeGeneratorUI(Project project) {
        this.settingBtn.setIcon(AllIcons.General.Settings);
        this.importBtn.setIcon(AllIcons.ToolbarDecorator.Import);
        this.qaLabel.setIcon(AllIcons.Windows.Help);
        this.authorTextField.setText(System.getProperty("user.name"));

        for (String s : XGMavenUtil.getMavenArtifactId(project)) {
            projectModuleComboBox.addItem(s);
        }

        // 选择项目时需要给代码生成的路径进行赋值
        projectModuleComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                initProjectModuleComboBoxEvent(project, e.getItem().toString());
            }
        });

        importBtn.addActionListener(e -> {
            VirtualFile virtualFile = XGFileChooserUtil.chooseFileVirtual(project);
            if (ObjectUtil.isNull(virtualFile)) {
                return;
            }
            String path = virtualFile.getPath();
            tableList.setListData(new String[0]); // 清空JList内容

            this.tableInfoList = importTableXml(path, runInfoLabel);

            if (tableInfoList != null) {
                Map<String, TableInfo> tableInfoMap = tableInfoList.stream().collect(Collectors.toMap(TableInfo::getName, Function.identity()));

                DefaultListModel<String> model = new DefaultListModel<>();
                // tableNameSet按照字母降序
                List<String> tableNameList = new ArrayList<>(tableInfoMap.keySet());
                Collections.sort(tableNameList);

                model.addAll(tableNameList);
                tableList.setModel(model);

                TableListCellRenderer cellRenderer = new TableListCellRenderer(tableInfoMap, runInfoLabel);
                tableList.setCellRenderer(cellRenderer);
            }
        });

        //初始化包赋值操作
        if (ObjectUtil.isNotNull(projectModuleComboBox.getSelectedItem())) {
            initProjectModuleComboBoxEvent(project, projectModuleComboBox.getSelectedItem().toString());
        }
    }

    public static List<TableInfo> importTableXml(String path, JLabel runInfoLabel) {
        List<TableInfo> list = new ArrayList<>();

        File file = new File(path);
        if (!file.exists()) {
            runInfoLabel.setText("文件不存在");
            return null;
        }
        Document document = XmlUtil.readXML(file);
        NodeList tableNodes = document.getElementsByTagName("Table");

        // 遍历 Table 元素并打印信息
        for (int i = 0; i < tableNodes.getLength(); i++) {
            // 获取 Table 元素
            Element tableElement = (Element) tableNodes.item(i);

            // 提取表名 (Name 属性)
            TableInfo tableInfo = new TableInfo();
            List<ColumnInfo> columnList = new ArrayList<>();
            String tableName = tableElement.getAttribute("Name");
            String tableText = tableElement.getAttribute("Text");
            tableInfo.setName(tableName);
            tableInfo.setComment(tableText);
            tableInfo.setColumnList(columnList);

            // 你可以根据需要提取更多的属性或子元素
            // 例如，提取 Table 下的 Column 元素
            NodeList columnNodes = tableElement.getElementsByTagName("Column");
            for (int j = 0; j < columnNodes.getLength(); j++) {
                Element columnElement = (Element) columnNodes.item(j);
                String primaryKey = columnElement.getAttribute("PrimaryKey");
                String columnName = columnElement.getAttribute("Name");
                String columnText = columnElement.getAttribute("Text");
                String dataType = columnElement.getAttribute("DataType");

                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setName(columnText);
                columnInfo.setFieldName(columnName);
                columnInfo.setFieldType(dataType);
                columnInfo.setPrimaryKey(Boolean.getBoolean(primaryKey));
                tableInfo.getColumnList().add(columnInfo);
            }
            list.add(tableInfo);
        }

        runInfoLabel.setText("已导入" + list.size() + "张表");
        return list;
    }

    private void initProjectModuleComboBoxEvent(Project project, String selectedItem) {
        File sourcePath = XGMavenUtil.getMavenArtifactIdSourcePath(project, selectedItem);
        assert sourcePath != null;
        File file = XGFileChooserUtil.walkFiles(sourcePath);
        codeGeneratorPathTextField.setText(sourcePath.getAbsolutePath());
        String modulePath = file.getAbsolutePath().replace(sourcePath.getAbsolutePath() + "\\", "");
        modulePath = modulePath.replace("\\", ".");

        controllerPathTextField.setText(modulePath + ".controller");
        servicePathTextField.setText(modulePath + ".service");
        mapperPathTextField.setText(modulePath + ".mapper");
        entityPathTextField.setText(modulePath + ".entity");
        dtoPathTextField.setText(modulePath + ".dto");
        queryPathTextField.setText(modulePath + ".query");
        mapStructPathTextField.setText(modulePath + ".mapstruct");
        mapperXmlPathTextField.setText("mapper");
    }
}
