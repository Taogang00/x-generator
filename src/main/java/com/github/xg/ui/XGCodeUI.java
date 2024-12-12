package com.github.xg.ui;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.*;
import com.github.xg.render.XGTableListCellRenderer;
import com.github.xg.utils.XGFileUtil;
import com.github.xg.utils.XGModuleUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.fields.ExpandableTextField;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.DOT;
import static com.github.xg.constant.XGConstants.*;
import static com.github.xg.utils.XGTemplateUtil.getFreemarkerTemplate;

/**
 * 代码生成器 主页的UI
 *
 * @author taogang
 * @date 2024/11/19
 */
public class XGCodeUI {

    @Getter
    private JPanel rootJPanel;
    @Getter
    private JLabel runInfoLabel;
    @Getter
    private JComboBox<String> configComboBox;
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
    private JList<String> tableList;
    private JTextField removeClassNamePrefixTextField;
    private JTextField addClassNamePrefixTextFieldTextField;
    private JTextField authorTextField;
    private List<XGXmlElementTable> tableInfoList;
    private List<? extends String> xgGeneratorSelectedTableValuesList;

    private final List<XgTableObj> xgGeneratorSelectedTableObjList;
    private final XGGlobalObj xgGlobalObj;

    public XGCodeUI(Project project, XGGlobalObj xgGlobalObj) {
        this.xgGlobalObj = xgGlobalObj;
        this.skipRadioButton.setActionCommand("0");
        this.overrideRadioButton.setActionCommand("1");
        this.importBtn.setIcon(AllIcons.General.OpenDisk);
        this.runInfoLabel.setIcon(AllIcons.General.Information);
        this.authorTextField.setText(System.getProperty("user.name"));
        this.packageAllBtn.setText("全不选");

        this.xgGeneratorSelectedTableValuesList = new ArrayList<>();
        this.xgGeneratorSelectedTableObjList = new ArrayList<>();
        xgGlobalObj.setAuthor(authorTextField.getText());

        //配置的选项
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> valuesList = state.getXgConfigs();
        for (int i = 0; i < valuesList.size(); i++) {
            XGConfig config = valuesList.get(i);
            configComboBox.addItem(config.getName());
            if (config.getIsDefault()) {
                configComboBox.setSelectedIndex(i);
            }
        }

        // 1.项目模块加载
        Module[] modules = XGModuleUtil.getModules(project);
        for (Module module : modules) {
            projectModuleComboBox.addItem(module.getName());
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
        controllerCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateController(e.getStateChange() == ItemEvent.SELECTED));
        serviceCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateService(e.getStateChange() == ItemEvent.SELECTED));
        entityCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateEntity(e.getStateChange() == ItemEvent.SELECTED));
        dtoCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateDTO(e.getStateChange() == ItemEvent.SELECTED));
        queryCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateQuery(e.getStateChange() == ItemEvent.SELECTED));
        mapStructCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateMapStruct(e.getStateChange() == ItemEvent.SELECTED));
        mapperCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateMapper(e.getStateChange() == ItemEvent.SELECTED));
        mapperXmlCheckBox.addItemListener(e -> this.xgGlobalObj.setGenerateMapperXml(e.getStateChange() == ItemEvent.SELECTED));

        // 4.添加ActionListener来监听文件冲突时按钮的状态变化
        ActionListener actionListener = e -> {
            // 获取选中的 JRadioButton
            JRadioButton selectedButton = (JRadioButton) e.getSource();
            if ("1".equals(selectedButton.getActionCommand())) {
                this.xgGlobalObj.setFileOverride(true);
            } else if ("0".equals(selectedButton.getActionCommand())) {
                this.xgGlobalObj.setFileOverride(false);
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
            VirtualFile virtualFile = XGFileUtil.chooseFileVirtual(project);
            if (ObjectUtil.isNull(virtualFile)) {
                return;
            }
            String path = virtualFile.getPath();
            tableList.setListData(new String[0]); // 清空JList内容
            this.tableInfoList = importTableXml(path);

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

        // 9. 表单元素输入监听
        authorTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setAuthor(authorTextField.getText());
            }
        });
        removeClassNamePrefixTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setRemoveClassNamePrefix(removeClassNamePrefixTextField.getText());
            }
        });
        addClassNamePrefixTextFieldTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setAddClassNamePrefix(addClassNamePrefixTextFieldTextField.getText());
            }
        });
        sourceCodeGeneratorPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setSourceCodeGeneratorPath(sourceCodeGeneratorPathTextField.getText());
            }
        });
        resourcesCodeGeneratorPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setResourcesCodeGeneratorPath(resourcesCodeGeneratorPathTextField.getText());
            }
        });
        controllerPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setControllerPackagePath(controllerPathTextField.getText());
            }
        });
        servicePathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setServicePackagePath(servicePathTextField.getText());
            }
        });
        entityPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setEntityPackagePath(entityPathTextField.getText());
            }
        });
        dtoPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setDtoPackagePath(dtoPathTextField.getText());
            }
        });
        queryPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setQueryPackagePath(queryPathTextField.getText());
            }
        });
        mapStructPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setMapstructPackagePath(mapStructPathTextField.getText());
            }
        });
        mapperPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setMapperPackagePath(mapperPathTextField.getText());
            }
        });
        mapperXmlPathTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                xgGlobalObj.setMapperXmlPackagePath(mapperXmlPathTextField.getText());
            }
        });
    }

    /**
     * 导入表 XML
     *
     * @param path 路径
     * @return {@link List }<{@link XGXmlElementTable }>
     */
    public List<XGXmlElementTable> importTableXml(String path) {
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
                    String nullOption = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_NULL_OPTION);
                    String dataLength = columnElement.getAttribute(XML_ELEMENT_COLUMN_ATTRIBUTE_DATA_LENGTH);

                    XGXmlElementColumn XGXmlElementColumn = new XGXmlElementColumn();
                    XGXmlElementColumn.setName(columnText);
                    XGXmlElementColumn.setFieldName(columnName);
                    XGXmlElementColumn.setFieldType(dataType);
                    XGXmlElementColumn.setDataLength(Integer.valueOf(dataLength));
                    XGXmlElementColumn.setNullOption(Boolean.valueOf(nullOption));
                    XGXmlElementColumn.setPrimaryKey(Boolean.valueOf(primaryKey));
                    XGXmlElementTable.getColumnList().add(XGXmlElementColumn);
                }
                list.add(XGXmlElementTable);
            }
            this.runInfoLabel.setText("已导入" + list.size() + "张表");
            this.runInfoLabel.setIcon(AllIcons.General.Information);
        } catch (Exception e) {
            this.runInfoLabel.setText("XML文件解析错误!");
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
        String sourcePath = XGModuleUtil.getModuleSourcePath(project, selectedItem);
        Assert.notNull(sourcePath, "未识别到项目资源路径");
        File sourceDirectory = new File(sourcePath);
        //src/main/source 绝对地址目录，形如：D:\gogs\camel\2.src\tles-oles-camel-out\src\main\resource
        String resourcePath = XGModuleUtil.getModuleReSourcePath(project, selectedItem);
        Assert.notNull(resourcePath, "未识别到项目资源路径");
        File resourceDirectory = new File(resourcePath);

        String sourceDirectoryAbsolutePath = sourceDirectory.getAbsolutePath();
        String resourceDirectoryAbsolutePath = resourceDirectory.getAbsolutePath();

        this.sourceCodeGeneratorPathTextField.setText(sourceDirectoryAbsolutePath);
        this.resourcesCodeGeneratorPathTextField.setText(resourceDirectoryAbsolutePath);
        this.xgGlobalObj.setSourceCodeGeneratorPath(sourceDirectoryAbsolutePath);
        this.xgGlobalObj.setResourcesCodeGeneratorPath(resourceDirectoryAbsolutePath);

        File file = XGFileUtil.walkFiles(sourceDirectory);
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

        this.xgGlobalObj.setControllerPackagePath(controllerPackagePath);
        this.xgGlobalObj.setServicePackagePath(servicePackagePath);
        this.xgGlobalObj.setMapperPackagePath(mapperPackagePath);
        this.xgGlobalObj.setEntityPackagePath(entityPackagePath);
        this.xgGlobalObj.setDtoPackagePath(dtoPackagePath);
        this.xgGlobalObj.setQueryPackagePath(queryPackagePath);
        this.xgGlobalObj.setMapstructPackagePath(mapStructPackagePath);
        this.xgGlobalObj.setMapperXmlPackagePath("mapper");

        this.controllerPathTextField.setText(controllerPackagePath);
        this.servicePathTextField.setText(this.xgGlobalObj.getServicePackagePath());
        this.mapperPathTextField.setText(this.xgGlobalObj.getMapperPackagePath());
        this.entityPathTextField.setText(this.xgGlobalObj.getEntityPackagePath());
        this.dtoPathTextField.setText(this.xgGlobalObj.getDtoPackagePath());
        this.queryPathTextField.setText(this.xgGlobalObj.getQueryPackagePath());
        this.mapStructPathTextField.setText(this.xgGlobalObj.getMapstructPackagePath());
        this.mapperXmlPathTextField.setText(this.xgGlobalObj.getMapperXmlPackagePath());
    }

    /**
     * 初始化需要生成的表对象
     *
     * @param selectedValuesList Selected Values 列表
     */
    public void initXgGeneratorSelectedTableValuesList(List<? extends String> selectedValuesList) {
        this.xgGeneratorSelectedTableValuesList = selectedValuesList;
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
            Messages.showInfoMessage("请先导入表实体数据！", "X-Generator");
            return;
        }
        if (this.xgGeneratorSelectedTableValuesList.isEmpty()) {
            Messages.showInfoMessage("请先选择要生成的表实体！", "X-Generator");
            return;
        }
        if (!controllerCheckBox.isSelected() && !entityCheckBox.isSelected()
                && !mapStructCheckBox.isSelected() && !queryCheckBox.isSelected() && !mapperXmlCheckBox.isSelected()
                && !mapperCheckBox.isSelected() && !serviceCheckBox.isSelected() && !dtoCheckBox.isSelected()) {
            Messages.showInfoMessage("请先选择要生成的代码对象！", "X-Generator");
            return;
        }

        for (String s : xgGeneratorSelectedTableValuesList) {
            XGXmlElementTable xgXmlElementTable = tableInfoMap.get(s);
            String elementTableName = xgXmlElementTable.getName();

            XgTableObj xgTableObj = new XgTableObj();
            xgTableObj.setTableName(elementTableName);
            xgTableObj.setTableComment(xgXmlElementTable.getComment());
            //entity
            xgTableObj.setEntityClassName(elementTableName);
            xgTableObj.setEntityPackagePath(xgGlobalObj.getEntityPackagePath());
            xgTableObj.setEntityAbsolutePath(xgGlobalObj.getOutputEntityPath() + File.separator + xgTableObj.getEntityClassName() + ".java");
            //mapper
            xgTableObj.setMapperClassName(elementTableName + "Mapper");
            xgTableObj.setMapperPackagePath(xgGlobalObj.getMapperPackagePath());
            xgTableObj.setMapperAbsolutePath(xgGlobalObj.getOutputEntityPath() + File.separator + xgTableObj.getMapstructClassName() + ".java");
            //mapper-xml
            xgTableObj.setMapperXml(elementTableName + "Mapper");
            xgTableObj.setMapperXmlPackagePath(xgGlobalObj.getOutputMapperXmlPath());
            xgTableObj.setMapperXmlAbsolutePath(xgGlobalObj.getOutputMapperXmlPath() + File.separator + xgTableObj.getMapperXml() + ".xml");
            //service
            xgTableObj.setServiceClassName(elementTableName + "Service");
            xgTableObj.setServicePackagePath(xgGlobalObj.getServicePackagePath());
            xgTableObj.setServiceAbsolutePath(xgGlobalObj.getOutputServicePath() + File.separator + xgTableObj.getServiceClassName() + ".java");
            //service-impl
            xgTableObj.setServiceImplClassName(elementTableName + "ServiceImpl");
            xgTableObj.setServiceImplPackagePath(xgGlobalObj.getServiceImplPackagePath());
            xgTableObj.setServiceImplAbsolutePath(xgGlobalObj.getOutputServiceImplPath() + File.separator + xgTableObj.getServiceImplClassName() + ".java");
            //dto
            xgTableObj.setDtoClassName(elementTableName + "DTO");
            xgTableObj.setDtoPackagePath(xgGlobalObj.getDtoPackagePath());
            xgTableObj.setDtoAbsolutePath(xgGlobalObj.getOutputDTOPath() + File.separator + xgTableObj.getDtoClassName() + ".java");
            //query
            xgTableObj.setQueryClassName(elementTableName + "Query");
            xgTableObj.setQueryPackagePath(xgGlobalObj.getQueryPackagePath());
            xgTableObj.setQueryAbsolutePath(xgGlobalObj.getOutputQueryPath() + File.separator + xgTableObj.getQueryClassName() + ".java");
            //controller
            xgTableObj.setControllerClassName(elementTableName + "Controller");
            xgTableObj.setControllerPackagePath(xgGlobalObj.getControllerPackagePath());
            xgTableObj.setControllerAbsolutePath(xgGlobalObj.getOutputControllerPath() + File.separator + xgTableObj.getControllerClassName() + ".java");
            //mapstruct
            xgTableObj.setMapstructClassName(elementTableName + "Mapstruct");
            xgTableObj.setMapstructPackagePath(xgGlobalObj.getMapstructPackagePath());
            xgTableObj.setMapstructAbsolutePath(xgGlobalObj.getOutputMapStructPath() + File.separator + xgTableObj.getMapstructClassName() + ".java");

            XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig(configComboBox.getSelectedItem().toString());
            Map<String, String> columnJavaTypeMapping = selectXGConfig.getColumnJavaTypeMapping();

            List<XGTableFieldsObj> tableFields = new ArrayList<>();
            for (XGXmlElementColumn columnInfo : xgXmlElementTable.getColumnList()) {
                XGTableFieldsObj xgTableFieldsObj = new XGTableFieldsObj();
                xgTableFieldsObj.setComment(columnInfo.getName());
                xgTableFieldsObj.setPrimaryKey(columnInfo.getPrimaryKey());
                xgTableFieldsObj.setNullOption(columnInfo.getNullOption());
                xgTableFieldsObj.setDataLength(columnInfo.getDataLength());
                xgTableFieldsObj.setPropertyName(StrUtil.lowerFirst(columnInfo.getFieldName()));
                for (Map.Entry<String, String> regexEntry : columnJavaTypeMapping.entrySet()) {
                    if (StrUtil.isNotBlank(regexEntry.getValue())) {
                        if (ReUtil.isMatch(regexEntry.getKey(), columnInfo.getFieldType().toLowerCase())) {
                            String value = regexEntry.getValue();
                            xgTableFieldsObj.setPropertyType(value.substring(value.lastIndexOf(".") + 1));
                            xgTableFieldsObj.setPropertyClass(value);
                        }
                    }
                }
                tableFields.add(xgTableFieldsObj);
            }
            xgTableObj.setTableFields(tableFields);
            xgGeneratorSelectedTableObjList.add(xgTableObj);
        }

        //去掉统一前缀
        for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
            String controllerName = StrUtil.removePrefix(xgTableObj.getControllerClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            controllerName = this.xgGlobalObj.getAddClassNamePrefix() + controllerName;
            xgTableObj.setControllerClassName(controllerName);

            String serviceName = StrUtil.removePrefix(xgTableObj.getServiceClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            serviceName = this.xgGlobalObj.getAddClassNamePrefix() + serviceName;
            xgTableObj.setServiceClassName(serviceName);

            String serviceImplName = StrUtil.removePrefix(xgTableObj.getServiceImplClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            serviceImplName = this.xgGlobalObj.getAddClassNamePrefix() + serviceImplName;
            xgTableObj.setServiceImplClassName(serviceImplName);

            String mapperName = StrUtil.removePrefix(xgTableObj.getMapperClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            mapperName = this.xgGlobalObj.getAddClassNamePrefix() + mapperName;
            xgTableObj.setMapperClassName(mapperName);

            String dtoName = StrUtil.removePrefix(xgTableObj.getDtoClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            dtoName = this.xgGlobalObj.getAddClassNamePrefix() + dtoName;
            xgTableObj.setDtoClassName(dtoName);

            String entityName = StrUtil.removePrefix(xgTableObj.getEntityClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            entityName = this.xgGlobalObj.getAddClassNamePrefix() + entityName;
            xgTableObj.setEntityClassName(entityName);

            String queryName = StrUtil.removePrefix(xgTableObj.getQueryClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            queryName = this.xgGlobalObj.getAddClassNamePrefix() + queryName;
            xgTableObj.setQueryClassName(queryName);

            String mapStructName = StrUtil.removePrefix(xgTableObj.getMapstructClassName(), this.xgGlobalObj.getRemoveClassNamePrefix());
            mapStructName = this.xgGlobalObj.getAddClassNamePrefix() + mapStructName;
            xgTableObj.setMapstructClassName(mapStructName);

            String xmlName = StrUtil.removePrefix(xgTableObj.getMapperXml(), this.xgGlobalObj.getRemoveClassNamePrefix());
            xmlName = this.xgGlobalObj.getAddClassNamePrefix() + xmlName;
            xgTableObj.setMapperXml(xmlName);

            xgTableObj.setDtoAbsolutePath(xgGlobalObj.getOutputDTOPath() + File.separator + xgTableObj.getDtoClassName() + ".java");
            xgTableObj.setControllerAbsolutePath(xgGlobalObj.getOutputControllerPath() + File.separator + xgTableObj.getControllerClassName() + ".java");
            xgTableObj.setEntityAbsolutePath(xgGlobalObj.getOutputEntityPath() + File.separator + xgTableObj.getEntityClassName() + ".java");
            xgTableObj.setServiceImplAbsolutePath(xgGlobalObj.getOutputServiceImplPath() + File.separator + xgTableObj.getServiceImplClassName() + ".java");
            xgTableObj.setServiceAbsolutePath(xgGlobalObj.getOutputServicePath() + File.separator + xgTableObj.getServiceClassName() + ".java");
            xgTableObj.setQueryAbsolutePath(xgGlobalObj.getOutputQueryPath() + File.separator + xgTableObj.getQueryClassName() + ".java");
            xgTableObj.setMapstructAbsolutePath(xgGlobalObj.getOutputMapStructPath() + File.separator + xgTableObj.getMapstructClassName() + ".java");
            xgTableObj.setMapperAbsolutePath(xgGlobalObj.getOutputMapperPath() + File.separator + xgTableObj.getMapperClassName() + ".java");
            xgTableObj.setMapperXmlAbsolutePath(xgGlobalObj.getOutputMapperXmlPath() + File.separator + xgTableObj.getMapperXml() + ".xml");
        }

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> xgGlobalInfoMap = BeanUtil.beanToMap(this.xgGlobalObj);
        map.put("global", xgGlobalInfoMap);

        int count = 0;
        XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig(configComboBox.getSelectedItem().toString());
        List<XGTabInfo> xgTabInfoList = selectXGConfig.getXgTabInfoList();
        for (XGTabInfo xgTabInfo : xgTabInfoList) {
            if (CONTROLLER.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateControllerCode(template, map);
            }
            if (ENTITY.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateEntityCode(template, map);
            }
            if (DTO.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateDTOCode(template, map);
            }
            if (QUERY.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateQueryCode(template, map);
            }
            if (SERVICE.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateServiceCode(template, map);
            }
            if (SERVICE_IMPL.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateServiceImplCode(template, map);
            }
            if (MAPPER.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateMapperCode(template, map);
            }
            if (XML.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateMapperXmlCode(template, map);
            }
            if (MAPSTRUCT.equals(xgTabInfo.getType())) {
                Template template = getFreemarkerTemplate(xgTabInfo.getContent(), xgTabInfo.getType());
                count += generateMapStructCode(template, map);
            }
        }
        Messages.showInfoMessage("生成成功，共有 " + count + " 个文件发生变化", "X-Generator");
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
        if (xgGlobalObj.getGenerateController()) {
            Path path = Paths.get(xgGlobalObj.getOutputControllerPath());
            // 在使用 FileOutputStream 时，如果文件的父目录不存在（即文件所在的文件夹），Java 会抛出 FileNotFoundException，即使你尝试创建一个新的文件。
            // 为了避免这个问题，你需要确保文件的父目录已经存在。如果目录不存在，你需要手动创建它。
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getControllerAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getControllerAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateEntity()) {
            Path path = Paths.get(xgGlobalObj.getOutputEntityPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getEntityAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getEntityAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateDTO()) {
            Path path = Paths.get(xgGlobalObj.getOutputDTOPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getDtoAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getDtoAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateQuery()) {
            Path path = Paths.get(xgGlobalObj.getOutputQueryPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getQueryAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getQueryAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGlobalObj.getOutputServicePath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getServiceAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getServiceAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGlobalObj.getOutputServiceImplPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getServiceImplAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getServiceImplAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateMapper()) {
            Path path = Paths.get(xgGlobalObj.getOutputMapperPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getMapperAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getMapperAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateMapperXml()) {
            Path path = Paths.get(xgGlobalObj.getOutputMapperXmlPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getMapperXmlAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getMapperXmlAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
        if (xgGlobalObj.getGenerateMapStruct()) {
            Path path = Paths.get(xgGlobalObj.getOutputMapStructPath());
            Files.createDirectories(path);

            for (XgTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getMapstructAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && this.xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getMapstructAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
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
