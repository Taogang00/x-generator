package com.github.xg.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

/**
 * 通知工具类
 */
public class XGNotifyUtil {

    /**
     * 通知成功
     *
     * @param content 内容
     * @param title   标题
     * @param project 项目
     */
    public static void notifySuccess(String content, String title, Project project) {
        Notification notification = new Notification(
                "NotificationXg",
                title,
                content,
                NotificationType.INFORMATION
        );
        // 在屏幕右下角显示通知
        Notifications.Bus.notify(notification, project);
    }
}
