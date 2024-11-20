package com.xg.ui;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.xg.model.*;
import com.xg.render.XGTableListCellRenderer;
import com.xg.utils.XGFileChooserUtil;
import com.xg.utils.XGMavenUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.DOT;
import static com.xg.model.XGXmlElementColumn.*;
import static com.xg.model.XGXmlElementTable.*;

/**
 * 代码生成器 主页的UI
 *
 * @author taogang
 * @date 2024/11/19
 */
public class XGCodeGeneratorUI {

    @Getter
    private JPanel rootJPanel;
    @Getter
    private JLabel runInfoLabel;
    @Getter
    private Map<String, XGXmlElementTable> tableInfoMap;

    private JComboBox<String> projectModuleComboBox;
    private ExpandableTextField controllerPathTextField;
    private ExpandableTextField servicePathTextField;
    private ExpandableTextField mapperPathTextField;
    private ExpandableTextField entityPathTextField;
    private ExpandableTextField dtoPathTextField;
    private ExpandableTextField queryPathTextField;
    private ExpandableTextField mapStructPathTextField;
    private ExpandableTextField mapperXmlPathTextField;
    private ExpandableTextField sourceCodeGeneratorPathTextField;
    private ExpandableTextField resourcesCodeGeneratorPathTextField;

    private JRadioButton skipRadioButton;
    private JRadioButton overrideRadioButton;
    private JCheckBox controllerCheckBox;
    private JCheckBox serviceCheckBox;
    private JCheckBox mapperCheckBox;
    private JCheckBox entityCheckBox;
    private JCheckBox dtoCheckBox;
    private JCheckBox queryCheckBox;
    private JCheckBox mapStructCheckBox;
    private JCheckBox mapperXmlCheckBox;
    private JButton importBtn;
    private JButton packageAllBtn;
    private JButton settingBtn;
    private JComboBox<String> configComboBox;
    private JList<String> tableList;
    private JTextField ignoreTablePrefixTextField;
    private JTextField authorTextField;
    private List<XGXmlElementTable> tableInfoList;

    private final List<XgGeneratorTableObj> xgGeneratorSelectedTableObjList;
    private final XGGeneratorGlobalObj xgGeneratorGlobalObj;
    private final Map<String, Tuple> columnJavaTypeMapping = new HashMap<>();

    public XGCodeGeneratorUI(Project project) {
        this.xgGeneratorGlobalObj = new XGGeneratorGlobalObj();
        this.ignoreTablePrefixTextField.setText("_");
        this.xgGeneratorGlobalObj.setIgnoreTablePrefix("_");
        this.skipRadioButton.setActionCommand("0");
        this.overrideRadioButton.setActionCommand("1");

        this.settingBtn.setIcon(AllIcons.General.GearPlain);
        this.importBtn.setIcon(AllIcons.General.OpenDisk);
        this.runInfoLabel.setIcon(AllIcons.General.Information);
        this.authorTextField.setText(System.getProperty("user.name"));
        this.packageAllBtn.setText("全不选");
        this.xgGeneratorSelectedTableObjList = new ArrayList<>();
        this.xgGeneratorGlobalObj.setDateTime(DateUtil.formatDateTime(new Date()));
        this.xgGeneratorGlobalObj.setAuthor(authorTextField.getText());
        this.xgGeneratorGlobalObj.setFileOverride(false);

        // 1.项目模块加载
        List<String> mavenArtifactIds = XGMavenUtil.getMavenArtifactId(project);
        for (String item : mavenArtifactIds) {
            projectModuleComboBox.addItem(item);
        }

        // 2.生成Java对象【全选】、【全不选】按钮事件
        packageAllBtn.addActionListener(e -> {
            if (!this.controllerCheckBox.isSelected()
                    || !this.serviceCheckBox.isSelected()
                    || !this.dtoCheckBox.isSelected()
                    || !this.queryCheckBox.isSelected()
                    || !this.mapperCheckBox.isSelected()
                    || !this.entityCheckBox.isSelected()
                    || !this.mapperXmlCheckBox.isSelected()) {
                this.packageAllBtn.setText("全不选");
                this.controllerCheckBox.setSelected(true);
                this.entityCheckBox.setSelected(true);
                this.serviceCheckBox.setSelected(true);
                this.dtoCheckBox.setSelected(true);
                this.queryCheckBox.setSelected(true);
                this.mapperCheckBox.setSelected(true);
                this.mapStructCheckBox.setSelected(true);
                this.mapperXmlCheckBox.setSelected(true);
            } else {
                this.packageAllBtn.setText("全选");
                this.controllerCheckBox.setSelected(false);
                this.entityCheckBox.setSelected(false);
                this.serviceCheckBox.setSelected(false);
                this.dtoCheckBox.setSelected(false);
                this.queryCheckBox.setSelected(false);
                this.mapStructCheckBox.setSelected(false);
                this.mapperCheckBox.setSelected(false);
                this.mapperXmlCheckBox.setSelected(false);
            }
        });

        // 3.生成Java对象生成与否的单选事件
        controllerCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateController(e.getStateChange() == ItemEvent.SELECTED));
        serviceCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateService(e.getStateChange() == ItemEvent.SELECTED));
        entityCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateEntity(e.getStateChange() == ItemEvent.SELECTED));
        dtoCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateDTO(e.getStateChange() == ItemEvent.SELECTED));
        queryCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateQuery(e.getStateChange() == ItemEvent.SELECTED));
        mapStructCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapStruct(e.getStateChange() == ItemEvent.SELECTED));
        mapperCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapper(e.getStateChange() == ItemEvent.SELECTED));
        mapperXmlCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapperXml(e.getStateChange() == ItemEvent.SELECTED));

        // 4.添加ActionListener来监听文件冲突时按钮的状态变化
        ActionListener actionListener = e -> {
            // 获取选中的 JRadioButton
            JRadioButton selectedButton = (JRadioButton) e.getSource();
            if ("1".equals(selectedButton.getActionCommand())) {
                this.xgGeneratorGlobalObj.setFileOverride(true);
            } else if ("0".equals(selectedButton.getActionCommand())) {
                this.xgGeneratorGlobalObj.setFileOverride(false);
            }
        };
        skipRadioButton.addActionListener(actionListener);
        overrideRadioButton.addActionListener(actionListener);

        // 5.选择项目时需要给代码生成的路径进行赋值
        projectModuleComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                initXgGeneratorGlobalOutputPathAndPackagePath(project, e.getItem().toString());
            }
        });

        // 6.导入xml按钮事件
        importBtn.addActionListener(e -> {
            VirtualFile virtualFile = XGFileChooserUtil.chooseFileVirtual(project);
            if (ObjectUtil.isNull(virtualFile)) {
                return;
            }
            String path = virtualFile.getPath();
            tableList.setListData(new String[0]); // 清空JList内容
            this.tableInfoList = importTableXml(path, runInfoLabel);

            if (tableInfoList != null) {
                tableInfoMap = tableInfoList.stream().collect(Collectors.toMap(XGXmlElementTable::getName, Function.identity()));

                DefaultListModel<String> model = new DefaultListModel<>();
                // tableNameSet按照字母降序
                List<String> tableNameList = new ArrayList<>(tableInfoMap.keySet());
                Collections.sort(tableNameList);

                model.addAll(tableNameList);
                tableList.setModel(model);

                XGTableListCellRenderer cellRenderer = new XGTableListCellRenderer(this);
                tableList.setCellRenderer(cellRenderer);
            }
        });

        // 7.初始化包赋值操作
        if (ObjectUtil.isNotNull(projectModuleComboBox.getSelectedItem())) {
            initXgGeneratorGlobalOutputPathAndPackagePath(project, projectModuleComboBox.getSelectedItem().toString());
        }

        // 8.初始化数据库类型映射
        initColumnJavaTypeMapping();

        // 9. 表单元素输入监听
        authorTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setAuthor(authorTextField.getText());
            }
        });
        ignoreTablePrefixTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setIgnoreTablePrefix(ignoreTablePrefixTextField.getText());
            }
        });
        sourceCodeGeneratorPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setSourceCodeGeneratorPath(sourceCodeGeneratorPathTextField.getText());
            }
        });
        resourcesCodeGeneratorPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setResourcesCodeGeneratorPath(resourcesCodeGeneratorPathTextField.getText());
            }
        });
        controllerPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setControllerPackagePath(controllerPathTextField.getText());
            }
        });
        servicePathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setServicePackagePath(servicePathTextField.getText());
            }
        });
        entityPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setEntityPackagePath(entityPathTextField.getText());
            }
        });
        dtoPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setDtoPackagePath(dtoPathTextField.getText());
            }
        });
        queryPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setQueryPackagePath(queryPathTextField.getText());
            }
        });
        mapStructPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setMapstructPackagePath(mapStructPathTextField.getText());
            }
        });
        mapperPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setMapperPackagePath(mapperPathTextField.getText());
            }
        });
        mapperXmlPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGeneratorGlobalObj.setMapperXmlPackagePath(mapperXmlPathTextField.getText());
            }
        });
    }

    /**
     * 导入表 XML
     *
     * @param path         路径
     * @param runInfoLabel Run Info 标签
     * @return {@link List }<{@link XGXmlElementTable }>
     */
    public List<XGXmlElementTable> importTableXml(String path, JLabel runInfoLabel) {
        List<XGXmlElementTable> list = new ArrayList<>();
        try {
            Document document = XmlUtil.readXML(path);
            NodeList tableNodes = document.getElementsByTagName(XML_ELEMENT_TABLE_NAME);
            // 遍历 Table 元素并打印信息
            for (int i = 0; i < tableNodes.getLength(); i++) {
                Element tableElement = (Element) tableNodes.item(i);

                // 提取表名 (Name 属性)
                XGXmlElementTable XGXmlElementTable = new XGXmlElementTable();
                List<XGXmlElementColumn> columnList = new ArrayList<>();
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

                    XGXmlElementColumn XGXmlElementColumn = new XGXmlElementColumn();
                    XGXmlElementColumn.setName(columnText);
                    XGXmlElementColumn.setFieldName(columnName);
                    XGXmlElementColumn.setFieldType(dataType);
                    XGXmlElementColumn.setPrimaryKey(Boolean.valueOf(primaryKey));
                    XGXmlElementTable.getColumnList().add(XGXmlElementColumn);
                }
                list.add(XGXmlElementTable);
            }
            this.runInfoLabel.setText("已导入" + list.size() + "张表");
            this.runInfoLabel.setIcon(AllIcons.General.Information);
        } catch (Exception e) {
            this.runInfoLabel.setText("文件解析错误，请选择正确格式的xml文件");
            this.runInfoLabel.setIcon(AllIcons.General.Warning);
        }
        return list;
    }

    /**
     * 初始化输出代码的路径和包路径
     *
     * @param project      项目
     * @param selectedItem 已选项
     */
    public void initXgGeneratorGlobalOutputPathAndPackagePath(Project project, String selectedItem) {
        //src/main/java 绝对地址目录,形如：D:\gogs\camel\2.src\tles-oles-camel-out\src\main\java
        File sourceDirectory = XGMavenUtil.getMavenArtifactIdSourcePath(project, selectedItem);
        //src/main/source 绝对地址目录，形如：D:\gogs\camel\2.src\tles-oles-camel-out\src\main\resource
        File resourceDirectory = XGMavenUtil.getMavenArtifactIdResourcePath(project, selectedItem);
        assert sourceDirectory != null;
        assert resourceDirectory != null;
        String sourceDirectoryAbsolutePath = sourceDirectory.getAbsolutePath();
        String resourceDirectoryAbsolutePath = resourceDirectory.getAbsolutePath();

        this.sourceCodeGeneratorPathTextField.setText(sourceDirectoryAbsolutePath);
        this.resourcesCodeGeneratorPathTextField.setText(resourceDirectoryAbsolutePath);
        this.xgGeneratorGlobalObj.setSourceCodeGeneratorPath(sourceDirectoryAbsolutePath);
        this.xgGeneratorGlobalObj.setResourcesCodeGeneratorPath(resourceDirectoryAbsolutePath);

        File file = XGFileChooserUtil.walkFiles(sourceDirectory);
        String outputFilePath = file.getAbsolutePath();

        //D:\gogs\camel\2.src\tles-oles-camel-out\src\main\java 与 D:\gogs\camel\2.src\tles-oles-camel-out\src\main\java\com\tles\oles\controller 差：
        //=\com\tles\oles\controller
        String controllerPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "controller", sourceDirectoryAbsolutePath);
        String servicePackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "service", sourceDirectoryAbsolutePath);
        String mapperPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "mapper", sourceDirectoryAbsolutePath);
        String entityPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "entity", sourceDirectoryAbsolutePath);
        String dtoPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "dto", sourceDirectoryAbsolutePath);
        String queryPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "query", sourceDirectoryAbsolutePath);
        String mapStructPackagePath = StrUtil.removePrefix(outputFilePath + File.separator + "mapstruct", sourceDirectoryAbsolutePath);

        // \com\tles\oles\controller -> .com.tles.oles.controller
        controllerPackagePath = StrUtil.replace(controllerPackagePath, File.separator, DOT);
        servicePackagePath = StrUtil.replace(servicePackagePath, File.separator, DOT);
        mapperPackagePath = StrUtil.replace(mapperPackagePath, File.separator, DOT);
        entityPackagePath = StrUtil.replace(entityPackagePath, File.separator, DOT);
        dtoPackagePath = StrUtil.replace(dtoPackagePath, File.separator, DOT);
        queryPackagePath = StrUtil.replace(queryPackagePath, File.separator, DOT);
        mapStructPackagePath = StrUtil.replace(mapStructPackagePath, File.separator, DOT);

        // 去掉 .com.tles.oles.controller 第一个.
        controllerPackagePath = StrUtil.removePrefix(controllerPackagePath, DOT);
        servicePackagePath = StrUtil.removePrefix(servicePackagePath, DOT);
        mapperPackagePath = StrUtil.removePrefix(mapperPackagePath, DOT);
        entityPackagePath = StrUtil.removePrefix(entityPackagePath, DOT);
        dtoPackagePath = StrUtil.removePrefix(dtoPackagePath, DOT);
        queryPackagePath = StrUtil.removePrefix(queryPackagePath, DOT);
        mapStructPackagePath = StrUtil.removePrefix(mapStructPackagePath, DOT);

        this.xgGeneratorGlobalObj.setControllerPackagePath(controllerPackagePath);
        this.xgGeneratorGlobalObj.setServicePackagePath(servicePackagePath);
        this.xgGeneratorGlobalObj.setMapperPackagePath(mapperPackagePath);
        this.xgGeneratorGlobalObj.setEntityPackagePath(entityPackagePath);
        this.xgGeneratorGlobalObj.setDtoPackagePath(dtoPackagePath);
        this.xgGeneratorGlobalObj.setQueryPackagePath(queryPackagePath);
        this.xgGeneratorGlobalObj.setMapstructPackagePath(mapStructPackagePath);
        this.xgGeneratorGlobalObj.setMapperXmlPackagePath("mapper");

        this.controllerPathTextField.setText(controllerPackagePath);
        this.servicePathTextField.setText(this.xgGeneratorGlobalObj.getServicePackagePath());
        this.mapperPathTextField.setText(this.xgGeneratorGlobalObj.getMapperPackagePath());
        this.entityPathTextField.setText(this.xgGeneratorGlobalObj.getEntityPackagePath());
        this.dtoPathTextField.setText(this.xgGeneratorGlobalObj.getDtoPackagePath());
        this.queryPathTextField.setText(this.xgGeneratorGlobalObj.getQueryPackagePath());
        this.mapStructPathTextField.setText(this.xgGeneratorGlobalObj.getMapstructPackagePath());
        this.mapperXmlPathTextField.setText(this.xgGeneratorGlobalObj.getMapperXmlPackagePath());
    }

    /**
     * 初始化需要生成的表对象
     *
     * @param selectedValuesList Selected Values 列表
     */
    public void initXgGeneratorSelectedTableObjList(List<? extends String> selectedValuesList) {
        xgGeneratorSelectedTableObjList.clear();
        for (String s : selectedValuesList) {
            XGXmlElementTable xgXmlElementTable = tableInfoMap.get(s);
            String elementTableName = xgXmlElementTable.getName();

            XgGeneratorTableObj xgGeneratorTableObj = new XgGeneratorTableObj();
            xgGeneratorTableObj.setTableName(elementTableName);
            xgGeneratorTableObj.setTableComment(xgXmlElementTable.getComment());
            //entity
            xgGeneratorTableObj.setEntityClassName(elementTableName);
            xgGeneratorTableObj.setEntityPackagePath(xgGeneratorGlobalObj.getEntityPackagePath());
            xgGeneratorTableObj.setEntityPath(xgGeneratorGlobalObj.getOutputEntityPath() + File.separator + xgGeneratorTableObj.getEntityClassName() + ".java");
            //mapper
            xgGeneratorTableObj.setMapperClassName(elementTableName + "Mapper");
            xgGeneratorTableObj.setMapperPackagePath(xgGeneratorGlobalObj.getMapperPackagePath());
            xgGeneratorTableObj.setMapperPath(xgGeneratorGlobalObj.getOutputEntityPath() + File.separator + xgGeneratorTableObj.getMapstructClassName() + ".java");
            //mapper-xml
            xgGeneratorTableObj.setMapperXml(elementTableName + "Mapper");
            xgGeneratorTableObj.setMapperXmlPackagePath(xgGeneratorGlobalObj.getOutputMapperXmlPath());
            xgGeneratorTableObj.setMapperXmlPath(xgGeneratorGlobalObj.getOutputMapperXmlPath() + File.separator + xgGeneratorTableObj.getMapperXml() + ".xml");
            //service
            xgGeneratorTableObj.setServiceClassName(elementTableName + "Service");
            xgGeneratorTableObj.setServicePackagePath(xgGeneratorGlobalObj.getServicePackagePath());
            xgGeneratorTableObj.setServicePath(xgGeneratorGlobalObj.getOutputServicePath() + File.separator + xgGeneratorTableObj.getServiceClassName() + ".java");
            //service-impl
            xgGeneratorTableObj.setServiceImplClassName(elementTableName + "ServiceImpl");
            xgGeneratorTableObj.setServiceImplPackagePath(xgGeneratorGlobalObj.getServiceImplPackagePath());
            xgGeneratorTableObj.setServiceImplPath(xgGeneratorGlobalObj.getOutputServiceImplPath() + File.separator + xgGeneratorTableObj.getServiceImplClassName() + ".java");
            //dto
            xgGeneratorTableObj.setDtoClassName(elementTableName + "DTO");
            xgGeneratorTableObj.setDtoPackagePath(xgGeneratorGlobalObj.getDtoPackagePath());
            xgGeneratorTableObj.setDtoPath(xgGeneratorGlobalObj.getOutputDTOPath() + File.separator + xgGeneratorTableObj.getDtoClassName() + ".java");
            //query
            xgGeneratorTableObj.setQueryClassName(elementTableName + "Query");
            xgGeneratorTableObj.setQueryPackagePath(xgGeneratorGlobalObj.getQueryPackagePath());
            xgGeneratorTableObj.setQueryPath(xgGeneratorGlobalObj.getOutputQueryPath() + File.separator + xgGeneratorTableObj.getQueryClassName() + ".java");
            //controller
            xgGeneratorTableObj.setControllerClassName(elementTableName + "Controller");
            xgGeneratorTableObj.setControllerPackagePath(xgGeneratorGlobalObj.getControllerPackagePath());
            xgGeneratorTableObj.setControllerPath(xgGeneratorGlobalObj.getOutputControllerPath() + File.separator + xgGeneratorTableObj.getControllerClassName() + ".java");
            //mapstruct
            xgGeneratorTableObj.setMapstructClassName(elementTableName + "Mapstruct");
            xgGeneratorTableObj.setMapstructPackagePath(xgGeneratorGlobalObj.getMapstructPackagePath());
            xgGeneratorTableObj.setMapstructPath(xgGeneratorGlobalObj.getOutputMapStructPath() + File.separator + xgGeneratorTableObj.getMapstructClassName() + ".java");

            List<XGGeneratorTableFieldsObj> tableFields = new ArrayList<>();
            for (XGXmlElementColumn columnInfo : xgXmlElementTable.getColumnList()) {
                XGGeneratorTableFieldsObj xgGeneratorTableFieldsObj = new XGGeneratorTableFieldsObj();
                xgGeneratorTableFieldsObj.setComment(columnInfo.getName());
                xgGeneratorTableFieldsObj.setPrimaryKey(columnInfo.getPrimaryKey());
                xgGeneratorTableFieldsObj.setPropertyName(StrUtil.lowerFirst(columnInfo.getFieldName()));
                xgGeneratorTableFieldsObj.setPropertyType(columnInfo.getFieldType());
                //重新赋值
                for (Map.Entry<String, Tuple> regexEntry : this.columnJavaTypeMapping.entrySet()) {
                    boolean match = ReUtil.isMatch(regexEntry.getKey(), columnInfo.getFieldType().toLowerCase());
                    if (match) {
                        xgGeneratorTableFieldsObj.setPropertyType(regexEntry.getValue().get(0));
                        xgGeneratorTableFieldsObj.setPropertyClass(regexEntry.getValue().get(1));
                    }
                }
                tableFields.add(xgGeneratorTableFieldsObj);
            }
            xgGeneratorTableObj.setTableFields(tableFields);
            xgGeneratorSelectedTableObjList.add(xgGeneratorTableObj);
        }
    }

    /**
     * 初始化数据库类型映射
     */
    public void initColumnJavaTypeMapping() {
        // 数据库类型映射
        this.columnJavaTypeMapping.put("varchar(\\(\\d+\\))?", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("varchar2(\\(\\d+\\))?", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("nvarchar(\\(\\d+\\))?", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("nvarchar2(\\(\\d+\\))?", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("char(\\(\\d+\\))?", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("(tiny|medium|long)*text", new Tuple("String", "java.lang.String"));
        this.columnJavaTypeMapping.put("numeric(\\(\\d+,\\d+\\))?", new Tuple("Double", "java.lang.Double"));
        this.columnJavaTypeMapping.put("numericn(\\(\\d+,\\d+\\))?", new Tuple("Double", "java.lang.Double"));
        this.columnJavaTypeMapping.put("numeric(\\(\\d+\\))?", new Tuple("Integer", "java.lang.Integer"));
        this.columnJavaTypeMapping.put("decimal(\\(\\d+,\\d+\\))?", new Tuple("Double", "java.lang.Double"));
        this.columnJavaTypeMapping.put("bigint(\\(\\d+\\))?", new Tuple("Long", "java.lang.Long"));
        this.columnJavaTypeMapping.put("(tiny|small|medium)*int(\\(\\d+\\))?", new Tuple("Integer", "java.lang.Integer"));
        this.columnJavaTypeMapping.put("integer", new Tuple("Integer", "java.lang.Integer"));
        this.columnJavaTypeMapping.put("date", new Tuple("Date", "java.util.Date"));
        this.columnJavaTypeMapping.put("datetime", new Tuple("Date", "java.util.Date"));
    }

    /**
     * 从字符串获取模板
     *
     * @param templateContent 模板内容
     * @param templateName    模板名称
     * @return {@link Template }
     * @throws IOException io异常
     */
    public Template getFreemarkerTemplate(String templateContent, String templateName) throws IOException {
        // 创建 FreeMarker 配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.displayName());

        // 使用 StringReader 将字符串内容转换为 Reader
        StringReader stringReader = new StringReader(templateContent);

        // 创建模板并加载\ templateName 是模板的名称，可以任意指定
        return new Template(templateName, stringReader, cfg);
    }

    /**
     * 生成代码-点击生成按钮事件
     *
     * @param project      项目
     * @param xgMainDialog 项目
     * @throws IOException io异常
     */
    @SuppressWarnings("all")
    public void generateCodeAction(Project project, XGMainDialog xgMainDialog) throws IOException {
        if (this.tableInfoList == null || this.tableInfoList.isEmpty()) {
            Messages.showDialog("请先导入表实体数据！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }
        if (this.xgGeneratorSelectedTableObjList.isEmpty()) {
            Messages.showDialog("请先选择要生成的表实体！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }
        if (!controllerCheckBox.isSelected() && !entityCheckBox.isSelected()
                && !mapStructCheckBox.isSelected() && !queryCheckBox.isSelected() && !mapperXmlCheckBox.isSelected()
                && !mapperCheckBox.isSelected() && !serviceCheckBox.isSelected() && !dtoCheckBox.isSelected()) {
            Messages.showDialog("请先选择要生成的代码对象！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }

        //去掉统一前缀
        for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
            xgGeneratorTableObj.setControllerClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getControllerClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setControllerPath(xgGeneratorGlobalObj.getOutputControllerPath() + File.separator + xgGeneratorTableObj.getControllerClassName() + ".java");

            xgGeneratorTableObj.setServiceClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getServiceClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setServicePath(xgGeneratorGlobalObj.getOutputServicePath() + File.separator + xgGeneratorTableObj.getServiceClassName() + ".java");

            xgGeneratorTableObj.setServiceImplClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getServiceImplClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setServiceImplPath(xgGeneratorGlobalObj.getOutputServiceImplPath() + File.separator + xgGeneratorTableObj.getServiceImplClassName() + ".java");

            xgGeneratorTableObj.setMapperClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getMapperClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setMapperPath(xgGeneratorGlobalObj.getOutputMapperPath() + File.separator + xgGeneratorTableObj.getMapperClassName() + ".java");

            xgGeneratorTableObj.setDtoClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getDtoClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setDtoPath(xgGeneratorGlobalObj.getOutputDTOPath() + File.separator + xgGeneratorTableObj.getDtoClassName() + ".java");

            xgGeneratorTableObj.setEntityClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getEntityClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setEntityPath(xgGeneratorGlobalObj.getOutputEntityPath() + File.separator + xgGeneratorTableObj.getEntityClassName() + ".java");

            xgGeneratorTableObj.setQueryClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getQueryClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setQueryPath(xgGeneratorGlobalObj.getOutputQueryPath() + File.separator + xgGeneratorTableObj.getQueryClassName() + ".java");

            xgGeneratorTableObj.setMapstructClassName(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getMapstructClassName(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setMapstructPath(xgGeneratorGlobalObj.getOutputMapStructPath() + File.separator + xgGeneratorTableObj.getMapstructClassName() + ".java");

            xgGeneratorTableObj.setMapperXml(StrUtil.replaceIgnoreCase(xgGeneratorTableObj.getMapperXml(), this.xgGeneratorGlobalObj.getIgnoreTablePrefix(), ""));
            xgGeneratorTableObj.setMapperXmlPath(xgGeneratorGlobalObj.getOutputMapperXmlPath() + File.separator + xgGeneratorTableObj.getMapperXml() + ".xml");
        }

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> xgGlobalInfoMap = BeanUtil.beanToMap(this.xgGeneratorGlobalObj);
        map.put("global", xgGlobalInfoMap);

        int count = 0;
        //默认-生成controller
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/controller.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "controller");
            count += generateControllerCode(template, map);
        }

        //默认-生成entity
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/entity.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "entity");
            count += generateEntityCode(template, map);
        }

        //默认-生成dto
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/dto.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "dto");
            count += generateDTOCode(template, map);
        }

        //默认-生成query
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/query.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "query");
            count += generateQueryCode(template, map);
        }

        //默认-生成service
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/service.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "service");
            count += generateServiceCode(template, map);
        }

        //默认-生成serviceImpl
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/serviceImpl.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "serviceImpl");
            count += generateServiceImplCode(template, map);
        }

        //默认-生成mapper
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapper.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "mapper");
            count += generateMapperCode(template, map);
        }

        //默认-生成mapper-xml
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapper.xml.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "mapper-xml");
            count += generateMapperXmlCode(template, map);
        }

        //默认-生成mapstruct
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapstruct.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getFreemarkerTemplate(templateContent, "mapstruct");
            count += generateMapStructCode(template, map);
        }

        NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
        Notification notification = groupManager.getNotificationGroup("NotificationXg").createNotification("生成成功，共有 " + count + " 个文件发生变化", MessageType.INFO).setTitle("X-Generator");
        Notifications.Bus.notify(notification, project);
        xgMainDialog.doCancelAction();
    }

    /**
     * 生成entity代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateControllerCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateController()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputControllerPath());
            // 在使用 FileOutputStream 时，如果文件的父目录不存在（即文件所在的文件夹），Java 会抛出 FileNotFoundException，即使你尝试创建一个新的文件。
            // 为了避免这个问题，你需要确保文件的父目录已经存在。如果目录不存在，你需要手动创建它。
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getControllerPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getControllerPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成entity代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateEntityCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateEntity()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputEntityPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getEntityPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getEntityPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成DTO代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateDTOCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateDTO()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputDTOPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getDtoPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getDtoPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成Query代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateQueryCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateQuery()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputQueryPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getQueryPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getQueryPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成Service代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateServiceCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputServicePath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getServicePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getServicePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成ServiceImpl代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateServiceImplCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputServiceImplPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getServiceImplPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getServiceImplPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成Mapper代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateMapperCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateMapper()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapperPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getMapperPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapperPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成MapperXml代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateMapperXmlCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateMapperXml()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapperXmlPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getMapperXmlPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapperXmlPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 生成MapStruct代码
     *
     * @param template 模板
     * @param map      地图
     */
    public int generateMapStructCode(Template template, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGeneratorGlobalObj.getGenerateMapStruct()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapStructPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgGeneratorTableObj.getMapstructPath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGeneratorGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapstructPath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return count;
    }
}
