package com.xg.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class XGMainDialog extends DialogWrapper {

    private JPanel contentPane;

    private Project project;

    public XGMainDialog(Project project) {
        super(true); // 使用当前窗口作为父窗口
        this.project = project;

        this.setTitle("X-代码生成器 2024001");
        this.setSize(900, 400);
        this.setResizable(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel root = new XGCodeGeneratorUI(project).getRootJPanel();
        root.setMaximumSize(new Dimension(900, 400));
        return root;
    }
}
