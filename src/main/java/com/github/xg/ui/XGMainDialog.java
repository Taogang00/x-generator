package com.github.xg.ui;

import cn.hutool.core.date.DateUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.model.XGGlobalObj;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class XGMainDialog extends DialogWrapper {

    private JPanel rootPanel;

    private boolean currentPageSetting = true;

    private final Project project;

    private final XGCodeUI xgCodeUI;

    private final Action settingAction;

    private final AnActionEvent event;

    public XGMainDialog(Project project, @NotNull AnActionEvent event) {
        super(project);
        this.project = project;
        this.event = event;

        XGGlobalObj xgGlobalObj = new XGGlobalObj();
        xgGlobalObj.setDateTime(DateUtil.formatDateTime(new Date()));
        xgGlobalObj.setFileOverride(false);

        XGConfig.initXGDefaultTemplateManager();

        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.setTitle("X-Generator 0.1.4");
        this.setSize(1000, 690);
        this.setAutoAdjustable(true);

        xgCodeUI = new XGCodeUI(project, xgGlobalObj);
        rootPanel.add(xgCodeUI.getRootJPanel());

        settingAction = new DialogWrapperAction("设置") {
            @Override
            protected void doAction(ActionEvent e) {
                rootPanel.removeAll();
                if (currentPageSetting) {
                    XGSettingUI xgSettingUI = new XGSettingUI(project);
                    rootPanel.add(xgSettingUI.getRootJPanel());
                    settingAction.putValue(Action.NAME, "上一步");
                    setOKActionEnabled(false);
                } else {
                    rootPanel.add(xgCodeUI.getRootJPanel());
                    settingAction.putValue(Action.NAME, "设置");
                    setOKActionEnabled(true);
                }
                rootPanel.repaint();//刷新页面，重绘面板
                rootPanel.validate();//使重绘的面板确认生效
                currentPageSetting = !currentPageSetting;
            }
        };
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        // 在这里调用 XGCodeGeneratorUI 中的方法
        try {
            xgCodeUI.generateCodeAction(project, this, event);
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
        return new Action[]{settingAction, getCancelAction(), getOKAction(), getHelpAction()};
    }
}
