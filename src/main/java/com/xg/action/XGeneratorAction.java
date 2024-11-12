package com.xg.action;

import cn.hutool.core.collection.CollUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.xg.window.ErStudioCodeGeneratorDialog;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;

public class XGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            // 获取Maven项目管理器
            MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
            if (CollUtil.isNotEmpty(manager.getProjects())) {
                SwingUtilities.invokeLater(() -> {
                    ErStudioCodeGeneratorDialog generateWin = new ErStudioCodeGeneratorDialog(e);
                    generateWin.setVisible(true);
                });
            } else {
                NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
                Notification notification = groupManager.getNotificationGroup("NotificationXg")
                        .createNotification("暂不支持非Maven项目", MessageType.INFO).setTitle("项目模块信息");
                Notifications.Bus.notify(notification, project);
            }
        }
    }
}
