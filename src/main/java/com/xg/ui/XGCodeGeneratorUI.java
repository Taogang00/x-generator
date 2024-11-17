package com.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.XmlUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.xg.model.XGColumnInfo;
import com.xg.model.XGGlobalInfo;
import com.xg.model.XGTableInfo;
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
    private JComboBox<String> configComboBox;
    private JList<String> tableList;
    private JButton settingBtn;
    private JTextField ignoreTablePrefixTextField;
    private JTextField authorTextField;
    private JButton packageAllBtn;
    private JLabel runInfoLabel;

    private final XGGlobalInfo xgGlobalInfo;

    public XGCodeGeneratorUI(Project project) {
        this.settingBtn.setIcon(AllIcons.General.Settings);
        this.importBtn.setIcon(AllIcons.ToolbarDecorator.Import);
        this.authorTextField.setText(System.getProperty("user.name"));
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

            List<XGTableInfo> tableInfoList = importTableXml(path, runInfoLabel);

            if (tableInfoList != null) {
                this.xgGlobalInfo.setTableInfoList(tableInfoList);
                Map<String, XGTableInfo> tableInfoMap = tableInfoList.stream().collect(Collectors.toMap(XGTableInfo::getName, Function.identity()));

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

    public static List<XGTableInfo> importTableXml(String path, JLabel runInfoLabel) {
        List<XGTableInfo> list = new ArrayList<>();

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
            XGTableInfo XGTableInfo = new XGTableInfo();
            List<XGColumnInfo> columnList = new ArrayList<>();
            String tableName = tableElement.getAttribute("Name");
            String tableText = tableElement.getAttribute("Text");
            XGTableInfo.setName(tableName);
            XGTableInfo.setComment(tableText);
            XGTableInfo.setColumnList(columnList);

            // 你可以根据需要提取更多的属性或子元素
            // 例如，提取 Table 下的 Column 元素
            NodeList columnNodes = tableElement.getElementsByTagName("Column");
            for (int j = 0; j < columnNodes.getLength(); j++) {
                Element columnElement = (Element) columnNodes.item(j);
                String primaryKey = columnElement.getAttribute("PrimaryKey");
                String columnName = columnElement.getAttribute("Name");
                String columnText = columnElement.getAttribute("Text");
                String dataType = columnElement.getAttribute("DataType");

                XGColumnInfo XGColumnInfo = new XGColumnInfo();
                XGColumnInfo.setName(columnText);
                XGColumnInfo.setFieldName(columnName);
                XGColumnInfo.setFieldType(dataType);
                XGColumnInfo.setPrimaryKey(Boolean.getBoolean(primaryKey));
                XGTableInfo.getColumnList().add(XGColumnInfo);
            }
            list.add(XGTableInfo);
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
        xgPackageInfo.setModulePackageName(modulePath);

        xgPackageInfo.setControllerPackageName(modulePath + ".controller");
        xgPackageInfo.setServicePackageName(modulePath + ".service");
        xgPackageInfo.setMapperPackageName(modulePath + ".mapper");
        xgPackageInfo.setEntityPackageName(modulePath + ".entity");
        xgPackageInfo.setDtoPackageName(modulePath + ".dto");
        xgPackageInfo.setQueryPackageName(modulePath + ".query");
        xgPackageInfo.setMapstructPackageName(modulePath + ".mapstruct");
        xgPackageInfo.setMapperXmlPackage("mapper");

        controllerPathTextField.setText(xgPackageInfo.getControllerPackageName());
        servicePathTextField.setText(xgPackageInfo.getServicePackageName());
        mapperPathTextField.setText(xgPackageInfo.getMapperPackageName());
        entityPathTextField.setText(xgPackageInfo.getEntityPackageName());
        dtoPathTextField.setText(xgPackageInfo.getDtoPackageName());
        queryPathTextField.setText(xgPackageInfo.getQueryPackageName());
        mapStructPathTextField.setText(xgPackageInfo.getMapstructPackageName());
        mapperXmlPathTextField.setText(xgPackageInfo.getMapperXmlPackage());
    }

    public void generateCode(Project project) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_33);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        configuration.setClassForTemplateLoading(this.getClass(), "/");

        System.out.println(this.xgGlobalInfo);
    }
}
