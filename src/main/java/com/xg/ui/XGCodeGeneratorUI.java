package com.xg.ui;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xg.model.XGXmlElementColumn.*;
import static com.xg.model.XGXmlElementTable.*;

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
    private List<XGXmlElementTable> tableInfoList;
    private final List<XgGeneratorTableObj> xgGeneratorTableObjList;
    private final XGGeneratorGlobalObj xgGeneratorGlobalObj;

    public XGCodeGeneratorUI(Project project) {
        this.xgGeneratorGlobalObj = new XGGeneratorGlobalObj();

        this.settingBtn.setIcon(AllIcons.General.Settings);
        this.importBtn.setIcon(AllIcons.ToolbarDecorator.Import);
        this.authorTextField.setText(System.getProperty("user.name"));
        this.packageAllBtn.setText("全不选");
        this.xgGeneratorTableObjList = new ArrayList<>();
        this.xgGeneratorGlobalObj.setDateTime(DateUtil.formatDateTime(new Date()));
        this.xgGeneratorGlobalObj.setAuthor(authorTextField.getText());

        for (String s : XGMavenUtil.getMavenArtifactId(project)) {
            projectModuleComboBox.addItem(s);
        }

        // 作者，从系统中读取
        authorTextField.addActionListener(e -> this.xgGeneratorGlobalObj.setAuthor(authorTextField.getText()));

        // 生成对象
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

        // 生成与否
        controllerCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateController(e.getStateChange() == ItemEvent.SELECTED));
        serviceCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateService(e.getStateChange() == ItemEvent.SELECTED));
        entityCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateEntity(e.getStateChange() == ItemEvent.SELECTED));
        dtoCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateDTO(e.getStateChange() == ItemEvent.SELECTED));
        queryCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateQuery(e.getStateChange() == ItemEvent.SELECTED));
        mapStructCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapStruct(e.getStateChange() == ItemEvent.SELECTED));
        mapperCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapper(e.getStateChange() == ItemEvent.SELECTED));
        mapXmlCheckBox.addItemListener(e -> this.xgGeneratorGlobalObj.setGenerateMapperXml(e.getStateChange() == ItemEvent.SELECTED));

        // 选择项目时需要给代码生成的路径进行赋值
        projectModuleComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                initSelectedModulePackage(project, e.getItem().toString());
            }
        });

        // 导入xml按钮事件
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

        // 初始化包赋值操作
        if (ObjectUtil.isNotNull(projectModuleComboBox.getSelectedItem())) {
            initSelectedModulePackage(project, projectModuleComboBox.getSelectedItem().toString());
        }
    }

    /**
     * 导入表 XML
     *
     * @param path         路径
     * @param runInfoLabel Run Info 标签
     * @return {@link List }<{@link XGXmlElementTable }>
     */
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

        runInfoLabel.setText("已导入" + list.size() + "张表");
        return list;
    }

    /**
     * 初始化选定模块包
     *
     * @param project      项目
     * @param selectedItem 已选项
     */
    private void initSelectedModulePackage(Project project, String selectedItem) {
        File sourcePath = XGMavenUtil.getMavenArtifactIdSourcePath(project, selectedItem);
        File resourcePath = XGMavenUtil.getMavenArtifactIdResourcePath(project, selectedItem);
        assert sourcePath != null;
        assert resourcePath != null;

        codeGeneratorPathTextField.setText(sourcePath.getAbsolutePath());
        File file = XGFileChooserUtil.walkFiles(sourcePath);
        String absolutePath = file.getAbsolutePath();

        this.xgGeneratorGlobalObj.setOutputControllerPath(absolutePath + File.separator + "controller");
        this.xgGeneratorGlobalObj.setOutputEntityPath(absolutePath + File.separator + "entity");
        this.xgGeneratorGlobalObj.setOutputServicePath(absolutePath + File.separator + "service");
        this.xgGeneratorGlobalObj.setOutputServiceImplPath(absolutePath + File.separator + "service" + File.separator + "impl");
        this.xgGeneratorGlobalObj.setOutputQueryPath(absolutePath + File.separator + "query");
        this.xgGeneratorGlobalObj.setOutputDTOPath(absolutePath + File.separator + "dto");
        this.xgGeneratorGlobalObj.setOutputMapperPath(absolutePath + File.separator + "mapper");
        this.xgGeneratorGlobalObj.setOutputMapStructPath(absolutePath + File.separator + "mapstruct");
        this.xgGeneratorGlobalObj.setOutputMapperXmlPath(resourcePath + File.separator + "mapper");

        String modulePath = absolutePath.replace(sourcePath.getAbsolutePath() + File.separator, "");
        modulePath = modulePath.replace(File.separator, ".");
        this.xgGeneratorGlobalObj.setModulePackagePath(modulePath);

        this.xgGeneratorGlobalObj.setControllerPackagePath(modulePath + "." + "controller");
        this.xgGeneratorGlobalObj.setServicePackagePath(modulePath + "." + "service");
        this.xgGeneratorGlobalObj.setServiceImplPackagePath(modulePath + "." + "service.impl");
        this.xgGeneratorGlobalObj.setMapperPackagePath(modulePath + "." + "mapper");
        this.xgGeneratorGlobalObj.setEntityPackagePath(modulePath + "." + "entity");
        this.xgGeneratorGlobalObj.setDtoPackagePath(modulePath + "." + "dto");
        this.xgGeneratorGlobalObj.setQueryPackagePath(modulePath + "." + "query");
        this.xgGeneratorGlobalObj.setMapstructPackagePath(modulePath + "." + "mapstruct");
        this.xgGeneratorGlobalObj.setMapperXmlPackagePath("mapper");

        controllerPathTextField.setText(this.xgGeneratorGlobalObj.getControllerPackagePath());
        servicePathTextField.setText(this.xgGeneratorGlobalObj.getServicePackagePath());
        mapperPathTextField.setText(this.xgGeneratorGlobalObj.getMapperPackagePath());
        entityPathTextField.setText(this.xgGeneratorGlobalObj.getEntityPackagePath());
        dtoPathTextField.setText(this.xgGeneratorGlobalObj.getDtoPackagePath());
        queryPathTextField.setText(this.xgGeneratorGlobalObj.getQueryPackagePath());
        mapStructPathTextField.setText(this.xgGeneratorGlobalObj.getMapstructPackagePath());
        mapperXmlPathTextField.setText(this.xgGeneratorGlobalObj.getMapperXmlPackagePath());
    }

    /**
     * 初始化需要生成的表对象
     *
     * @param selectedValuesList Selected Values 列表
     */
    public void initSelectXgGeneratorTableObj(List<? extends String> selectedValuesList) {
        xgGeneratorTableObjList.clear();
        for (String s : selectedValuesList) {
            XGXmlElementTable xgXmlElementTable = tableInfoMap.get(s);
            String elementTableName = xgXmlElementTable.getName();
            String tableObj = elementTableName.replace("_", "");

            XgGeneratorTableObj xgGeneratorTableObj = new XgGeneratorTableObj();
            xgGeneratorTableObj.setTableName(elementTableName);
            xgGeneratorTableObj.setTableComment(xgXmlElementTable.getComment());
            //entity
            xgGeneratorTableObj.setEntityClassName(tableObj);
            xgGeneratorTableObj.setEntityPackagePath(xgGeneratorGlobalObj.getEntityPackagePath());
            xgGeneratorTableObj.setEntityPath(xgGeneratorGlobalObj.getOutputEntityPath() + File.separator + tableObj + ".java");
            //mapper
            xgGeneratorTableObj.setMapperClassName(tableObj + "Mapper");
            xgGeneratorTableObj.setMapperPackagePath(xgGeneratorGlobalObj.getMapperPackagePath());
            xgGeneratorTableObj.setMapperPath(xgGeneratorGlobalObj.getOutputEntityPath() + File.separator + tableObj + "Mapper.java");
            //mapper-xml
            xgGeneratorTableObj.setMapXml(tableObj + "Mapper");
            //TODO 不用赋值
            xgGeneratorTableObj.setMapXmlPackagePath(xgGeneratorGlobalObj.getOutputMapperXmlPath());
            xgGeneratorTableObj.setMapXmlPath(xgGeneratorGlobalObj.getOutputMapperXmlPath() + File.separator + tableObj + "Mapper.xml");
            //service
            xgGeneratorTableObj.setServiceClassName(tableObj + "Service");
            xgGeneratorTableObj.setServicePackagePath(xgGeneratorGlobalObj.getServicePackagePath());
            xgGeneratorTableObj.setServicePath(xgGeneratorGlobalObj.getOutputServicePath() + File.separator + tableObj + "Service.java");
            //service-impl
            xgGeneratorTableObj.setServiceImplClassName(tableObj + "ServiceImpl");
            xgGeneratorTableObj.setServiceImplPackagePath(xgGeneratorGlobalObj.getServiceImplPackagePath());
            xgGeneratorTableObj.setServiceImplPath(xgGeneratorGlobalObj.getOutputServiceImplPath() + File.separator + tableObj + "ServiceImpl.java");
            //dto
            xgGeneratorTableObj.setDtoClassName(tableObj + "DTO");
            xgGeneratorTableObj.setDtoPackagePath(xgGeneratorGlobalObj.getDtoPackagePath());
            xgGeneratorTableObj.setDtoPath(xgGeneratorGlobalObj.getOutputDTOPath() + File.separator + tableObj + "DTO.java");
            //query
            xgGeneratorTableObj.setQueryClassName(tableObj + "Query");
            xgGeneratorTableObj.setQueryPackagePath(xgGeneratorGlobalObj.getQueryPackagePath());
            xgGeneratorTableObj.setQueryPath(xgGeneratorGlobalObj.getOutputQueryPath() + File.separator + tableObj + "Query.java");
            //controller
            xgGeneratorTableObj.setControllerClassName(tableObj + "Controller");
            xgGeneratorTableObj.setControllerPackagePath(xgGeneratorGlobalObj.getControllerPackagePath());
            xgGeneratorTableObj.setControllerPath(xgGeneratorGlobalObj.getOutputControllerPath() + File.separator + tableObj + "Controller.java");
            //mapstruct
            xgGeneratorTableObj.setMapstructClassName(tableObj + "Mapstruct");
            xgGeneratorTableObj.setMapstructPackagePath(xgGeneratorGlobalObj.getMapstructPackagePath());
            xgGeneratorTableObj.setMapstructPath(xgGeneratorGlobalObj.getOutputMapStructPath() + File.separator + tableObj + "Mapstruct.java");

            List<XGGeneratorTableFieldsObj> tableFields = new ArrayList<>();
            for (XGXmlElementColumn columnInfo : xgXmlElementTable.getColumnList()) {
                XGGeneratorTableFieldsObj xgGeneratorTableFieldsObj = new XGGeneratorTableFieldsObj();
                xgGeneratorTableFieldsObj.setComment(columnInfo.getName());
                xgGeneratorTableFieldsObj.setPrimaryKey(columnInfo.getPrimaryKey());
                xgGeneratorTableFieldsObj.setPropertyName(StrUtil.lowerFirst(columnInfo.getFieldName()));
                //TODO转换
                xgGeneratorTableFieldsObj.setPropertyType(columnInfo.getFieldType());
                tableFields.add(xgGeneratorTableFieldsObj);
            }
            xgGeneratorTableObj.setTableFields(tableFields);
            xgGeneratorTableObjList.add(xgGeneratorTableObj);
        }
    }

    /**
     * 从字符串获取模板
     *
     * @param templateContent 模板内容
     * @param templateName    模板名称
     * @return {@link Template }
     * @throws IOException io异常
     */
    public Template getTemplateFromString(String templateContent, String templateName) throws IOException {
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
    public void generateCode(Project project, XGMainDialog xgMainDialog) throws IOException {
        if (this.tableInfoList == null || this.tableInfoList.isEmpty()) {
            Messages.showDialog("请先导入表实体数据！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }
        if (this.xgGeneratorTableObjList.isEmpty()) {
            Messages.showDialog("请先选择要生成的表实体！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }
        if (!controllerCheckBox.isSelected() && !entityCheckBox.isSelected()
                && !mapStructCheckBox.isSelected() && !queryCheckBox.isSelected() && !mapXmlCheckBox.isSelected()
                && !mapperCheckBox.isSelected() && !serviceCheckBox.isSelected() && !dtoCheckBox.isSelected()) {
            Messages.showDialog("请先选择要生成的代码对象！", "操作提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> xgGlobalInfoMap = BeanUtil.beanToMap(this.xgGeneratorGlobalObj);
        map.put("global", xgGlobalInfoMap);

        //默认-生成controller
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/controller.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "controller");
            generateControllerCode(template, map);
        }

        //默认-生成entity
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/entity.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "entity");
            generateEntityCode(template, map);
        }

        //默认-生成dto
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/dto.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "dto");
            generateDTOCode(template, map);
        }

        //默认-生成query
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/query.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "query");
            generateQueryCode(template, map);
        }

        //默认-生成service
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/service.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "service");
            generateServiceCode(template, map);
        }

        //默认-生成serviceImpl
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/serviceImpl.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "serviceImpl");
            generateServiceImplCode(template, map);
        }

        //默认-生成mapper
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapper.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "mapper");
            generateMapperCode(template, map);
        }

        //默认-生成mapper-xml
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapper.xml.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "mapper-xml");
            generateMapperXmlCode(template, map);
        }

        //默认-生成mapstruct
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/mapstruct.java.ftl")) {
            assert resourceAsStream != null;
            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            Template template = getTemplateFromString(templateContent, "mapstruct");
            generateMapStructCode(template, map);
        }

        NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
        Notification notification = groupManager.getNotificationGroup("NotificationXg")
                .createNotification("生成成功", MessageType.INFO).setTitle("X-Generator");
        Notifications.Bus.notify(notification, project);
        xgMainDialog.doCancelAction();
    }

    /**
     * 生成entity代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateControllerCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateController()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputControllerPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getControllerPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成entity代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateEntityCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateEntity()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputEntityPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getEntityPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成DTO代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateDTOCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateDTO()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputDTOPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getDtoPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成Query代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateQueryCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateQuery()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputQueryPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getQueryPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成Service代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateServiceCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputServicePath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getServicePath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成ServiceImpl代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateServiceImplCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateService()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputServiceImplPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getServiceImplPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成Mapper代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateMapperCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateMapper()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapperPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapperPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成MapperXml代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateMapperXmlCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateMapperXml()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapperXmlPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapXmlPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 生成MapStruct代码
     *
     * @param template 模板
     * @param map      地图
     */
    public void generateMapStructCode(Template template, Map<String, Object> map) throws IOException {
        if (xgGeneratorGlobalObj.getGenerateMapStruct()) {
            Path path = Paths.get(xgGeneratorGlobalObj.getOutputMapStructPath());
            Files.createDirectories(path);

            for (XgGeneratorTableObj xgGeneratorTableObj : xgGeneratorTableObjList) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(xgGeneratorTableObj.getMapstructPath())) {
                    Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgGeneratorTableObj);
                    map.put("table", stringObjectMap);
                    template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                } catch (IOException | TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
