package com.xg.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class XGMainDialog extends DialogWrapper {

    private JPanel contentPane;

    private final Project project;

    public XGMainDialog(Project project) {
        super(true); // 使用当前窗口作为父窗口
        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.project = project;

        this.setTitle("X-代码生成器 0.0.1");
        this.setSize(900, 400);
        this.setResizable(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new XGCodeGeneratorUI(project).getRootJPanel();
    }
}
