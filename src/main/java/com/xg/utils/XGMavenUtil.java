package com.xg.utils;

import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * maven程序工具类
 *
 * @author taogang
 * @date 2024/11/14
 */
public class XGMavenUtil {

    /**
     * 获取 Maven 项目列表
     *
     * @param project 项目
     * @return {@link List }<{@link MavenProject }>
     */
    public static List<MavenProject> getMavenProjectList(Project project) {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        return manager.getProjects();
    }

    /**
     * 获取项目所有的Maven ArtifactId
     *
     * @param project 项目
     * @return {@link List }<{@link String }>
     */
    public static List<String> getMavenArtifactId(Project project) {
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        List<MavenProject> projects = manager.getProjects();
        List<MavenId> mavenIdList = projects.stream().map(MavenProject::getMavenId).toList();
        return mavenIdList.stream().map(MavenId::getArtifactId).collect(Collectors.toList());
    }

    /**
     * 获取项目指定的Maven的ArtifactId的源码路径
     *
     * @param project    项目
     * @param artifactId 项目 ID
     * @return {@link File }
     */
    public static File getMavenArtifactIdSourcePath(Project project, String artifactId) {
        List<MavenProject> mavenProjectList = getMavenProjectList(project);
        for (MavenProject mavenProject : mavenProjectList) {
            if (Objects.equals(mavenProject.getMavenId().getArtifactId(), artifactId)) {
                return new File(mavenProject.getSources().get(0));
            }
        }
        return null;
    }

    /**
     * 获取项目指定的Maven的ArtifactId的资源路径
     *
     * @param project    项目
     * @param artifactId 项目 ID
     * @return {@link File }
     */
    public static File getMavenArtifactIdResourcePath(Project project, String artifactId) {
        List<MavenProject> mavenProjectList = getMavenProjectList(project);
        for (MavenProject mavenProject : mavenProjectList) {
            if (Objects.equals(mavenProject.getMavenId().getArtifactId(), artifactId)) {
                return new File(mavenProject.getResources().get(0).getDirectory());
            }
        }
        return null;
    }
}
