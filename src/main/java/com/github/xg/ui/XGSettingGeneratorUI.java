package com.github.xg.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import lombok.Getter;

import javax.swing.*;

@Getter
public class XGSettingGeneratorUI {
    private JTextField textField1;
    private JPanel rootJPanel;
    private JButton backBtn;

    public XGSettingGeneratorUI(Project project, XGMainDialog xgMainDialog) {
        this.backBtn.setIcon(AllIcons.Actions.Exit);
        // 设置按钮事件
        backBtn.addActionListener(e -> {
            xgMainDialog.switchPage(0);
        });
    }
}
