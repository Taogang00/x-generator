package com.github.xg.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class XGMainDialog extends DialogWrapper {

    private JPanel contentPane;

    private final Project project;

    private XGCodeGeneratorUI codeGeneratorUI;

    public XGMainDialog(Project project) {
        super(project);
        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.project = project;

        this.setTitle("X-代码生成器 0.0.5");
        this.setSize(990, 450);
        this.setResizable(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        codeGeneratorUI = new XGCodeGeneratorUI(project);
        return codeGeneratorUI.getRootJPanel();
    }

    @Override
    protected void doOKAction() {
        // 在这里调用 XGCodeGeneratorUI 中的方法
        if (codeGeneratorUI != null) {
            try {
                codeGeneratorUI.generateCodeAction(project, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected @NonNls @Nullable String getHelpId() {
        return "helpId";
    }

    @Override
    protected Action @NotNull [] createActions() {
        return super.createActions();
    }

}
