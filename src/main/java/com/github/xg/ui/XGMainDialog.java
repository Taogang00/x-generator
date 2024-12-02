package com.github.xg.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class XGMainDialog extends DialogWrapper {

    private JPanel rootPanel;

    private final Project project;

    private final XGCodeUI codeGeneratorUI;

    private final XGSettingUI xgSettingUI;

    private final List<JPanel> containerPanelList = new ArrayList<>();

    public XGMainDialog(Project project) {
        super(project);
        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.project = project;

        this.setTitle("X-代码生成器 0.0.5");
        this.setSize(990, 680);
        this.setResizable(false);

        codeGeneratorUI = new XGCodeUI(project, this);
        xgSettingUI = new XGSettingUI(project, this);
        containerPanelList.add(codeGeneratorUI.getRootJPanel());
        containerPanelList.add(xgSettingUI.getRootJPanel());
        // 默认切换到第一页
        switchPage(0);
        init();
    }

    public void switchPage(int page) {
        super.setOKActionEnabled(page != 1);
        rootPanel.removeAll();
        JPanel jPanel = containerPanelList.get(page);
        rootPanel.add(jPanel);
        rootPanel.repaint();//刷新页面，重绘面板
        rootPanel.validate();//使重绘的面板确认生效
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        // 在这里调用 XGCodeGeneratorUI 中的方法
        try {
            codeGeneratorUI.generateCodeAction(project, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doHelpAction() {
        // 获取 HelpManager 并打开指定的帮助链接
        BrowserUtil.browse(Objects.requireNonNull(getHelpId()));
    }

    @Override
    protected @NonNls @Nullable String getHelpId() {
        return "https://github.com/Taogang00/x-generator";
    }

    @Override
    protected Action @NotNull [] createActions() {
        return super.createActions();
    }

}
