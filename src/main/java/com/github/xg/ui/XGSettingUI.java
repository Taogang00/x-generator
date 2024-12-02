package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.model.XGTabInfo;
import com.github.xg.persistent.XGSettingManager;
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
    private JButton importBtn;
    private JButton exportBtn;
    private JButton resetBtn;
    private JTextPane jt;
    private JPanel templateList;
    private JPanel templateEditor;
    private JList<String> list1;
    Map<String, XGTabInfo> tabMap;

    public XGSettingUI(Project project, XGMainDialog xgMainDialog) {
        this.backBtn.setIcon(AllIcons.Actions.Exit);

        // 添加到顶部
        ActionToolbar actionToolbar = toolBar();
        actionToolbar.setTargetComponent(templateList);
        templateList.add(actionToolbar.getComponent(), BorderLayout.NORTH);

        List<XGTabInfo> infoList = XGConfig.getTabList();
        infoList.sort(Comparator.comparing(XGTabInfo::getOrderNo));

        tabMap = infoList.stream().collect(Collectors.toMap(XGTabInfo::getTitle, Function.identity()));
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(infoList.stream().map(XGTabInfo::getTitle).toList());

        list1.setModel(model);
        list1.setSelectedIndex(0);

        // 设置按钮事件
        backBtn.addActionListener(e -> {
            xgMainDialog.switchPage(0);
        });

        // 导入配置
        importBtn.addActionListener(e -> {
            String exportPath = XGFileUtil.chooseDirectory(project);
            if (ObjectUtil.isNull(exportPath)) {
                return;
            }
            XGSettingManager.importConfig(exportPath);
        });

        // 导出配置
        exportBtn.addActionListener(e -> {
            String exportPath = XGFileUtil.chooseDirectory(project);
            if (StrUtil.isEmpty(exportPath)) {
                return;
            }
            XGSettingManager.export(exportPath);
        });
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
