package com.github.xg.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class MultiInputDialog extends DialogWrapper {

    private XGSettingUI xgSettingUI;
    private JTextField field1;
    private JTextField field2;
    String firstValue;
    String secondValue;

    public MultiInputDialog(XGSettingUI xgSettingUI) {
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

        JLabel label1 = new JLabel("数据库类型(正则匹配)");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        field1 = new JTextField();
        JLabel label2 = new JLabel("Java类型");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        field2 = new JTextField();

        panel.add(label1);
        panel.add(field1);
        panel.add(label2);
        panel.add(field2);
        return panel;
    }

    @Override
    protected void doOKAction() {
        // Get the values from the input fields
        String firstValue = field1.getText();
        String secondValue = field2.getText();

        System.out.println("First Value: " + firstValue);
        System.out.println("Second Value: " + secondValue);

        super.doOKAction();
    }
}
