package com.taogang.xg;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.List;

public class MavenProjectInfoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            // 获取Maven项目管理器
            MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

            // 获取所有Maven项目并提取模块名称
            List<MavenProject> projects = manager.getProjects();
            for (MavenProject mavenProject : projects) {
                String stringBuffer = "GroupId:" + mavenProject.getMavenId().getGroupId() + " | " +
                        "ArtifactId:" + mavenProject.getMavenId().getArtifactId() + " | " +
                        "Version:" + mavenProject.getMavenId().getVersion() + " | " +
                        "MavenProject.SourcePath:" + mavenProject.getSources().get(0) + " | " +
                        "MavenProject.ResourcesPath:" + mavenProject.getResources().get(0) + " | " +
                        "MavenProject.Name:" + mavenProject.getName() + " | " +
                        "MavenProject.DisplayName:" + mavenProject.getDisplayName();
                System.out.println(stringBuffer);
            }
        }
    }
}
