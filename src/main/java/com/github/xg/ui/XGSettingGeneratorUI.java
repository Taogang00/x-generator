package com.github.xg.ui;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xg.persistent.XGGeneratorSetting;
import com.github.xg.utils.XGFileUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;

import javax.swing.*;

@Getter
public class XGSettingGeneratorUI {
    private JPanel rootJPanel;
    private JTabbedPane tabbedPane1;
    private JButton backBtn;
    private JButton importBtn;
    private JButton exportBtn;
    private JButton resetBtn;
    private JTextPane jt;

    public XGSettingGeneratorUI(Project project, XGMainDialog xgMainDialog) {
        this.backBtn.setIcon(AllIcons.Actions.Back);

        // 设置按钮事件
        backBtn.addActionListener(e -> {
            xgMainDialog.switchPage(0);
        });

        // 导入配置
        importBtn.addActionListener(e -> {
            VirtualFile virtualFile = XGFileUtil.chooseFileVirtual(project);
            if (ObjectUtil.isNull(virtualFile)) {
                return;
            }
            String path = virtualFile.getPath();
            XGGeneratorSetting.importConfig(path);
        });

        // 导出配置
        exportBtn.addActionListener(e -> {
            String exportPath = XGFileUtil.chooseDirectory(project);
            if (StrUtil.isEmpty(exportPath)) {
                return;
            }
            XGGeneratorSetting.export(exportPath);
        });
    }
}
