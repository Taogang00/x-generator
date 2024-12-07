package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGTabInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
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
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class XGSettingUI {
    private JPanel rootJPanel;
    private JTabbedPane tabbedPane1;
    private JTextPane jt;
    private JPanel templateList;
    private JPanel templateEditorJPanel;
    private final Editor templateEditor;
    private JList<String> list1;
    private JComboBox<String> configComboBox;
    private JButton 重置Button;
    private JCheckBox setDefaultConfigCheckBox;
    private Map<String, XGTabInfo> tabMap;
    public static Key<Boolean> flexTemplate = Key.create("flexTemplate");

    public XGSettingUI(Project project, XGCodeUI xgCodeUI) {
        list1.setBorder(JBUI.Borders.empty(5));
        //配置的选项
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> valuesList = state.getXgConfigs();
        for (XGConfig config : valuesList) {
            configComboBox.addItem(config.getName());
        }
        configComboBox.setSelectedIndex(xgCodeUI.getConfigComboBox().getSelectedIndex());

        initXGTabInfo((String) xgCodeUI.getConfigComboBox().getSelectedItem());

        // 添加到顶部
        ActionToolbar actionToolbar = toolBar();
        actionToolbar.setTargetComponent(templateList);
        templateList.add(actionToolbar.getComponent(), BorderLayout.NORTH);

        templateEditorJPanel.setLayout(new GridLayout(1, 1));
        templateEditorJPanel.setPreferredSize(new Dimension(550, 600));

        XGConfig xgConfig = XGSettingManager.getSelectXGConfig((String) xgCodeUI.getConfigComboBox().getSelectedItem());
        setDefaultConfigCheckBox.setSelected(xgConfig.getIsDefault());

        List<XGTabInfo> infoList = xgConfig.getXgTabInfoList();
        templateEditor = createEditorWithText(project, infoList.get(0).getContent(), "ftl");
        templateEditorJPanel.add(templateEditor.getComponent());

        configComboBox.addActionListener(e -> {
            Object selectedItem = configComboBox.getSelectedItem();
            XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig((String) selectedItem);
            setDefaultConfigCheckBox.setSelected(selectXGConfig.getIsDefault());
        });

        Document document = templateEditor.getDocument();
        list1.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || ObjectUtil.isNull(list1.getSelectedValue())) {
                return;
            }
            document.setReadOnly(false);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                XGTabInfo xgTabInfo = tabMap.get(list1.getSelectedValue());
                String fileName = StrUtil.format("{}{}", xgTabInfo.getType(), ".vm");
                ((EditorEx) templateEditor).setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, fileName));
                document.setText(xgTabInfo.getContent());
            });
        });

        templateEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                XGTabInfo xgTabInfo = tabMap.get(list1.getSelectedValue());
                xgTabInfo.setContent(event.getDocument().getText());

                List<XGConfig> xgConfigs = state.getXgConfigs();
                for (XGConfig config : xgConfigs) {
                    List<XGTabInfo> xgTabInfoList = config.getXgTabInfoList();
                    for (XGTabInfo tabInfo : xgTabInfoList) {
                        if (tabInfo.getType().equals(xgTabInfo.getType())) {
                            tabInfo.setContent(event.getDocument().getText());
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
        editor.putUserData(flexTemplate, true);
        return editor;
    }

    public void initXGTabInfo(String selectedConfigKey) {
        XGConfig xgConfig = XGSettingManager.getSelectXGConfig(selectedConfigKey);
        List<XGTabInfo> infoList = xgConfig.getXgTabInfoList();
        infoList.sort(Comparator.comparing(XGTabInfo::getOrderNo));

        tabMap = infoList.stream().collect(Collectors.toMap(XGTabInfo::getType, Function.identity()));
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(infoList.stream().map(XGTabInfo::getType).toList());

        list1.setModel(model);
        list1.setSelectedIndex(0);
    }

    private ActionToolbar toolBar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        // 预览
        actionGroup.add(new AnAction("预览", "预览", AllIcons.Actions.ShowCode) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        return ActionManager.getInstance().createActionToolbar("Item Toolbar", actionGroup, true);
    }
}
