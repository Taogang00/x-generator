package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGTabInfo;
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
    private JPanel rootJPanel;
    private JPanel templateList;
    private JPanel templateEditorJPanel;
    private JTabbedPane tabbedPane1;
    private JList<String> xgTabInfoList;
    private JComboBox<String> configComboBox;
    private JCheckBox setDefaultConfigCheckBox;
    private JTable typeMappingTable;
    private JButton resetButton;
    private JButton addBtn;
    private JButton delBtn;
    private TreeMap<String, String> columnJavaTypeMapping;

    private final Editor templateEditor;
    private final String[] HEADER = {"数据库类型(正则)", "Java类型(全路径名)"};
    private Object[][] TABLE_DATA = {{"Column Type", "Java Type"}};

    public XGSettingUI(Project project, XGCodeUI xgCodeUI) {
        DefaultTableCellRenderer dc = new DefaultTableCellRenderer();
        dc.setHorizontalAlignment(JLabel.CENTER);
        this.typeMappingTable.setDefaultRenderer(Object.class, dc);
        this.typeMappingTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        this.delBtn.setEnabled(false);
        this.resetButton.setIcon(AllIcons.General.Reset);
        this.xgTabInfoList.setBorder(JBUI.Borders.emptyLeft(5));

        //配置的选项
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> valuesList = state.getXgConfigs();
        for (XGConfig config : valuesList) {
            this.configComboBox.addItem(config.getName());
        }
        this.configComboBox.setSelectedIndex(xgCodeUI.getConfigComboBox().getSelectedIndex());

        this.initXGTabInfo((String) configComboBox.getSelectedItem());
        this.initXGTableInfo((String) configComboBox.getSelectedItem());
        this.templateEditorJPanel.setLayout(new GridLayout(1, 1));
        this.templateEditorJPanel.setPreferredSize(new Dimension(550, 600));

        XGConfig xgConfig = XGSettingManager.getSelectXGConfig((String) configComboBox.getSelectedItem());
        this.setDefaultConfigCheckBox.setSelected(xgConfig.getIsDefault());

        List<XGTabInfo> infoList = xgConfig.getXgTabInfoList();
        this.templateEditor = createEditorWithText(project, infoList.get(0).getContent(), "ftl");
        this.templateEditorJPanel.add(templateEditor.getComponent());

        this.typeMappingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delBtn.setEnabled(true);
            }
        });

        this.typeMappingTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            String columnType = typeMappingTable.getValueAt(row, 0).toString();
            String javaType = typeMappingTable.getValueAt(row, 1).toString();
            if (StrUtil.isNotBlank(columnType) && StrUtil.isNotBlank(javaType)) {
                columnJavaTypeMapping.put(columnType, javaType);
                xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);
                XGSettingManager.updateXGConfigs(xgConfig);
            }
        });

        this.addBtn.addActionListener(e -> {
            ColumnMapInputDialog dialog = new ColumnMapInputDialog(this);
            dialog.show();
        });

        this.delBtn.addActionListener(e -> {
            int flag = Messages.showYesNoDialog("确定要删除吗？", "提示", Messages.getQuestionIcon());
            if (0 == flag) {
                int selectedRow = typeMappingTable.getSelectedRow();
                TableModel model = typeMappingTable.getModel();
                if (selectedRow == -1 || ObjectUtil.isNull(model)) {
                    return;
                }
                if (typeMappingTable.isEditing()) {
                    typeMappingTable.getCellEditor().stopCellEditing();
                }
                String columnType = model.getValueAt(selectedRow, 0).toString();

                columnJavaTypeMapping.remove(columnType);
                xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);
                XGSettingManager.updateXGConfigs(xgConfig);

                initXGTableInfo((String) configComboBox.getSelectedItem());
                delBtn.setEnabled(false);
                XGNotifyUtil.notifySuccess("删除成功！", "提示", project);
            }
        });

        this.resetButton.addActionListener(e1 -> {
            Object selectedItem = configComboBox.getSelectedItem();
            if (selectedItem != null) {
                int flag = Messages.showYesNoDialog("确定重置【" + selectedItem + "】模板配置吗？", "提示", AllIcons.General.QuestionDialog);
                if (flag == 0) {
                    XGConfig.resetSelectedConfigXgTabInfo(selectedItem.toString());
                    this.initXGTableInfo(configComboBox.getSelectedItem().toString());

                    Document document = templateEditor.getDocument();
                    document.setReadOnly(false);
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        XGTabInfo tabInfo = XGSettingManager.getSelectXGConfig(selectedItem.toString(), this.xgTabInfoList.getSelectedValue());
                        assert tabInfo != null;
                        String fileName = StrUtil.format("{}{}", tabInfo.getType(), ".ftl");
                        ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                        document.setText(tabInfo.getContent());
                    });
                    XGNotifyUtil.notifySuccess("【" + selectedItem + "】模板重置成功！", "提示", project);
                }
            }
        });

        this.configComboBox.addActionListener(e -> {
            Object selectedItem = configComboBox.getSelectedItem();
            assert selectedItem != null;

            XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
            setDefaultConfigCheckBox.setSelected(selectXGConfig.getIsDefault());
            initXGTabInfo(selectedItem.toString());
            initXGTableInfo(selectedItem.toString());

            Document document = templateEditor.getDocument();
            document.setReadOnly(false);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                XGTabInfo tabInfo = XGSettingManager.getSelectXGConfig(selectXGConfig, this.xgTabInfoList.getSelectedValue());
                assert tabInfo != null;
                String fileName = StrUtil.format("{}{}", tabInfo.getType(), ".ftl");
                ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                document.setText(tabInfo.getContent());
            });
        });

        this.xgTabInfoList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || ObjectUtil.isNull(xgTabInfoList.getSelectedValue())) {
                return;
            }
            Document document = templateEditor.getDocument();
            document.setReadOnly(false);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Object selectedItem = configComboBox.getSelectedItem();
                XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
                XGTabInfo tabInfo = XGSettingManager.getSelectXGConfig(selectXGConfig, this.xgTabInfoList.getSelectedValue());
                assert tabInfo != null;

                String fileName = StrUtil.format("{}{}", tabInfo.getType(), ".ftl");
                ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                document.setText(tabInfo.getContent());
            });
        });

        this.templateEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                Object selectedItem = configComboBox.getSelectedItem();
                List<XGConfig> xgConfigs = state.getXgConfigs();
                for (XGConfig config : xgConfigs) {
                    if (config.getName().equals(selectedItem)) {
                        List<XGTabInfo> xgTabInfos = config.getXgTabInfoList();
                        for (XGTabInfo tabInfo : xgTabInfos) {
                            if (tabInfo.getType().equals(xgTabInfoList.getSelectedValue())) {
                                tabInfo.setContent(event.getDocument().getText());
                            }
                        }
                    }
                }
                state.setXgConfigs(xgConfigs);
                XGSettingManager.getInstance().loadState(state);
            }
        });

        this.setDefaultConfigCheckBox.addActionListener(e -> {
            List<XGConfig> xgConfigs = state.getXgConfigs();
            for (XGConfig config : xgConfigs) {
                config.setIsDefault(false);
                if (config.getName().equals(configComboBox.getSelectedItem())) {
                    config.setIsDefault(true);
                }
            }
            state.setXgConfigs(xgConfigs);
            XGSettingManager.getInstance().loadState(state);
        });
    }

    public Editor createEditorWithText(Project project, String text, String fileSuffix) {
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        PsiFile psiFile = psiFileFactory.createFileFromText(PlainTextLanguage.INSTANCE, text);
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        assert document != null;
        // 获取EditorFactory实例
        EditorFactory editorFactory = EditorFactory.getInstance();
        // // 创建一个Document实例
        // 创建一个Editor实例
        Editor editor = editorFactory.createEditor(document, project);
        // 设置Editor的一些属性
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setGutterIconsShown(true);
        ((EditorEx) editor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, StrUtil.format("demo.{}", fileSuffix)));
        return editor;
    }

    public void initXGTabInfo(String selectedConfigKey) {
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);
        List<XGTabInfo> infoList = xgConfig.getXgTabInfoList();
        infoList.sort(Comparator.comparing(XGTabInfo::getOrderNo));

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(infoList.stream().map(XGTabInfo::getType).toList());

        xgTabInfoList.setModel(model);
        xgTabInfoList.setSelectedIndex(0);
    }

    public void initXGTableInfo(String selectedConfigKey) {
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);
        columnJavaTypeMapping = xgConfig.getColumnJavaTypeMapping();

        TABLE_DATA = new Object[!columnJavaTypeMapping.isEmpty() ? columnJavaTypeMapping.size() : 0][];
        int idx = 0;
        for (Map.Entry<String, String> stringTupleEntry : columnJavaTypeMapping.entrySet()) {
            TABLE_DATA[idx] = new Object[]{stringTupleEntry.getKey(), stringTupleEntry.getValue()};
            idx++;
        }
        typeMappingTable.setModel(getDataModel());
    }

    public void addXGTableInfo(String columnType, String javaType) {
        columnJavaTypeMapping.put(columnType, javaType);
        String selectedConfigKey = (String) configComboBox.getSelectedItem();
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);

        xgConfig.setColumnJavaTypeMapping(columnJavaTypeMapping);
        XGSettingManager.updateXGConfigs(xgConfig);
        initXGTableInfo((String) configComboBox.getSelectedItem());
    }

    @NotNull
    private DefaultTableModel getDataModel() {
        return new DefaultTableModel(TABLE_DATA, HEADER) {
            final boolean[] canEdit = {true, true, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
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
            super(true);
            this.xgSettingUI = xgSettingUI;
            setSize(380, 90);
            setTitle("X-Generator");
            setOKButtonText("添加");
            setCancelButtonText("取消");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 2));

            JLabel columnTypeJLabel = new JLabel("数据库类型(正则)");
            columnTypeJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            columnTypeJTextField = new JTextField();

            JLabel javaTypeJLabel = new JLabel("Java类型(全路径名)");
            javaTypeJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            javaTypeJTextField = new JTextField();

            panel.add(columnTypeJLabel);
            panel.add(columnTypeJTextField);
            panel.add(javaTypeJLabel);
            panel.add(javaTypeJTextField);
            return panel;
        }

        @Override
        protected void doOKAction() {
            String firstValue = columnTypeJTextField.getText();
            String secondValue = javaTypeJTextField.getText();
            if (StrUtil.isBlank(firstValue) && StrUtil.isBlank(secondValue)) {
                Messages.showWarningDialog("请输入表单数据项！", "提示");
                return;
            }
            xgSettingUI.addXGTableInfo(firstValue, secondValue);
            super.doOKAction();
        }
    }
}
