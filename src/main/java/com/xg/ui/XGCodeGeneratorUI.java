package com.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.XmlUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.xg.model.XGGlobalInfo;
import com.xg.model.XGXmlElementColumnInfo;
import com.xg.model.XGXmlElementTable;
import com.xg.model.XgGeneratorTableObj;
import com.xg.render.XGTableListCellRenderer;
import com.xg.utils.XGFileChooserUtil;
import com.xg.utils.XGMavenUtil;
import freemarker.template.Configuration;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xg.model.XGXmlElementColumnInfo.*;
import static com.xg.model.XGXmlElementTable.*;

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
    private JCheckBox mapXmlCheckBox;

    private JButton importBtn;
    private JButton packageAllBtn;
    private JButton settingBtn;

    private JComboBox<String> configComboBox;

    private JList<String> tableList;
    private JTextField ignoreTablePrefixTextField;
    private JTextField authorTextField;
    private JLabel runInfoLabel;

    private final XGGlobalInfo xgGlobalInfo;
    private List<XGXmlElementTable> tableInfoList;

    public XGCodeGeneratorUI(Project project) {
        this.settingBtn.setIcon(AllIcons.General.Settings);
        this.importBtn.setIcon(AllIcons.ToolbarDecorator.Import);
        this.authorTextField.setText(System.getProperty("user.name"));
        this.packageAllBtn.setText("全不选");
        this.xgGlobalInfo = new XGGlobalInfo();

        for (String s : XGMavenUtil.getMavenArtifactId(project)) {
            projectModuleComboBox.addItem(s);
        }

        packageAllBtn.addActionListener(e -> {
            if (!this.controllerCheckBox.isSelected()
                    || !this.serviceCheckBox.isSelected()
                    || !this.dtoCheckBox.isSelected()
                    || !this.queryCheckBox.isSelected()
                    || !this.mapperCheckBox.isSelected()
                    || !this.entityCheckBox.isSelected()
                    || !this.mapXmlCheckBox.isSelected()) {
                this.packageAllBtn.setText("全不选");
                this.controllerCheckBox.setSelected(true);
                this.entityCheckBox.setSelected(true);
                this.serviceCheckBox.setSelected(true);
                this.dtoCheckBox.setSelected(true);
                this.queryCheckBox.setSelected(true);
                this.mapperCheckBox.setSelected(true);
                this.mapStructCheckBox.setSelected(true);
                this.mapXmlCheckBox.setSelected(true);
            } else {
                this.packageAllBtn.setText("全选");
                this.controllerCheckBox.setSelected(false);
                this.entityCheckBox.setSelected(false);
                this.serviceCheckBox.setSelected(false);
                this.dtoCheckBox.setSelected(false);
                this.queryCheckBox.setSelected(false);
                this.mapStructCheckBox.setSelected(false);
                this.mapperCheckBox.setSelected(false);
                this.mapXmlCheckBox.setSelected(false);
            }
        });

        // 生成
        controllerCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateController(e.getStateChange() == ItemEvent.SELECTED);
        });
        serviceCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateService(e.getStateChange() == ItemEvent.SELECTED);
        });
        entityCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateEntity(e.getStateChange() == ItemEvent.SELECTED);
        });
        dtoCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateDTO(e.getStateChange() == ItemEvent.SELECTED);
        });
        queryCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateQuery(e.getStateChange() == ItemEvent.SELECTED);
        });
        mapStructCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateMapStruct(e.getStateChange() == ItemEvent.SELECTED);
        });
        mapperCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateMapper(e.getStateChange() == ItemEvent.SELECTED);
        });
        mapXmlCheckBox.addItemListener(e -> {
            this.xgGlobalInfo.setGenerateMapperXml(e.getStateChange() == ItemEvent.SELECTED);
        });

        // 选择项目时需要给代码生成的路径进行赋值
        projectModuleComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                initSelectedModulePackage(project, xgGlobalInfo, e.getItem().toString());
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
                Map<String, XGXmlElementTable> tableInfoMap = tableInfoList.stream().collect(Collectors.toMap(XGXmlElementTable::getName, Function.identity()));

                DefaultListModel<String> model = new DefaultListModel<>();
                // tableNameSet按照字母降序
                List<String> tableNameList = new ArrayList<>(tableInfoMap.keySet());
                Collections.sort(tableNameList);

                model.addAll(tableNameList);
                tableList.setModel(model);

                XGTableListCellRenderer cellRenderer = new XGTableListCellRenderer(tableInfoMap, runInfoLabel);
                tableList.setCellRenderer(cellRenderer);
            }
        });

        //初始化包赋值操作
        if (ObjectUtil.isNotNull(projectModuleComboBox.getSelectedItem())) {
            initSelectedModulePackage(project, xgGlobalInfo, projectModuleComboBox.getSelectedItem().toString());
        }
    }

    public static List<XGXmlElementTable> importTableXml(String path, JLabel runInfoLabel) {
        List<XGXmlElementTable> list = new ArrayList<>();

        File file = new File(path);
        if (!file.exists()) {
            runInfoLabel.setText("文件不存在");
            return null;
        }
        Document document = XmlUtil.readXML(file);
        NodeList tableNodes = document.getElementsByTagName(XML_ELEMENT_TABLE_NAME);

        // 遍历 Table 元素并打印信息
        for (int i = 0; i < tableNodes.getLength(); i++) {
            // 获取 Table 元素
            Element tableElement = (Element) tableNodes.item(i);

            // 提取表名 (Name 属性)
            XGXmlElementTable XGXmlElementTable = new XGXmlElementTable();
            List<XGXmlElementColumnInfo> columnList = new ArrayList<>();
            String tableName = tableElement.getAttribute(XML_ELEMENT_TABLE_ATTRIBUTE_NAME);
            String tableText = tableElement.getAttribute(XML_ELEMENT_TABLE_ATTRIBUTE_TEXT);
            XGXmlElementTable.setName(tableName);
            XGXmlElementTable.setComment(tableText);
            XGXmlElementTable.setColumnList(columnList);

            // 你可以根据需要提取更多的属性或子元素
            // 例如，提取 Table 下的 Column 元素
            NodeList columnNodes = tableElement.getElementsByTagName(XML_ELEMENT_COLUMN_NAME);
            for (int j = 0; j < columnNodes.getLength(); j++) {
                Element columnElement = (Element) columnNodes.item(j);
                String primaryKey = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_PRIMARY_KEY);
                String columnName = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_NAME);
                String columnText = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_TEXT);
                String dataType = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_DATATYPE);

                XGXmlElementColumnInfo XGXmlElementColumnInfo = new XGXmlElementColumnInfo();
                XGXmlElementColumnInfo.setName(columnText);
                XGXmlElementColumnInfo.setFieldName(columnName);
                XGXmlElementColumnInfo.setFieldType(dataType);
                XGXmlElementColumnInfo.setPrimaryKey(Boolean.getBoolean(primaryKey));
                XGXmlElementTable.getColumnList().add(XGXmlElementColumnInfo);
            }
            list.add(XGXmlElementTable);
        }

        runInfoLabel.setText("已导入" + list.size() + "张表");
        return list;
    }

    private void initSelectedModulePackage(Project project, XGGlobalInfo xgPackageInfo, String selectedItem) {
        File sourcePath = XGMavenUtil.getMavenArtifactIdSourcePath(project, selectedItem);
        assert sourcePath != null;
        File file = XGFileChooserUtil.walkFiles(sourcePath);
        codeGeneratorPathTextField.setText(sourcePath.getAbsolutePath());
        String modulePath = file.getAbsolutePath().replace(sourcePath.getAbsolutePath() + "\\", "");
        modulePath = modulePath.replace("\\", ".");
        xgPackageInfo.setModulePackagePath(modulePath);

        xgPackageInfo.setControllerPackagePath(modulePath + ".controller");
        xgPackageInfo.setServicePackagePath(modulePath + ".service");
        xgPackageInfo.setMapperPackagePath(modulePath + ".mapper");
        xgPackageInfo.setEntityPackagePath(modulePath + ".entity");
        xgPackageInfo.setDtoPackagePath(modulePath + ".dto");
        xgPackageInfo.setQueryPackagePath(modulePath + ".query");
        xgPackageInfo.setMapstructPackagePath(modulePath + ".mapstruct");
        xgPackageInfo.setMapperXmlPackagePath("mapper");

        controllerPathTextField.setText(xgPackageInfo.getControllerPackagePath());
        servicePathTextField.setText(xgPackageInfo.getServicePackagePath());
        mapperPathTextField.setText(xgPackageInfo.getMapperPackagePath());
        entityPathTextField.setText(xgPackageInfo.getEntityPackagePath());
        dtoPathTextField.setText(xgPackageInfo.getDtoPackagePath());
        queryPathTextField.setText(xgPackageInfo.getQueryPackagePath());
        mapStructPathTextField.setText(xgPackageInfo.getMapstructPackagePath());
        mapperXmlPathTextField.setText(xgPackageInfo.getMapperXmlPackagePath());
    }

    public void generateCode(Project project) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_33);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        configuration.setClassForTemplateLoading(this.getClass(), "/");

        System.out.println(this.xgGlobalInfo);
    }

    public void generateEntityCode(Project project, XGGlobalInfo xgGlobalInfo, XgGeneratorTableObj xgGeneratorTableObj) {
        System.out.println(project);
        System.out.println(xgGlobalInfo);
        System.out.println(xgGeneratorTableObj);
    }
}
