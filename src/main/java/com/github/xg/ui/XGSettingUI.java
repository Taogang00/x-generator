package com.github.xg.ui;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGTempItem;
import com.github.xg.utils.XGFileUtil;
import com.github.xg.utils.XGNotifyUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 设置页面
 *
 * @author taogang
 * @date 2024/12/08
 */
@SuppressWarnings("DialogTitleCapitalization")
@Getter
public class XGSettingUI {
    // 声明根面板
    private JPanel rootJPanel;

    // 声明模板编辑器面板
    private JPanel templateEditorJPanel;

    // 声明标签页面板
    private JTabbedPane tabbedPane1;

    // 声明列表组件，用于显示信息列表
    private JList<String> xgTempItemList;

    // 声明组合框组件，用于选择配置
    private JComboBox<String> configComboBox;

    // 声明复选框组件，用于设置默认配置
    private JCheckBox setDefaultConfigCheckBox;

    // 声明表格组件，用于显示类型映射
    private JTable typeMappingTable;

    // 声明重置按钮
    private JButton resetButton;

    // 声明添加按钮
    private JButton addBtn;

    // 声明删除按钮
    private JButton delBtn;

    // 声明xml按钮
    private JButton xmlButton;

    // 声明一个TreeMap，用于存储列名和Java类型的映射关系
    private TreeMap<String, String> columnJavaTypeMapping;

    // 声明一个名为templateEditor的私有最终成员变量，类型为Editor
    private final Editor templateEditor;

    // 声明一个名为HEADER的私有最终成员变量，类型为String数组
    // 该数组包含两个元素："数据库类型(正则)"和"Java类型(全路径名)"
    private final String[] HEADER = {"数据库类型(正则)", "Java类型(全路径名)"};

    // 声明一个名为TABLE_DATA的私有成员变量，类型为Object二维数组
    // 该数组初始化为包含一个元素，元素为{"Column Type", "Java Type"}
    private Object[][] TABLE_DATA = {{"Column Type", "Java Type"}};

    public XGSettingUI(Project project, XGCodeUI xgCodeUI) {
        // 设置表格单元格渲染器居中对齐
        DefaultTableCellRenderer dc = new DefaultTableCellRenderer();
        dc.setHorizontalAlignment(JLabel.CENTER);
        this.typeMappingTable.setDefaultRenderer(Object.class, dc);
        // 设置表格头部字体
        this.typeMappingTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        // 禁用删除按钮
        this.delBtn.setEnabled(false);
        // 设置按钮图标
        this.resetButton.setIcon(AllIcons.Actions.ForceRefresh);
        this.addBtn.setIcon(AllIcons.Actions.AddList);
        this.delBtn.setIcon(AllIcons.Actions.GC);
        this.xmlButton.setIcon(AllIcons.FileTypes.Xml);
        // 设置下拉框边框
        this.xgTempItemList.setBorder(JBUI.Borders.emptyLeft(5));

        // 配置的选项
        // 获取配置状态
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        // 获取所有配置列表
        List<XGConfig> valuesList = state.getXgConfigs();
        // 遍历配置列表，添加到下拉框中
        for (XGConfig config : valuesList) {
            this.configComboBox.addItem(config.getName());
        }
        // 设置下拉框选中项
        this.configComboBox.setSelectedIndex(xgCodeUI.getConfigComboBox().getSelectedIndex());

        // 初始化XGTabInfo
        this.initXGTabInfo((String) configComboBox.getSelectedItem());
        // 初始化XGTableInfo
        this.initXGTableInfo((String) configComboBox.getSelectedItem());
        // 设置模板编辑器面板布局和大小
        this.templateEditorJPanel.setLayout(new GridLayout(1, 1));
        this.templateEditorJPanel.setPreferredSize(new Dimension(550, 600));

        // 获取当前选中的配置
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig((String) configComboBox.getSelectedItem());
        // 设置默认配置复选框选中状态
        this.setDefaultConfigCheckBox.setSelected(xgConfig.getIsDefault());

        // 获取当前配置下的所有XGTabInfo信息
        List<XGTempItem> infoList = xgConfig.getXgTempItemList();
        // 创建编辑器并设置内容
        this.templateEditor = createEditorWithText(project, infoList.get(0).getContent(), "ftl");
        // 将编辑器组件添加到模板编辑器面板中
        this.templateEditorJPanel.add(templateEditor.getComponent());

        // 为表格添加鼠标点击监听器
        this.typeMappingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 点击表格时启用删除按钮
                delBtn.setEnabled(true);
            }
        });

        // 为表格模型添加监听器
        this.typeMappingTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            String columnType = typeMappingTable.getValueAt(row, 0).toString();
            String javaType = typeMappingTable.getValueAt(row, 1).toString();
            // 当表格内容不为空时更新配置和映射关系
            if (StrUtil.isNotBlank(columnType) && StrUtil.isNotBlank(javaType)) {
                columnJavaTypeMapping.put(columnType, javaType);
                xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);
                XGSettingManager.updateXGConfigs(xgConfig);
            }
        });

        // 为添加按钮添加点击事件监听器
        this.addBtn.addActionListener(e -> {
            // 创建并显示输入对话框
            ColumnMapInputDialog dialog = new ColumnMapInputDialog(this);
            dialog.show();
        });

        // 为删除按钮添加点击事件监听器
        this.delBtn.addActionListener(e -> {
            // 弹出确认删除对话框
            int flag = Messages.showYesNoDialog("确定要删除吗？", "X-Generator", Messages.getQuestionIcon());
            if (0 == flag) {
                int selectedRow = typeMappingTable.getSelectedRow();
                TableModel model = typeMappingTable.getModel();
                // 检查是否选中有效行且模型不为空
                if (selectedRow == -1 || ObjectUtil.isNull(model)) {
                    return;
                }
                // 停止单元格编辑
                if (typeMappingTable.isEditing()) {
                    typeMappingTable.getCellEditor().stopCellEditing();
                }
                String columnType = model.getValueAt(selectedRow, 0).toString();

                // 从映射关系中移除该列类型
                columnJavaTypeMapping.remove(columnType);
                // 更新配置中的映射关系
                xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);
                // 更新配置
                XGSettingManager.updateXGConfigs(xgConfig);

                // 重新初始化XGTableInfo
                initXGTableInfo((String) configComboBox.getSelectedItem());
                // 禁用删除按钮
                delBtn.setEnabled(false);
                // 弹出删除成功提示
                XGNotifyUtil.notifySuccess("删除成功！", "X-Generator", project);
            }
        });

        // 导出配置
        this.xmlButton.addActionListener(e -> {
            String exportPath = XGFileUtil.chooseDirectory(project);
            if (StrUtil.isEmpty(exportPath)) {
                return;
            }
            InputStream resourceAsStream = XGSettingManager.class.getResourceAsStream("/import/import.xml");
            FileUtil.writeFromStream(resourceAsStream, new File(exportPath + File.separator + "import.xml"), true);
            XGNotifyUtil.notifySuccess("导出成功，请到选择的目录查看", "X-Generator", project);
        });

        // 为重置按钮添加点击事件监听器
        this.resetButton.addActionListener(e1 -> {
            Object selectedItem = configComboBox.getSelectedItem();
            if (selectedItem != null) {
                // 弹出确认重置对话框
                int flag = Messages.showYesNoDialog("确定重置【" + selectedItem + "】模板配置吗？", "X-Generator", AllIcons.General.QuestionDialog);
                if (flag == 0) {
                    // 重置选中的配置模板信息
                    XGConfig.resetSelectedConfigXgTabInfo(selectedItem.toString());
                    // 重新初始化XGTableInfo
                    this.initXGTableInfo(configComboBox.getSelectedItem().toString());

                    // 获取编辑器文档对象
                    Document document = templateEditor.getDocument();
                    // 设置文档可编辑
                    document.setReadOnly(false);
                    // 执行写操作
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        // 获取选中的XGTabInfo信息
                        XGTempItem tabInfo = XGSettingManager.getSelectXGConfig(selectedItem.toString(), this.xgTempItemList.getSelectedValue());
                        assert tabInfo != null;
                        // 构造文件名
                        String fileName = StrUtil.format("{}{}", tabInfo.getName(), ".ftl");
                        // 设置编辑器高亮器
                        ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                        // 设置编辑器文本内容
                        document.setText(tabInfo.getContent());
                    });
                    // 弹出重置成功提示
                    XGNotifyUtil.notifySuccess("【" + selectedItem + "】模板重置成功！", "X-Generator", project);
                }
            }
        });

        // 为下拉框添加选择事件监听器
        this.configComboBox.addActionListener(e -> {
            Object selectedItem = configComboBox.getSelectedItem();
            assert selectedItem != null;

            // 获取选中的配置信息
            XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
            // 设置默认配置复选框选中状态
            setDefaultConfigCheckBox.setSelected(selectXGConfig.getIsDefault());
            // 重新初始化XGTabInfo
            initXGTabInfo(selectedItem.toString());
            // 重新初始化XGTableInfo
            initXGTableInfo(selectedItem.toString());
            // 获取编辑器文档对象
            Document document = templateEditor.getDocument();
            // 设置文档可编辑
            document.setReadOnly(false);
            // 执行写操作
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // 获取选中的XGTabInfo信息
                XGTempItem tabInfo = XGSettingManager.getSelectXGConfig(selectXGConfig, this.xgTempItemList.getSelectedValue());
                assert tabInfo != null;
                // 构造文件名
                String fileName = StrUtil.format("{}{}", tabInfo.getName(), ".ftl");
                // 设置编辑器高亮器
                ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                // 设置编辑器文本内容
                document.setText(tabInfo.getContent());
            });
        });

        // 为下拉框添加列表选择监听器
        this.xgTempItemList.addListSelectionListener(e -> {
            // 检查是否正在调整值或选中的值为空
            if (e.getValueIsAdjusting() || ObjectUtil.isNull(xgTempItemList.getSelectedValue())) {
                return;
            }
            // 获取编辑器文档对象
            Document document = templateEditor.getDocument();
            // 设置文档可编辑
            document.setReadOnly(false);
            // 执行写操作
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // 获取下拉框选中的项
                Object selectedItem = configComboBox.getSelectedItem();
                // 获取选中的配置信息
                XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
                // 获取选中的XGTabInfo信息
                XGTempItem tabInfo = XGSettingManager.getSelectXGConfig(selectXGConfig, this.xgTempItemList.getSelectedValue());
                assert tabInfo != null;

                // 构造文件名
                String fileName = StrUtil.format("{}{}", tabInfo.getName(), ".ftl");
                // 设置编辑器高亮器
                ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                // 设置编辑器文本内容
                document.setText(tabInfo.getContent());
            });
        });

        // 为编辑器文档添加文档监听器
        this.templateEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                // 获取当前选中的配置项
                Object selectedItem = configComboBox.getSelectedItem();
                // 获取所有的XGConfig配置
                List<XGConfig> xgConfigs = state.getXgConfigs();
                // 遍历所有的XGConfig配置
                for (XGConfig config : xgConfigs) {
                    // 如果配置项的名称与选中的项匹配
                    if (config.getName().equals(selectedItem)) {
                        // 获取该配置项下的所有XGTabInfo信息
                        List<XGTempItem> xgTempItems = config.getXgTempItemList();
                        // 遍历所有的XGTabInfo信息
                        for (XGTempItem tabInfo : xgTempItems) {
                            // 如果XGTabInfo的类型与选中的类型匹配
                            if (tabInfo.getName().equals(xgTempItemList.getSelectedValue())) {
                                // 设置XGTabInfo的内容为当前文档的内容
                                tabInfo.setContent(event.getDocument().getText());
                            }
                        }
                    }
                }
                // 更新state中的XGConfig配置
                state.setXgConfigs(xgConfigs);
                // 加载更新后的state
                XGSettingManager.getInstance().loadState(state);
            }

        });

        // 为默认配置复选框添加点击事件监听器
        this.setDefaultConfigCheckBox.addActionListener(e -> {
            // 获取所有配置列表
            List<XGConfig> xgConfigs = state.getXgConfigs();
            // 遍历配置列表，设置默认配置为false，当前选中的配置为true
            for (XGConfig config : xgConfigs) {
                config.setIsDefault(false);
                if (config.getName().equals(configComboBox.getSelectedItem())) {
                    config.setIsDefault(true);
                }
            }
            // 更新配置列表
            state.setXgConfigs(xgConfigs);
            // 加载更新后的状态
            XGSettingManager.getInstance().loadState(state);
        });
    }

    public Editor createEditorWithText(Project project, String text, String fileSuffix) {
        // 获取PsiFileFactory实例
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        // 创建一个PsiFile对象，内容为传入的text
        PsiFile psiFile = psiFileFactory.createFileFromText(PlainTextLanguage.INSTANCE, text);
        // 获取PsiFile对应的Document对象
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        assert document != null;
        // 获取EditorFactory实例
        EditorFactory editorFactory = EditorFactory.getInstance();
        // 创建一个Editor实例，传入Document对象和Project对象
        Editor editor = editorFactory.createEditor(document, project);
        // 设置Editor的一些属性
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false); // 关闭虚拟空间
        editorSettings.setLineMarkerAreaShown(false); // 不显示行标记区域
        editorSettings.setLineNumbersShown(true); // 显示行号
        editorSettings.setFoldingOutlineShown(true); // 显示折叠大纲
        editorSettings.setGutterIconsShown(true); // 显示gutter图标
        // 设置Editor的高亮器，根据fileSuffix动态生成高亮器名称
        ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, StrUtil.format("demo.{}", fileSuffix)));
        return editor;
    }

    public void initXGTabInfo(String selectedConfigKey) {
        // 根据传入的配置键获取对应的XGConfig对象
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);

        // 获取XGConfig对象中所有的XGTabInfo列表
        List<XGTempItem> infoList = xgConfig.getXgTempItemList();

        // 根据XGTabInfo的OrderNo属性对列表进行排序
        infoList.sort(Comparator.comparing(XGTempItem::getOrderNo));

        // 创建一个DefaultListModel对象
        DefaultListModel<String> model = new DefaultListModel<>();

        // 将XGTabInfo列表中每个元素的Type属性添加到模型中
        model.addAll(infoList.stream().map(XGTempItem::getName).toList());

        // 设置下拉列表框的模型为上面创建的模型
        xgTempItemList.setModel(model);

        // 设置下拉列表框的选中索引为0，即默认选中第一个选项
        xgTempItemList.setSelectedIndex(0);
    }

    public void initXGTableInfo(String selectedConfigKey) {
        // 根据选定的配置键获取对应的XGConfig对象
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);

        // 获取列名与Java类型的映射关系
        columnJavaTypeMapping = xgConfig.getColumnJavaTypeMapping();

        // 根据映射关系初始化表格数据
        // 如果映射关系不为空，则表格数据的行数等于映射关系的大小；否则，表格数据为空数组
        TABLE_DATA = new Object[!columnJavaTypeMapping.isEmpty() ? columnJavaTypeMapping.size() : 0][];

        // 初始化索引变量
        int idx = 0;

        // 遍历映射关系，填充表格数据
        for (Map.Entry<String, String> stringTupleEntry : columnJavaTypeMapping.entrySet()) {
            // 将每个映射关系作为一个对象数组放入表格数据中
            TABLE_DATA[idx] = new Object[]{stringTupleEntry.getKey(), stringTupleEntry.getValue()};
            // 索引自增
            idx++;
        }

        // 设置表格模型
        typeMappingTable.setModel(getDataModel());
    }

    public void addXGTableInfo(String columnType, String javaType) {
        // 将列类型和Java类型添加到映射关系中
        columnJavaTypeMapping.put(columnType, javaType);

        // 获取当前选中的配置键
        String selectedConfigKey = (String) configComboBox.getSelectedItem();

        // 根据选中的配置键获取对应的XGConfig对象
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);

        // 更新XGConfig对象的列名和Java类型的映射关系
        xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);

        // 更新XGConfig配置
        XGSettingManager.updateXGConfigs(xgConfig);

        // 重新初始化XGTableInfo
        initXGTableInfo((String) configComboBox.getSelectedItem());
    }

    @NotNull
    private DefaultTableModel getDataModel() {
        // 创建一个新的DefaultTableModel对象，并传入表格数据和表头
        return new DefaultTableModel(TABLE_DATA, HEADER) {
            // 定义一个布尔数组，表示每一列是否可以编辑
            final boolean[] canEdit = {true, true, true};

            // 重写isCellEditable方法，根据列索引确定单元格是否可编辑
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // 返回对应列的canEdit值
                return canEdit[columnIndex];
            }
        };
    }

    @SuppressWarnings("DialogTitleCapitalization")
    public static class ColumnMapInputDialog extends DialogWrapper {

        private final XGSettingUI xgSettingUI;

        private JTextField columnTypeJTextField;

        private JTextField javaTypeJTextField;

        public ColumnMapInputDialog(XGSettingUI xgSettingUI) {
            // 调用父类的构造函数，设置对话框为模态对话框
            super(true);

            // 将传入的XGSettingUI对象赋值给成员变量xgSettingUI
            this.xgSettingUI = xgSettingUI;

            // 设置对话框的大小
            setSize(380, 90);

            // 设置对话框的标题
            setTitle("X-Generator");

            // 设置“确定”按钮的文本
            setOKButtonText("添加");

            // 设置“取消”按钮的文本
            setCancelButtonText("取消");

            // 初始化对话框
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            // 创建一个JPanel作为中心面板
            JPanel panel = new JPanel();
            // 设置面板的布局为2行2列的网格布局
            panel.setLayout(new GridLayout(2, 2));

            // 创建一个JLabel，显示“数据库类型(正则)”
            JLabel columnTypeJLabel = new JLabel("数据库类型(正则)");
            // 设置JLabel的水平对齐方式为居中
            columnTypeJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            // 创建一个JTextField，用于输入数据库类型
            columnTypeJTextField = new JTextField();

            // 创建一个JLabel，显示“Java类型(全路径名)”
            JLabel javaTypeJLabel = new JLabel("Java类型(全路径名)");
            // 设置JLabel的水平对齐方式为居中
            javaTypeJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            // 创建一个JTextField，用于输入Java类型
            javaTypeJTextField = new JTextField();

            // 将JLabel和JTextField添加到面板中
            panel.add(columnTypeJLabel);
            panel.add(columnTypeJTextField);
            panel.add(javaTypeJLabel);
            panel.add(javaTypeJTextField);
            return panel;
        }

        @Override
        protected void doOKAction() {
            // 获取第一个输入框（列类型）中的文本
            String firstValue = columnTypeJTextField.getText();
            // 获取第二个输入框（Java类型）中的文本
            String secondValue = javaTypeJTextField.getText();

            // 如果两个输入框都为空
            if (StrUtil.isBlank(firstValue) && StrUtil.isBlank(secondValue)) {
                // 显示警告对话框，提示用户输入表单数据项
                Messages.showWarningDialog("请输入表单数据项！", "X-Generator");
                // 退出方法
                return;
            }

            // 调用xgSettingUI的addXGTableInfo方法，将输入的数据添加到XGTableInfo中
            xgSettingUI.addXGTableInfo(firstValue, secondValue);

            // 调用父类的doOKAction方法
            super.doOKAction();
        }

    }
}
