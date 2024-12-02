package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.persistent.XGSettingManager;
import com.github.xg.utils.XGFileUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import lombok.Getter;

import javax.swing.*;

@Getter
public class XGSettingUI {
    private JPanel rootJPanel;
    private JTabbedPane tabbedPane1;
    private JButton backBtn;
    private JButton importBtn;
    private JButton exportBtn;
    private JButton resetBtn;
    private JTextPane jt;

    public XGSettingUI(Project project, XGMainDialog xgMainDialog) {
        this.backBtn.setIcon(AllIcons.Actions.Back);

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
}
