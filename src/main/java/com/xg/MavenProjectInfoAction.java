package com.xg;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.List;

@SuppressWarnings("DialogTitleCapitalization")
public class MavenProjectInfoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            // 获取Maven项目管理器
            MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
            List<MavenProject> projects = manager.getProjects();
            NotificationGroupManager groupManager = NotificationGroupManager.getInstance();

            if (CollUtil.isNotEmpty(projects)) {
                JSONArray jsonArray = new JSONArray();
                for (MavenProject mavenProject : projects) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("GroupId", mavenProject.getMavenId().getGroupId());
                    jsonObject.put("ArtifactId", mavenProject.getMavenId().getArtifactId());
                    jsonObject.put("Version", mavenProject.getMavenId().getVersion());
                    jsonObject.put("MavenProject.SourcePath", mavenProject.getSources().get(0));
                    jsonObject.put("MavenProject.ResourcesPath", mavenProject.getResources().get(0).getDirectory());
                    jsonObject.put("MavenProject.DisplayName", mavenProject.getDisplayName());
                    jsonObject.put("MavenProject.Name", mavenProject.getName());
                    jsonObject.put("MavenProject.Directory", mavenProject.getDirectory());
                    jsonObject.put("MavenProject.BuildDirectory", mavenProject.getBuildDirectory());
                    jsonArray.add(jsonObject);
                }
                Notification notification = groupManager.getNotificationGroup("NotificationXg")
                        .createNotification(jsonArray.toJSONString(), MessageType.INFO).setTitle("项目模块信息");
                Notifications.Bus.notify(notification);
            } else {
                Notification notification = groupManager.getNotificationGroup("NotificationXg")
                        .createNotification("暂不支持非Maven项目", MessageType.INFO).setTitle("项目模块信息");
                Notifications.Bus.notify(notification);
            }
        }
    }
}
