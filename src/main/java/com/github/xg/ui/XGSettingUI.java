package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.model.XGGlobalObj;
import com.github.xg.model.XGTabInfo;
import com.github.xg.utils.XGFileUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
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
    private JButton backBtn;
    private JTextPane jt;
    private JPanel templateList;
    private JPanel templateEditor;
    private JList<String> list1;
    private Map<String, XGTabInfo> tabMap;

    public XGSettingUI(Project project, XGMainDialog xgMainDialog, XGGlobalObj xgGlobalObj) {
        this.backBtn.setIcon(AllIcons.Actions.Exit);

        // 添加到顶部
        ActionToolbar actionToolbar = toolBar();
        actionToolbar.setTargetComponent(templateList);
        templateList.add(actionToolbar.getComponent(), BorderLayout.NORTH);

        // 设置按钮事件
        backBtn.addActionListener(e -> {
            xgMainDialog.switchPage(0);
        });
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
