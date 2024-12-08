package com.github.xg.ui;

import cn.hutool.core.date.DateUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGGlobalObj;
import com.github.xg.model.XGTabInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.github.xg.constant.XGConstants.*;
import static com.github.xg.utils.XGTemplateUtil.getTemplateContent;

public class XGMainDialog extends DialogWrapper {

    private JPanel rootPanel;

    private boolean currentPageSetting = true;

    private final Project project;

    private final XGCodeUI xgCodeUI;

    private final Action settingAction;

    public XGMainDialog(Project project) {
        super(project);
        this.project = project;
        XGGlobalObj xgGlobalObj = new XGGlobalObj();
        xgGlobalObj.setDateTime(DateUtil.formatDateTime(new Date()));
        xgGlobalObj.setFileOverride(false);

        XGConfig.  initXGDefaultTemplateManager();

        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.setTitle("X-代码生成器 0.0.5");
        this.setSize(999, 666);
        this.setResizable(false);

        xgCodeUI = new XGCodeUI(project, xgGlobalObj);
        rootPanel.add(xgCodeUI.getRootJPanel());

        settingAction = new DialogWrapperAction("设置") {
            @Override
            protected void doAction(ActionEvent e) {
                rootPanel.removeAll();
                if (currentPageSetting) {
                    XGSettingUI xgSettingUI = new XGSettingUI(project, xgCodeUI);
                    rootPanel.add(xgSettingUI.getRootJPanel());

                    settingAction.putValue(Action.SMALL_ICON, AllIcons.Actions.Exit);
                    settingAction.putValue(Action.NAME, "返回");
                    setOKActionEnabled(false);
                } else {
                    rootPanel.add(xgCodeUI.getRootJPanel());

                    settingAction.putValue(Action.SMALL_ICON, AllIcons.General.GearPlain);
                    settingAction.putValue(Action.NAME, "设置");
                    setOKActionEnabled(true);
                }
                rootPanel.repaint();//刷新页面，重绘面板
                rootPanel.validate();//使重绘的面板确认生效
                currentPageSetting = !currentPageSetting;
            }
        };
        settingAction.putValue(Action.SMALL_ICON, AllIcons.General.GearPlain);
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
            xgCodeUI.generateCodeAction(project, this);
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
        Action helpAction = getHelpAction();
        return new Action[]{settingAction, getOKAction(), getCancelAction(), helpAction};
    }
}
