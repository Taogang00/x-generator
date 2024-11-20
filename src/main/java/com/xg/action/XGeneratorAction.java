package com.xg.action;

import cn.hutool.core.collection.CollUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.xg.ui.XGMainDialog;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;

public class XGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                // 获取Maven项目管理器
                MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
                if (CollUtil.isNotEmpty(manager.getProjects())) {
                    SwingUtilities.invokeLater(() -> {
                        XGMainDialog generateWin = new XGMainDialog(project);
                        generateWin.show();
                    });
                } else {
                    NotificationGroupManager groupManager = NotificationGroupManager.getInstance();
                    Notification notification = groupManager.getNotificationGroup("NotificationXg")
                            .createNotification("X暂不支持非Maven项目", MessageType.INFO).setTitle("X-Generator");
                    Notifications.Bus.notify(notification, project);
                }
            });
        }
    }
}
