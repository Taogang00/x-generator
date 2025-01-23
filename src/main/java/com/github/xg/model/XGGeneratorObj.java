package com.github.xg.model;

import com.intellij.ui.components.fields.ExpandableTextField;
import lombok.Data;

import javax.swing.*;

/**
 * XGGeneratorObj类用于封装XG生成器中的选项和文本输入组件
 * 它结合了图形用户界面(GUI)的两个元素：复选框和可扩展的文本字段
 * 这种设计允许用户通过勾选复选框来启用或禁用某些功能，并通过文本字段输入自定义信息
 */
@Data
public class XGGeneratorObj {

    /**
     * jCheckBox用于提供一个复选框，允许用户进行二选一的操作
     * 例如，用户可以选择是否启用某个特定的功能或设置
     */
    private JCheckBox jCheckBox;

    /**
     * expandableTextField提供了一个文本输入区域，用户可以在其中输入或粘贴信息
     * 这个字段的设计允许它根据需要扩展，以适应不同长度的输入
     */
    private ExpandableTextField expandableTextField;
}
