package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGTabInfo;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 *
 * @author taogang
 * @date 2024/12/08
 */
@Getter
public class XGSettingUI {
    private JPanel rootJPanel;
    private JTabbedPane tabbedPane1;
    private JPanel templateList;
    private JPanel templateEditorJPanel;
    private JList<String> xgTabInfoList;
    private JComboBox<String> configComboBox;
    private JButton resetButton;
    private JCheckBox setDefaultConfigCheckBox;
    private JTable table1;
    private JButton addBtn;
    private JButton delBtn;
    private Map<String, String> columnJavaTypeMapping;

    private final Editor templateEditor;
    private final String[] HEADER = {"数据类型(正则匹配)", "Java类型"};
    private Object[][] TABLE_DATA = {{"Column Type", "Java Type"}};

    public XGSettingUI(Project project, XGCodeUI xgCodeUI) {
        resetButton.setIcon(AllIcons.General.Reset);
        xgTabInfoList.setBorder(JBUI.Borders.emptyLeft(5));
        //配置的选项
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> valuesList = state.getXgConfigs();
        for (XGConfig config : valuesList) {
            configComboBox.addItem(config.getName());
        }
        configComboBox.setSelectedIndex(xgCodeUI.getConfigComboBox().getSelectedIndex());

        initXGTabInfo((String) xgCodeUI.getConfigComboBox().getSelectedItem());

        initXGTableInfo((String) xgCodeUI.getConfigComboBox().getSelectedItem());

        templateEditorJPanel.setLayout(new GridLayout(1, 1));
        templateEditorJPanel.setPreferredSize(new Dimension(550, 600));

        XGConfig xgConfig = XGSettingManager.getSelectXGConfig((String) xgCodeUI.getConfigComboBox().getSelectedItem());
        setDefaultConfigCheckBox.setSelected(xgConfig.getIsDefault());

        List<XGTabInfo> infoList = xgConfig.getXgTabInfoList();
        templateEditor = createEditorWithText(project, infoList.get(0).getContent(), "ftl");
        templateEditorJPanel.add(templateEditor.getComponent());

        resetButton.addActionListener(e1 -> {
            Object selectedItem = configComboBox.getSelectedItem();
            if (selectedItem != null) {
                int flag = Messages.showYesNoDialog("确定重置【" + selectedItem + "】模板配置吗？", "提示", AllIcons.General.QuestionDialog);
                if (flag == 0) {
                    XGConfig.resetSelectedConfigXgTabInfo(selectedItem.toString());

                    Document document = templateEditor.getDocument();
                    document.setReadOnly(false);
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        XGTabInfo tabInfo = XGSettingManager.getSelectXGConfig(selectedItem.toString(), this.xgTabInfoList.getSelectedValue());
                        assert tabInfo != null;
                        String fileName = StrUtil.format("{}{}", tabInfo.getType(), ".ftl");
                        ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                        document.setText(tabInfo.getContent());
                    });
                }
            }
        });

        configComboBox.addActionListener(e -> {
            Object selectedItem = configComboBox.getSelectedItem();
            assert selectedItem != null;

            XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
            setDefaultConfigCheckBox.setSelected(selectXGConfig.getIsDefault());
            initXGTabInfo(selectedItem.toString());

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

        xgTabInfoList.addListSelectionListener(e -> {
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

        templateEditor.getDocument().addDocumentListener(new DocumentListener() {
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

        setDefaultConfigCheckBox.addActionListener(e -> {
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

        table1.setModel(getDataModel());
    }

    @NotNull
    private DefaultTableModel getDataModel() {
        return new DefaultTableModel(TABLE_DATA, HEADER) {
            boolean[] canEdit = {true, true, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
    }

    private void setColumnInput(Project project) {
        TableColumn comboBoxColumn = table1.getColumnModel().getColumn(1);
        TableColumn type = table1.getColumnModel().getColumn(0);

        ExtendableTextField textField = new ExtendableTextField();

        comboBoxColumn.setCellEditor(new DefaultCellEditor(textField));
    }
}
