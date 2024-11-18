package com.xg.ui;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
    private final XGGlobalInfo xgGlobalInfo;

    public XGCodeGeneratorUI(Project project) {
        this.xgGlobalInfo = new XGGlobalInfo();

        this.settingBtn.setIcon(AllIcons.General.Settings);
        this.importBtn.setIcon(AllIcons.ToolbarDecorator.Import);
        this.authorTextField.setText(System.getProperty("user.name"));
        this.packageAllBtn.setText("全不选");
        this.xgGeneratorTableObjList = new ArrayList<>();
        this.xgGlobalInfo.setDateTime(DateUtil.formatDateTime(new Date()));
        this.xgGlobalInfo.setAuthor(authorTextField.getText());

        for (String s : XGMavenUtil.getMavenArtifactId(project)) {
            projectModuleComboBox.addItem(s);
        }

        //作者，从系统中读取
        authorTextField.addActionListener(e -> this.xgGlobalInfo.setAuthor(authorTextField.getText()));

        //生成对象
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
        controllerCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateController(e.getStateChange() == ItemEvent.SELECTED));
        serviceCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateService(e.getStateChange() == ItemEvent.SELECTED));
        entityCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateEntity(e.getStateChange() == ItemEvent.SELECTED));
        dtoCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateDTO(e.getStateChange() == ItemEvent.SELECTED));
        queryCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateQuery(e.getStateChange() == ItemEvent.SELECTED));
        mapStructCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateMapStruct(e.getStateChange() == ItemEvent.SELECTED));
        mapperCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateMapper(e.getStateChange() == ItemEvent.SELECTED));
        mapXmlCheckBox.addItemListener(e -> this.xgGlobalInfo.setGenerateMapperXml(e.getStateChange() == ItemEvent.SELECTED));

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

        this.xgGlobalInfo.setOutputControllerPath(absolutePath + File.separator + "controller");
        this.xgGlobalInfo.setOutputEntityPath(absolutePath + File.separator + "entity");
        this.xgGlobalInfo.setOutputServicePath(absolutePath + File.separator + "service");
        this.xgGlobalInfo.setOutputServiceImplPath(absolutePath + File.separator + "service" + File.separator + "impl");
        this.xgGlobalInfo.setOutputQueryPath(absolutePath + File.separator + "query");
        this.xgGlobalInfo.setOutputDTOPath(absolutePath + File.separator + "dto");
        this.xgGlobalInfo.setOutputMapperPath(absolutePath + File.separator + "mapper");
        this.xgGlobalInfo.setOutputMapStructPath(absolutePath + File.separator + "mapstruct");
        this.xgGlobalInfo.setOutputMapperXmlPath(resourcePath + File.separator + "mapper");

        String modulePath = absolutePath.replace(sourcePath.getAbsolutePath() + File.separator, "");
        modulePath = modulePath.replace(File.separator, ".");
        this.xgGlobalInfo.setModulePackagePath(modulePath);

        this.xgGlobalInfo.setControllerPackagePath(modulePath + "." + "controller");
        this.xgGlobalInfo.setServicePackagePath(modulePath + "." + "service");
        this.xgGlobalInfo.setServiceImplPackagePath(modulePath + "." + "service.impl");
        this.xgGlobalInfo.setMapperPackagePath(modulePath + "." + "mapper");
        this.xgGlobalInfo.setEntityPackagePath(modulePath + "." + "entity");
        this.xgGlobalInfo.setDtoPackagePath(modulePath + "." + "dto");
        this.xgGlobalInfo.setQueryPackagePath(modulePath + "." + "query");
        this.xgGlobalInfo.setMapstructPackagePath(modulePath + "." + "mapstruct");
        this.xgGlobalInfo.setMapperXmlPackagePath("mapper");

        controllerPathTextField.setText(this.xgGlobalInfo.getControllerPackagePath());
        servicePathTextField.setText(this.xgGlobalInfo.getServicePackagePath());
        mapperPathTextField.setText(this.xgGlobalInfo.getMapperPackagePath());
        entityPathTextField.setText(this.xgGlobalInfo.getEntityPackagePath());
        dtoPathTextField.setText(this.xgGlobalInfo.getDtoPackagePath());
        queryPathTextField.setText(this.xgGlobalInfo.getQueryPackagePath());
        mapStructPathTextField.setText(this.xgGlobalInfo.getMapstructPackagePath());
        mapperXmlPathTextField.setText(this.xgGlobalInfo.getMapperXmlPackagePath());
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
            xgGeneratorTableObj.setEntityPackagePath(xgGlobalInfo.getEntityPackagePath());
            xgGeneratorTableObj.setEntityPath(xgGlobalInfo.getOutputEntityPath() + File.separator + tableObj + ".java");
            //mapper
            xgGeneratorTableObj.setMapperClassName(tableObj + "Mapper");
            xgGeneratorTableObj.setMapperPath(xgGlobalInfo.getMapperPackagePath());
            xgGeneratorTableObj.setMapperPackagePath(xgGlobalInfo.getOutputEntityPath() + File.separator + tableObj + "Mapper.java");
            //mapper-xml
            xgGeneratorTableObj.setMapXml(tableObj + "Mapper");
            xgGeneratorTableObj.setMapXmlPath(xgGlobalInfo.getOutputMapperXmlPath());
            xgGeneratorTableObj.setMapXmlPackagePath(xgGlobalInfo.getOutputMapperXmlPath() + File.separator + tableObj + "Mapper.xml");
            //service
            xgGeneratorTableObj.setServiceClassName(tableObj + "Service");
            xgGeneratorTableObj.setServicePath(xgGlobalInfo.getServicePackagePath());
            xgGeneratorTableObj.setServicePackagePath(xgGlobalInfo.getOutputServicePath() + File.separator + tableObj + "Service.java");
            //service-impl
            xgGeneratorTableObj.setServiceImplClassName(tableObj + "ServiceImpl");
            xgGeneratorTableObj.setServiceImplPath(xgGlobalInfo.getServiceImplPackagePath());
            xgGeneratorTableObj.setServiceImplPackagePath(xgGlobalInfo.getOutputServiceImplPath() + File.separator + tableObj + "ServiceImpl.java");
            //dto
            xgGeneratorTableObj.setDtoClassName(tableObj + "DTO");
            xgGeneratorTableObj.setDtoPath(xgGlobalInfo.getDtoPackagePath());
            xgGeneratorTableObj.setDtoPackagePath(xgGlobalInfo.getOutputDTOPath() + File.separator + tableObj + "DTO.java");
            //query
            xgGeneratorTableObj.setQueryClassName(tableObj + "Query");
            xgGeneratorTableObj.setQueryPath(xgGlobalInfo.getQueryPackagePath());
            xgGeneratorTableObj.setQueryPackagePath(xgGlobalInfo.getOutputQueryPath() + File.separator + tableObj + "Query.java");
            //controller
            xgGeneratorTableObj.setControllerClassName(tableObj + "Controller");
            xgGeneratorTableObj.setControllerPath(xgGlobalInfo.getControllerPackagePath());
            xgGeneratorTableObj.setControllerPackagePath(xgGlobalInfo.getOutputControllerPath() + File.separator + tableObj + "Controller.java");
            //mapstruct
            xgGeneratorTableObj.setMapstructClassName(tableObj + "Mapstruct");
            xgGeneratorTableObj.setMapstructPath(xgGlobalInfo.getOutputMapStructPath());
            xgGeneratorTableObj.setMapstructPackagePath(xgGlobalInfo.getOutputMapStructPath() + File.separator + tableObj + "Mapstruct.java");

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
     * 生成代码-点击生成按钮事件
     *
     * @param project 项目
     * @throws IOException io异常
     */
    public void generateCode(Project project) throws IOException {
        System.out.println(JSONUtil.toJsonStr(this.xgGlobalInfo));
        System.out.println(JSONUtil.toJsonStr(this.xgGeneratorTableObjList));

//        Map<String, Object> map = new HashMap<>();
//        Map<String, Object> xgGlobalInfoMap = BeanUtil.beanToMap(this.xgGlobalInfo);
//        map.put("global", xgGlobalInfoMap);
//
//        //默认-生成entity
//        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("template/entity.java.ftl")) {
//            assert resourceAsStream != null;
//            String templateContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
//            Template template = getTemplateFromString(templateContent, "entity");
//            generateEntityCode(template, map);
//        }
    }

    public void generateEntityCode(Template template, Map<String, Object> map) {
        if (xgGlobalInfo.getGenerateEntity()) {
            File entityFile = new File(xgGlobalInfo.getOutputEntityPath());
            if (!entityFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                entityFile.mkdir();
            }

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

    public Template getTemplateFromString(String templateContent, String templateName) throws IOException {
        // 创建 FreeMarker 配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.displayName());

        // 使用 StringReader 将字符串内容转换为 Reader
        StringReader stringReader = new StringReader(templateContent);

        // 创建模板并加载\ templateName 是模板的名称，可以任意指定
        return new Template(templateName, stringReader, cfg);
    }
}
